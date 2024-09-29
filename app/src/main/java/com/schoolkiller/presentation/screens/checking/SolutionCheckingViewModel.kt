package com.schoolkiller.presentation.screens.checking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdView
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.usecases.ads.BannerAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SolutionCheckingViewModel @Inject constructor(
    private val bannerAdUseCase: BannerAdUseCase
) : ViewModel() {

    // BannerAd State
    private var _adview = MutableStateFlow<AdView?>(null)
    val adview: StateFlow<AdView?> = _adview
    private fun updateAdview(newAd: AdView?) {
        _adview.update { newAd }
    }

    fun getBannerAdView(): AdView? {
        return bannerAdUseCase.getBannerAdView()
    }

    private var _originalPromptText = MutableStateFlow("")
    val originalPrompt: StateFlow<String> = _originalPromptText

    private var _selectedGradeOption by mutableStateOf(GradeOption.NONE)
    val selectedGradeOption: GradeOption
        get() = _selectedGradeOption

    private var _selectedRateMax by mutableIntStateOf(100)
    val selectedRateMax: Int
        get() = _selectedRateMax

    private val _textGenerationResult = MutableStateFlow("")
    val textGenerationResult = _textGenerationResult.asStateFlow()

    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

    fun updateSelectedGradeOption(newClassSelection: GradeOption) {
        _selectedGradeOption = newClassSelection
    }

    fun updatePrompt(convertedPrompt: String) {
        _originalPromptText.value = convertedPrompt
    }

    fun updateTextGenerationResult(resultText: String?, error: Throwable? = null) {
        resultText?.let { text -> _textGenerationResult.update { text } }
        error?.let { err -> _error.update { err } }
    }

    fun buildPropertiesPrompt() {
        // reset
        updatePrompt(PromptText.CHECK_SOLUTION_PROMPT.promptText)
        updatePrompt(
            getGradePrompt()
        )
    }

    fun updateSelectedRateMax(newRateMax: Int) {
        _selectedRateMax = newRateMax
    }

    private fun getGradePrompt(): String {
        val selectedGradeStr = " ${_selectedGradeOption.arrayIndex}"

        return if (_originalPromptText.value.contains("(as grade+th grader)")) {
            _originalPromptText
                .value
                .replace(
                    "(as grade+th grader)",
                    "as ${selectedGradeStr}th grader"
                )
        } else {
            return _originalPromptText
                .value
                .plus(" Explain as ${selectedGradeStr}th grader.")
        }

    }

    init {
        updateAdview(getBannerAdView())
    }

}