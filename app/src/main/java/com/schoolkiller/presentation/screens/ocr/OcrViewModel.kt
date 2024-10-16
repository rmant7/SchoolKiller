package com.schoolkiller.presentation.screens.ocr

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.network.api.GeminiApiService
import com.schoolkiller.data.network.response.GeminiResponse
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.usecases.api.ExtractGeminiResponseUseCase
import com.schoolkiller.domain.usecases.api.GetImageByteArrayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val getImageByteArrayUseCase: GetImageByteArrayUseCase,
    private val extractGeminiResponseUseCase: ExtractGeminiResponseUseCase,
) : ViewModel() {

    private val maxOcrRequests = 3

    private val _recognizedTextList: MutableStateFlow<MutableList<String>> =
        MutableStateFlow(mutableListOf())
    val recognizedTextList: MutableStateFlow<MutableList<String>> = _recognizedTextList

    fun updateRecognizedTextList(newList: MutableList<String>) {
        _recognizedTextList.value = newList
    }

    fun updateRecognizedText(index: Int, recognizedText: String) {
        // index out of bounds check
        if (_recognizedTextList.value.size <= index)
            _recognizedTextList.value.add(recognizedText)
        else
            _recognizedTextList.value[index] = recognizedText
    }

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String?> = _recognizedText

    fun updateRecognizedText(recognizedText: String) {
        _recognizedText.update { recognizedText }
    }

    // a pair of error title and error message
    private val _ocrError = MutableStateFlow<Throwable?>(null)
    val ocrError: StateFlow<Throwable?> = _ocrError

    fun updateOcrError(ocrError: Throwable?) {
        _ocrError.update { ocrError }
    }

    private suspend fun fetchResponse(
        actualFileUri: String,
        systemInstruction: String
    ): String {

        val content = geminiApiService.generateContent(
            actualFileUri,
            PromptText.OCR_PROMPT.promptText,
            systemInstruction
        )

        val textResponse = if (content is GeminiResponse.Success) {
            extractGeminiResponseUseCase.invoke(content.data ?: "{}")
        } else {
            // updateOcrError(RuntimeException())
            content.message
        }
        if (!textResponse.isNullOrEmpty())
            return textResponse
        else {
            // updateOcrError(RuntimeException())
            return ""
        }
    }

    fun geminiImageToText(
        imageUri: Uri,
        fileName: String,
        useHtml: Boolean
    ) = viewModelScope.launch {

        val fileByteArray = getImageByteArrayUseCase.invoke(imageUri = imageUri)
        val uploadResult = geminiApiService.uploadFileWithProgress(
            fileByteArray,
            fileName
        )

        uploadResult.onSuccess { uploadModel ->
            val fileUriResult = geminiApiService.uploadFileBytes(
                uploadModel.uploadUrl,
                fileByteArray
            )

            fileUriResult.onSuccess { fileUriJson ->
                val actualFileUri = Json.parseToJsonElement(fileUriJson)
                    .jsonObject["file"]?.jsonObject?.get("uri")?.jsonPrimitive?.content

                if (actualFileUri != null) {
                    /** For tests */
                    val systemInstruction =
                        if (useHtml) PromptText.HTML_OCR_SYSTEM_INSTRUCTION.promptText
                        else PromptText.NO_HTML_OCR_SYSTEM_INSTRUCTION.promptText


                    val list = ArrayList<String>()
                    repeat(maxOcrRequests) {
                        // async calls
                        launch {
                            val response = fetchResponse(
                                actualFileUri,
                                systemInstruction,
                            )
                            list.add(response)
                            if (list.size == maxOcrRequests) {
                                updateRecognizedTextList(list)
                                updateRecognizedText(recognizedTextList.value[0])
                            }
                        }
                    }

                } else {
                    updateOcrError(RuntimeException())
                    updateRecognizedText("")
                }

            }

            fileUriResult.onFailure { updateOcrError(it) }
        }
        uploadResult.onFailure { updateOcrError(it) }
    }

}
