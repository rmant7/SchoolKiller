package com.schoolkiller.presentation.screens.checking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdView
import com.schoolkiller.data.repositories.DataStoreRepository
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.model.SolutionProperties
import com.schoolkiller.domain.usecases.ads.BannerAdUseCase
import com.schoolkiller.presentation.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SolutionCheckingViewModel @Inject constructor(
    private val bannerAdUseCase: BannerAdUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    private val _solutionPropertiesState = MutableStateFlow(SolutionProperties())
    val solutionPropertiesState: StateFlow<SolutionProperties> = _solutionPropertiesState
        .onStart {
            /** This is like the init block */
            updateAdview(bannerAdUseCase.getStretchedBannerAdView())
            readSolutionPromptTextState()
            readSolutionGradeOptionState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SolutionProperties()
        )

    /** we can use this for handling errors, easier debugging with logging, and
     * show circular indicator when something is delaying to showed in the UI */
    private val _solutionScreenRequestState = MutableStateFlow<RequestState<SolutionProperties>>(
        RequestState.Idle
    )
    val solutionScreenRequestState: StateFlow<RequestState<SolutionProperties>> =
        _solutionScreenRequestState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = RequestState.Idle
            )


    /*
    init {
        _adview.update { bannerAdUseCase.getStretchedBannerAdView() }
        readSolutionPromptTextState()
        readSolutionGradeOptionState()
    }
     */

    private fun updateAdview(newAd: AdView?) {
        _solutionPropertiesState.update { currentState ->
            currentState.copy(adView = newAd)
        }
    }

    fun updateSelectedRateMax(newRateMax: Int) {
        _solutionPropertiesState.update { currentState ->
            currentState.copy(selectedRateMax = newRateMax)
        }
    }

    fun updateSelectedGradeOption(newClassSelection: GradeOption) {
        _solutionPropertiesState.update { currentState ->
            currentState.copy(grade = newClassSelection)
        }
        persistSolutionGradeOptionState(newClassSelection)
    }

    private fun updateSolutionPromptText(newSolutionPromptText: String) {
        _solutionPropertiesState.update { currentState ->
            currentState.copy(solutionPromptText = newSolutionPromptText)
        }
        persistSolutionPromptTextState(newSolutionPromptText)
    }

    fun updateTextGenerationResult(resultText: String?, error: Throwable? = null) {
        _solutionPropertiesState.update { currentState ->
            currentState.copy(
                textGenerationResult = resultText ?: currentState.textGenerationResult,
                error = error ?: currentState.error
            )
        }
    }


    fun buildSolutionPrompt() {
        val originalPrompt = PromptText.CHECK_SOLUTION_PROMPT.promptText
        val selectedGradeStr = "${solutionPropertiesState.value.grade.arrayIndex}"

        val promptWithGradeOption = if (originalPrompt.contains("(as grade+th grader)")) {
            originalPrompt.replace("(as grade+th grader)", "as ${selectedGradeStr}th grader")
        } else {
            originalPrompt
        }
        updateSolutionPromptText(promptWithGradeOption)
    }

    /** This should be inside buildSolutionPrompt.
     *  Maybe even let user to choose if they want to do OCR or not.
     */
    fun getPrompt(recognizedText: String?): String {
        return solutionPropertiesState.value.solutionPromptText +
                " The task is: $recognizedText"
    }

    fun getSystemInstruction(hasHtml: Boolean): String {
        val systemInstruction = PromptText
            .CHECK_SOLUTION_SYSTEM_INSTRUCTION.promptText
        if (hasHtml) systemInstruction.plus(PromptText.HTML_REQUEST)
        return systemInstruction
    }


    private fun persistSolutionGradeOptionState(solutionGradeOption: GradeOption) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSolutionGradeOptionState(solutionGradeOption = solutionGradeOption)
        }
    }

    private fun persistSolutionPromptTextState(newSolutionPromptText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSolutionPromptState(solutionPrompt = newSolutionPromptText)
        }
    }

    private fun readSolutionGradeOptionState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readSolutionGradeOptionState.map { GradeOption.valueOf(it) }
                    .collect { gradeOption ->
                        updateSelectedGradeOption(gradeOption)
                    }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading grade option state")
            updateSelectedGradeOption(GradeOption.NONE)
        }
    }

    private fun readSolutionPromptTextState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readSolutionPromptState.collect { solutionPromptText ->
                    updateSolutionPromptText(solutionPromptText)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading solution prompt text state")
            updateSolutionPromptText(PromptText.CHECK_SOLUTION_PROMPT.promptText)
        }
    }

}