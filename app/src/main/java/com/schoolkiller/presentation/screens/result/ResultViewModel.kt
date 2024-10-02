package com.schoolkiller.presentation.screens.result

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.network.api.GeminiApiService
import com.schoolkiller.data.network.response.GeminiResponse
import com.schoolkiller.domain.usecases.ads.InterstitialAdUseCase
import com.schoolkiller.domain.usecases.api.ExtractGeminiResponseUseCase
import com.schoolkiller.domain.usecases.api.GetImageByteArrayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val getImageByteArrayUseCase: GetImageByteArrayUseCase,
    private val extractGeminiResponseUseCase: ExtractGeminiResponseUseCase,
    private val interstitialAdUseCase: InterstitialAdUseCase
) : ViewModel() {

    private val _textGenerationResult = MutableStateFlow("")
    val textGenerationResult = _textGenerationResult.asStateFlow()

    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

    private var _requestGeminiResponse = MutableStateFlow(true)
    val requestGeminiResponse: StateFlow<Boolean> = _requestGeminiResponse

    private var _isResultFetchedStatus = MutableStateFlow(false)
    val isResultFetchedStatus: StateFlow<Boolean> = _isResultFetchedStatus

    fun updateResultFetchedStatus(isResultFetchedStatus: Boolean) {
        _isResultFetchedStatus.update { isResultFetchedStatus }
    }

    fun updateTextGenerationResult(resultText: String?, error: Throwable? = null) {
        resultText?.let { text -> _textGenerationResult.update { text } }
        error?.let { err -> _error.update { err } }
    }

    fun showInterstitialAd(context: Context){
        interstitialAdUseCase.show(context)
    }

    fun fetchGeminiResponse(
        imageUri: Uri,
        fileName: String,
        prompt: String
    ) {
        viewModelScope.launch {
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
                        val content = geminiApiService.generateContent(actualFileUri, prompt)
                        val textResponse = if (content is GeminiResponse.Success) {
                            extractGeminiResponseUseCase.invoke(content.data ?: "{}")
                        } else {
                            content.message
                        }
                        updateTextGenerationResult(textResponse)
                    } else {
                        // Handle the case where the URI couldn't be extracted
                        updateTextGenerationResult(
                            null,
                            RuntimeException(" URI couldn't be extracted")
                        )
                    }
                }

                fileUriResult.onFailure { throwable ->
                    _error.update { throwable }
                }
            }
            uploadResult.onFailure { throwable ->
                _error.update { throwable }
            }
        }
    }

    fun updateRequestGeminiResponse(requestResponse: Boolean) {
        _requestGeminiResponse.update { requestResponse }
    }

    fun clearError() {
        _error.value = null
    }

    //Don't remove, for future development
    /*
       fun fetchAIResponse(
           imageUri: Uri,
           fileName: String,
           context: Context
           aiModelOption: AiModelOptions
       ) {

           when (aiModelOption) {
             AiModelOptions.MODEL_ONE -> fetchOpenAiResponse(imageUri)
               AiModelOptions.MODEL_TWO -> fetchGeminiResponse(
                   imageUri, fileName, ""
               )
           }
       }
   */

    //Don't remove, for future development
    /*
       private fun convertToBase64(selectedUri: Uri, context: Context): String {
           val bitmap = MediaStore.Images.Media.getBitmap(
               context.contentResolver,
               selectedUri
           )
           val outputStream = ByteArrayOutputStream()
           bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
           val byteArray = outputStream.toByteArray()

           val encodedString: String = Base64.encodeToString(
               byteArray, Base64.DEFAULT
           )
           return encodedString
       }
   */

    //Don't remove, for future development
    /*
    fun fetchOpenAiResponse(imageUri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val key = "API_KEY"

            val model: OpenAiChatModel = OpenAiChatModel.builder()
                .apiKey(key)
                .modelName("gpt-4o")
                .build()

            val userMessage: UserMessage = UserMessage.from(
                TextContent.from("What is in this picture?"),
                ImageContent.from(
                    convertToBase64(imageUri, context), "image/png",
                    ImageContent.DetailLevel.LOW
                )
            )
            val response: Response<AiMessage> = model.generate(userMessage)

            updateTextGenerationResult(response.content().text())
        }
    }
*/

}