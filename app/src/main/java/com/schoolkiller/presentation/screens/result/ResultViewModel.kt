package com.schoolkiller.presentation.screens.result

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.network.api.GeminiApiService
import com.schoolkiller.data.network.response.GeminiResponse
import com.schoolkiller.data.repositories.DataStoreRepository
import com.schoolkiller.domain.model.ResultProperties
import com.schoolkiller.domain.usecases.ads.InterstitialAdUseCase
import com.schoolkiller.domain.usecases.api.ExtractGeminiResponseUseCase
import com.schoolkiller.domain.usecases.api.GetImageByteArrayUseCase
import com.schoolkiller.presentation.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val getImageByteArrayUseCase: GetImageByteArrayUseCase,
    private val extractGeminiResponseUseCase: ExtractGeminiResponseUseCase,
    private val interstitialAdUseCase: InterstitialAdUseCase,
    // private val bannerAdUseCase: BannerAdUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    /*private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String?> = _recognizedText*/

    private val _resultPropertiesState = MutableStateFlow(ResultProperties())
    val resultPropertiesState: StateFlow<ResultProperties> = _resultPropertiesState
        .onStart {
            /** This is like the init block */
            // updateAdview(bannerAdUseCase.getMediumBannerAdView())
            readImageState()
            readConvertedSolvePromptState()
            readConvertedSolutionPromptTextState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ResultProperties()
        )

    /*fun updateRecognizedText(recognizedText: String) {
        _recognizedText.update { recognizedText }
    }*/

    /** we can use this for handling errors, easier debugging with logging, and
     * show circular indicator when something is delaying to showed in the UI */
    private val _resultScreenRequestState = MutableStateFlow<RequestState<ResultProperties>>(
        RequestState.Idle
    )
    val resultScreenRequestState: StateFlow<RequestState<ResultProperties>> =
        _resultScreenRequestState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = RequestState.Idle
            )

    /*
//    init {
//        updateAdview(bannerAdUseCase.getMediumBannerAdView())
//        readImageState()
//        readConvertedSolvePromptState()
//        readConvertedSolutionPromptTextState()
//    }

     */

    /*fun updateAdview(adView: AdView?) {
        _resultPropertiesState.update { currentState ->
            currentState.copy(mediumBannerAdview = adView)
        }
    }*/

    fun updateIsSolveActionRequested(isSolveActionRequested: Boolean) {
        _resultPropertiesState.update { currentState ->
            currentState.copy(isSolveActionRequested = isSolveActionRequested)
        }
    }

    fun updateResultFetchedStatus(isResultFetchedStatus: Boolean) {
        _resultPropertiesState.update { currentState ->
            currentState.copy(isResultFetchedStatus = isResultFetchedStatus)
        }
    }

    fun updateTextGenerationResult(resultText: String?, error: Throwable? = null) {
        _resultPropertiesState.update { currentState ->
            currentState.copy(
                textGenerationResult = resultText ?: currentState.textGenerationResult,
                error = error ?: currentState.error
            )
        }
    }

    fun updatePassedImageUri(uri: Uri?) {
        _resultPropertiesState.update { currentState ->
            currentState.copy(passedImageUri = uri)
        }
    }


    fun updatePassedConvertedSolutionPrompt(solutionPrompt: String) {
        _resultPropertiesState.update { currentState ->
            currentState.copy(passedConvertedSolutionPrompt = solutionPrompt)
        }
    }

    fun updatePassedConvertedSolvePrompt(solvePrompt: String) {
        _resultPropertiesState.update { currentState ->
            currentState.copy(passedConvertedSolvePrompt = solvePrompt)
        }
    }

    fun updateRequestGeminiResponse(requestResponse: Boolean) {
        _resultPropertiesState.update { currentState ->
            currentState.copy(requestGeminiResponse = requestResponse)
        }
    }

    fun clearError() {
        _resultPropertiesState.update { currentState ->
            currentState.copy(error = null)
        }
    }

    fun showInterstitialAd(context: Context) {
        interstitialAdUseCase.show(context)
    }

    fun geminiGenerateSolution(
        systemInstruction: String,
        prompt: String,
        textOnExtractionError: String
    ) = viewModelScope.launch {

        val sysInstruction = systemInstruction +
                "Use Html tags instead of markdown." +
                "Don't include pictures in your response."

        val content = geminiApiService.generateContent(
            "", prompt, sysInstruction
        )
        val textResponse = if (content is GeminiResponse.Success) {
            extractGeminiResponseUseCase.invoke(content.data ?: "{}")
        } else {
            content.message
        }

        if (!textResponse.isNullOrEmpty())
            updateTextGenerationResult(textResponse)
        else {
            updateTextGenerationResult(textOnExtractionError)
            //updateError(Throwable("Extraction failure"))
        }
    }

    /*
    fun fetchGeminiResponse(
        imageUri: Uri,
        fileName: String,
        prompt: String,
        systemInstruction: String
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
                        val content = geminiApiService.generateContent(
                            actualFileUri,
                            prompt,
                            systemInstruction
                        )
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
                    _resultPropertiesState.update { currentState ->
                        currentState.copy(error = throwable)
                    }
                }
            }
            uploadResult.onFailure { throwable ->
                _resultPropertiesState.update { currentState ->
                    currentState.copy(error = throwable)
                }
            }
        }
    }
*/

    private fun readImageState() {
        viewModelScope.launch {
            try {
                dataStoreRepository.readImageState.collect { imageUri ->
                    updatePassedImageUri(imageUri)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error reading image state")
                updatePassedImageUri(null)
            }
        }
    }

    private fun readConvertedSolvePromptState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readSolvePromptState.collect { solvePromptText ->
                    updatePassedConvertedSolvePrompt(solvePromptText)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading solve prompt text state")
            updatePassedConvertedSolvePrompt(resultPropertiesState.value.passedConvertedSolvePrompt)
        }
    }

    private fun readConvertedSolutionPromptTextState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readSolutionPromptState.collect { solutionPromptText ->
                    updatePassedConvertedSolutionPrompt(solutionPromptText)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading solution prompt text state")
            updatePassedConvertedSolutionPrompt(resultPropertiesState.value.passedConvertedSolutionPrompt)
        }
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