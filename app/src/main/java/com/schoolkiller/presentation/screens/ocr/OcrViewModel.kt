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

    // if number of candidates can be more than 1


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

    private val _ocrError = MutableStateFlow<Throwable?>(null)
    val ocrError: StateFlow<Throwable?> = _ocrError

    fun updateOcrError(ocrError: Throwable?) {
        _ocrError.update { ocrError }
    }

    fun geminiImageToText(
        imageUri: Uri,
        fileName: String,
        textOnExtractionError: String,
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
                        if (useHtml) PromptText.HTML_REQUEST.promptText
                        else PromptText.NO_HTML_REQUEST.promptText
                    systemInstruction.plus(PromptText.OCR_SYSTEM_INSTRUCTION.promptText)

                    val content = geminiApiService.generateContent(
                        actualFileUri,
                        PromptText.OCR_PROMPT.promptText,
                        systemInstruction
                    )

                    val list = ArrayList<String>()
                    content.forEach { response ->
                        val textResponse = if (response is GeminiResponse.Success) {
                            extractGeminiResponseUseCase.invoke(response.data ?: "{}")
                        } else {
                            response.message
                        }
                        if (!textResponse.isNullOrEmpty())
                            list.add(textResponse)
                        else {
                            updateOcrError(Throwable(textOnExtractionError))
                            list.add("")
                        }
                    }
                    updateRecognizedTextList(list)
                    // update with the first result
                    updateRecognizedText(recognizedTextList.value[0])

                    /*
                    val textResponse = if (content is GeminiResponse.Success) {
                        extractGeminiResponseUseCase.invoke(content.data ?: "{}")
                    } else {
                        content.message
                    }
                    if (!textResponse.isNullOrEmpty())
                        updateRecognizedText(textResponse)
                    else {
                        updateOcrError(Throwable(textOnExtractionError))
                        updateRecognizedText("")
                    }
                    */
                } else {
                    updateOcrError(Throwable(textOnExtractionError))
                    updateRecognizedText("")
                }

            }

            fileUriResult.onFailure { updateOcrError(it) }
        }
        uploadResult.onFailure { updateOcrError(it) }
    }

}
