package com.schoolkiller.presentation.screens.ocr

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.network.api.GeminiApiService
import com.schoolkiller.data.network.response.GeminiResponse
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
        textOnExtractionError: String
    ) = viewModelScope.launch {

        val prompt = "Recognize text from this image."
        val systemInstruction = ""

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
                    val content = geminiApiService.generateContent(
                        actualFileUri, prompt, systemInstruction
                    )
                    val textResponse = if (content is GeminiResponse.Success) {
                        extractGeminiResponseUseCase.invoke(content.data ?: "{}")
                    } else {
                        content.message
                    }
                    if (!textResponse.isNullOrEmpty())
                        updateRecognizedText(textResponse)
                    else
                        updateRecognizedText(textOnExtractionError)
                } else
                    updateRecognizedText(textOnExtractionError)
            }

            fileUriResult.onFailure { updateOcrError(it) }
        }
        uploadResult.onFailure { updateOcrError(it) }
    }

}
