package com.schoolkiller.presentation.screens.result

import android.content.Context
import android.net.Uri
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.network.gemini_api.GeminiApiService
import com.schoolkiller.data.network.gemini_api.GeminiRequest
import com.schoolkiller.data.network.gemini_api.GeminiResponse
import com.schoolkiller.data.repositories.DataStoreRepository
import com.schoolkiller.domain.model.ResultProperties
import com.schoolkiller.domain.prompt.Prompt
import com.schoolkiller.domain.usecases.ImageUtils
import com.schoolkiller.presentation.RequestState
import com.schoolkiller.presentation.ads.InterstitialAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.Bidi
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val geminiApiService: GeminiApiService,
    //private val imageUtils: ImageUtils,
    private val interstitialAdUseCase: InterstitialAdUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _textAlignment = MutableStateFlow(LayoutDirection.Ltr)
    val textAlignment: StateFlow<LayoutDirection> = _textAlignment

    fun updateTextAlignment(textAlignment: LayoutDirection) {
        _textAlignment.update { textAlignment }
    }

    private fun getTextDir(content: String): LayoutDirection {
        val isLtr = Bidi(
            content,
            Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT
        ).isLeftToRight
        return if (isLtr) LayoutDirection.Ltr else LayoutDirection.Rtl
    }

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
    init {
        updateAdview(bannerAdUseCase.getMediumBannerAdView())
        readImageState()
        readConvertedSolvePromptState()
        readConvertedSolutionPromptTextState()
    }

     */

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

        if (resultText != null)
            updateTextAlignment(getTextDir(resultText))

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
        val request = GeminiRequest.buildGeminiRequest(
            prompt = prompt,
            systemInstruction = systemInstruction + Prompt.HTML_REQUEST.text
        )
        val content = geminiApiService.generateContent(request)
        if (content is GeminiResponse.Success) {
            updateTextGenerationResult(content.data)
        } else {
            updateTextGenerationResult(textOnExtractionError)

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

}