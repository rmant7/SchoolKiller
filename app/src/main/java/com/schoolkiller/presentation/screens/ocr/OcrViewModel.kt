package com.schoolkiller.presentation.screens.ocr

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.LayoutDirection
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
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import timber.log.Timber
import java.io.IOException
import java.text.Bidi
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val imageUtils: ImageUtils,
) : ViewModel() {

    private var isFirstAlignment = true
    // Text direction: ltr / rtl
    private val _textDirection = MutableStateFlow(LayoutDirection.Ltr)
    val textDirection: StateFlow<LayoutDirection> = _textDirection

    fun updateTextDirection(textDirection: LayoutDirection) {
        _textDirection.update { textDirection }
    }

    private fun getTextDir(text: String): LayoutDirection {
        val isLtr = Bidi(
            text,
            Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT
        ).isLeftToRight
        return if (isLtr) LayoutDirection.Ltr else LayoutDirection.Rtl
    }

    // Show notification that ocr result is ready
    private val _shouldShowOcrNotification = MutableStateFlow(false)
    val shouldShowOcrNotification: StateFlow<Boolean> = _shouldShowOcrNotification

    fun updateShouldShowOcrNotification(shouldShowOcrNotification: Boolean) {
        _shouldShowOcrNotification.update { shouldShowOcrNotification }
    }

    // New ocr request condition
    private val _shouldRecognizeText = MutableStateFlow(true)
    val shouldRecognizeText: StateFlow<Boolean> = _shouldRecognizeText

    fun updateShouldRecognizeText(shouldRecognizeText: Boolean) {
        _shouldRecognizeText.update { shouldRecognizeText }
    }

    private val maxOcrRequests = 2
    // Html responses from Gemini Ocr
    var htmlGeminiResponses: MutableList<String> = mutableStateListOf()

    fun replaceRecognizedText(index: Int, recognizedText: String) {
        if (index < htmlGeminiResponses.size)
            htmlGeminiResponses[index] = recognizedText
    }

    private fun addRecognizedText(recognizedText: String) {
        if (htmlGeminiResponses.size < maxOcrRequests)
            htmlGeminiResponses.add(recognizedText)
    }

    fun clearRecognizedTextList() {
        htmlGeminiResponses.clear()
    }

    // Tesseract Ocr result.
    private val _tesseractOcrResult = MutableStateFlow("")
    val tesseractOcrResult: StateFlow<String> = _tesseractOcrResult

    fun updateTesseractOcrResult(recognizedText: String) {
        _tesseractOcrResult.update { recognizedText }
    }

    // Text selected by user
    private val _selectedText = MutableStateFlow("")
    val selectedText: StateFlow<String> = _selectedText

    fun updateSelectedText(recognizedText: String) {
        if (isFirstAlignment) {
            updateTextDirection(getTextDir(recognizedText))
            isFirstAlignment = false
        }
        _selectedText.update { recognizedText }
    }

    // A pair of error title and error message
    private val _ocrError = MutableStateFlow<Throwable?>(null)
    val ocrError: StateFlow<Throwable?> = _ocrError

    fun updateOcrError(ocrError: Throwable?) {
        _ocrError.update { ocrError }
    }


    fun tessaractImageToText(
        passedImageUri: Uri,
        context: Context
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            /*
            // Open the image from assets/images folder
            val inputStream = context.assets.open("images/shalom.png") // should be replaced by the requested image
            val bitmap = BitmapFactory.decodeStream(inputStream)
            */

            val bitmap = imageUtils.convertUriToBitMap(passedImageUri)
            // Check if the bitmap was successfully loaded
            if (bitmap != null) {
                // Copy tessdata files to internal storage
                val tessDataPath = context.filesDir.absolutePath + "/tessdata"
                copyTessDataFiles(context, tessDataPath)

                val response = tessaractImage(context, bitmap)
                response.onSuccess { res ->
                    Timber.d("Success! Ocr result is $res")
                    updateTesseractOcrResult(res)
                }
                response.onFailure {
                    Timber.e(it)
                    updateOcrError(it)
                }
            } else {
                Timber.e(IOException())
                updateOcrError(IOException("Unable to load bitmap from assets"))
            }
        } catch (e: Exception) {
            // Handle any exceptions (like missing file, decoding errors)
            Timber.e(e, "Unexpected Error.")
            updateOcrError(e)
        }
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

                repeat(maxOcrRequests) {
                    fetchResponse(
                        actualFileUri = actualFileUri,
                        systemInstruction = Prompt.HTML_OCR_SYSTEM_INSTRUCTION.text,
                        invalidOcrResultText = invalidOcrResultText,
                        onResultFetched = {
                            //updateShouldShowOcrNotification(true)
                            addRecognizedText(it)
                            if (htmlGeminiResponses.size == 1)
                                updateSelectedText(it)
                        }
                    )
                }

            }

            fileUriResult.onFailure {
                updateOcrError(it)
            }
        }
        uploadResult.onFailure { updateOcrError(it) }
    }

    private fun fetchResponse(
        actualFileUri: String,
        systemInstruction: String,
        invalidOcrResultText: String,
        onResultFetched: (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {

        val request = GeminiRequest.buildGeminiRequest(
            actualFileUri,
            Prompt.OCR_PROMPT.text,
            systemInstruction
        )

        val content = geminiApiService.generateContent(request)

        if (content is GeminiResponse.Success)
            onResultFetched(cleanHtmlResponse(content.data!!))
        else
            onResultFetched(invalidOcrResultText)
    }

    private fun cleanHtmlResponse(htmlResponse: String): String {
        val imageRegex = Regex("""!\[.*?]\(.*?\)""", RegexOption.MULTILINE)
        val emptyLines = Regex("""^\s+""", RegexOption.MULTILINE)
        val cleanedResponse = htmlResponse
            .replace(imageRegex, "") // remove all image markdown references
            .trim() // remove leading and trailing spaces
            .replace(emptyLines, "") // remove empty lines

        // convert markdown to html
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(cleanedResponse)
        val html = HtmlGenerator(cleanedResponse, parsedTree, flavour).generateHtml()
        return html
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
