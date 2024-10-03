package com.schoolkiller.presentation.screens.result

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.schoolkiller.data.Constants
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

    // InterstitialAd State
    private var _interstitialAd = MutableStateFlow<InterstitialAd?>(null)
    val interstitialAd: StateFlow<InterstitialAd?> = _interstitialAd

    fun updateInterstitialAd(newAd: InterstitialAd?) {
        _interstitialAd.update { newAd }
    }

    fun loadInterstitialAd() {
        interstitialAdUseCase.loadAd(
            adUnitId = Constants.INTERSTITIAL_AD_ID,
            viewModel = this@ResultViewModel
        )
    }


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



    init {
        loadInterstitialAd()
    }

}