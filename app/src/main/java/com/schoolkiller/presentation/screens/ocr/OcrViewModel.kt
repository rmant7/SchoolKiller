package com.schoolkiller.presentation.screens.ocr

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.network.gemini_api.GeminiApiService
import com.schoolkiller.data.network.gemini_api.GeminiRequest
import com.schoolkiller.data.network.gemini_api.GeminiResponse
import com.schoolkiller.domain.usecases.tessaractImage
import com.schoolkiller.domain.usecases.copyTessDataFiles
import com.schoolkiller.domain.prompt.Prompt
import com.schoolkiller.domain.usecases.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.output.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val imageUtils: ImageUtils,
) : ViewModel() {

    private val maxOcrRequests = 3

    var recognizedList: MutableList<String> = mutableStateListOf()

    fun replaceRecognizedText(index: Int, recognizedText: String) {
        if (index < recognizedList.size)
            recognizedList[index] = recognizedText
    }

    private fun addRecognizedText(recognizedText: String) {
        if (recognizedList.size < maxOcrRequests)
            recognizedList.add(recognizedText)
    }

    fun clearRecognizedTextList() {
        recognizedList.clear()
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
        systemInstruction: String,
        invalidOcrResultText: String
    ): String {

        val request = GeminiRequest.buildGeminiRequest(
            actualFileUri,
            Prompt.OCR_PROMPT.text,
            systemInstruction
        )

        val content = geminiApiService.generateContent(request)

        return if (content is GeminiResponse.Success)
            content.data!!
        else
            invalidOcrResultText
    }

    fun tessaractImageToText(context: Context) = viewModelScope.launch {
        try {
            // Open the image from assets/images folder
            val inputStream = context.assets.open("images/shalom.png") // should be replaced by the requested image
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Check if the bitmap was successfully loaded
            if (bitmap != null) {
                // Copy tessdata files to internal storage
                val tessDataPath = context.filesDir.absolutePath + "/tessdata"
                copyTessDataFiles(context, tessDataPath)

                val response = tessaractImage(context, bitmap)
                response.onSuccess { res ->
                    addRecognizedText(res)
                    if (recognizedList.size == 1) {
                        updateRecognizedText(res)
                    }
                }
                response.onFailure {
                    updateOcrError(it)
                }
            } else {
                updateOcrError(IOException("Unable to load bitmap from assets"))
            }
        } catch (e: Exception) {
            // Handle any exceptions (like missing file, decoding errors)
            updateOcrError(e)
        }
    }

    fun geminiImageToText(
        imageUri: Uri,
        fileName: String,
        useHtml: Boolean,
        invalidOcrResultText: String
    ) = viewModelScope.launch {

        val fileByteArray = imageUtils.convertUriToByteArray(imageUri = imageUri)
        val uploadResult = geminiApiService.uploadFileWithProgress(
            fileByteArray,
            fileName
        )

        uploadResult.onSuccess { uploadModel ->
            val fileUriResult = geminiApiService.uploadFileBytes(
                uploadModel.uploadUrl,
                fileByteArray
            )

            fileUriResult.onSuccess { actualFileUri ->

                /** For tests */
                val systemInstruction =
                    if (useHtml) Prompt.HTML_OCR_SYSTEM_INSTRUCTION.text
                    else Prompt.NO_HTML_OCR_SYSTEM_INSTRUCTION.text

                repeat(maxOcrRequests) {
                    // async calls
                    launch {
                        val response = fetchResponse(
                            actualFileUri,
                            systemInstruction,
                            invalidOcrResultText
                        ).trim()
                        addRecognizedText(response)
                        if (recognizedList.size == 1)
                            updateRecognizedText(response)

                        /*if (list.size == maxOcrRequests) {
                            updateRecognizedTextList(list)
                            updateRecognizedText(recognizedTextList.value[0])
                        }*/
                    }
                }

            }

            fileUriResult.onFailure {
                updateOcrError(it)
                updateRecognizedText(invalidOcrResultText)
            }
        }
        uploadResult.onFailure { updateOcrError(it) }
    }

    //Don't remove, for future development

    fun fetchOpenAiResponse(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            // val key = "API_KEY"

            val model: OpenAiChatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build()

            val userMessage: UserMessage = UserMessage.from(
                TextContent.from(
                    "Describe each math problem in this image " +
                            "Describe shapes, known and unknown variables. " +
                            "Separate each math problem" +
                            "Don't use markdown or html tags."
                ),
                ImageContent.from(
                    "https://i.imgur.com/udmUVd0.jpeg",
                    ImageContent.DetailLevel.HIGH
                )
                /* ImageContent.from(
                     imageUtils.convertToBase64(imageUri), "image/jpg",
                     ImageContent.DetailLevel.LOW
                 )*/
            )

            val response: Response<AiMessage> = model.generate(userMessage)
            println(response)
            // updateTextGenerationResult(response.content().text())
        }
    }

}
