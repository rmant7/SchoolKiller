package com.schoolkiller.presentation.screens.ocr


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.network.gemini_api.GeminiApiService
import com.schoolkiller.data.network.gemini_api.GeminiRequest
import com.schoolkiller.data.network.gemini_api.GeminiResponse
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
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val imageUtils: ImageUtils,
) : ViewModel() {

    private val maxOcrRequests = 2

    private val _htmlGeminiResponse = MutableStateFlow("")
    val htmlGeminiResponse: StateFlow<String> = _htmlGeminiResponse

    fun updateHtmlGeminiResponse(recognizedText: String) {
        _htmlGeminiResponse.update { recognizedText }
    }

    private val _noHtmlGeminiResponse = MutableStateFlow("")
    val noHtmlGeminiResponse: StateFlow<String> = _noHtmlGeminiResponse

    fun updateNoHtmlGeminiResponse(recognizedText: String) {
        _noHtmlGeminiResponse.update { recognizedText }
    }

    /*
    var recognizedTextList: MutableList<String> = mutableStateListOf()

    fun replaceRecognizedText(index: Int, recognizedText: String) {
        if (index < recognizedTextList.size)
            recognizedTextList[index] = recognizedText
        else addRecognizedText(recognizedText)
    }

    private fun addRecognizedText(recognizedText: String) {
        if (recognizedTextList.size < maxOcrRequests)
            recognizedTextList.add(recognizedText)
    }

    fun clearRecognizedTextList() {
        recognizedTextList.clear()
    }
*/

    private val _selectedText = MutableStateFlow("")
    val selectedText: StateFlow<String> = _selectedText

    fun updateSelectedText(recognizedText: String) {
        _selectedText.update { recognizedText }
    }

    /*fun updateSelectedText(selectedIndex: Int) {
        _selectedText.update { recognizedTextList[selectedIndex] }
    }*/


    // a pair of error title and error message
    private val _ocrError = MutableStateFlow<Throwable?>(null)
    val ocrError: StateFlow<Throwable?> = _ocrError

    fun updateOcrError(ocrError: Throwable?) {
        _ocrError.update { ocrError }
    }

    private fun fetchResponse(
        actualFileUri: String,
        systemInstruction: String,
        invalidOcrResultText: String,
        onFetch: (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {

        val request = GeminiRequest.buildGeminiRequest(
            actualFileUri,
            Prompt.OCR_PROMPT.text,
            systemInstruction
        )

        val content = geminiApiService.generateContent(request)

        if (content is GeminiResponse.Success)
            onFetch(content.data!!)
        else
            onFetch(invalidOcrResultText)
    }

    fun geminiImageToText(
        imageUri: Uri,
        fileName: String,
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

                fetchResponse(
                    actualFileUri,
                    Prompt.NO_HTML_OCR_SYSTEM_INSTRUCTION.text,
                    invalidOcrResultText,
                    onFetch = {
                        val cleanedStr = it.replace(
                            Regex("""^\s+""", RegexOption.MULTILINE), ""
                        )
                        updateNoHtmlGeminiResponse(cleanedStr)
                        updateSelectedText(cleanedStr)
                    }
                )

                fetchResponse(
                    actualFileUri,
                    Prompt.HTML_OCR_SYSTEM_INSTRUCTION.text,
                    invalidOcrResultText,
                    onFetch = {
                        updateHtmlGeminiResponse(it)
                    }
                )

            }

            fileUriResult.onFailure {
                updateOcrError(it)
            }
        }
        uploadResult.onFailure { updateOcrError(it) }
    }

    // Don't remove, for future development
    // ChatGpt response
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
