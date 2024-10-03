package com.schoolkiller.presentation.screens.checking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdView
import com.schoolkiller.data.repositories.DataStoreRepository
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.model.SolutionProperties
import com.schoolkiller.domain.usecases.ads.BannerAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SolutionCheckingViewModel @Inject constructor(
    private val bannerAdUseCase: BannerAdUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    // BannerAd State
    private var _adview = MutableStateFlow<AdView?>(null)
    val adview: StateFlow<AdView?> = _adview.asStateFlow()
    private fun updateAdview(newAd: AdView?) {
        _adview.update { newAd }
    }

    fun getBannerAdView(): AdView? {
        return bannerAdUseCase.getBannerAdView()
    }


    private val _solutionPropertiesState = MutableStateFlow(SolutionProperties())
    val solutionPropertiesState:StateFlow<SolutionProperties> = _solutionPropertiesState.asStateFlow()
     /*   Thinking of this approach, need more testing
    private val _solutionPropertiesState = MutableStateFlow(SolutionProperties())
    val solutionPropertiesState = _solutionPropertiesState
        .onStart {
            readSolutionGradeOptionState()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _solutionPropertiesState.value
        )
      */



    private var _selectedRateMax by mutableIntStateOf(100)
    val selectedRateMax: Int
        get() = _selectedRateMax


    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

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
        _solutionPropertiesState.update{ currentState ->
            resultText?.let { text ->  currentState.copy(textGenerationResult = text) }!!  // TODO { assertion }
        }
        error?.let { err -> _error.update { err } }

    }


    fun updateSelectedRateMax(newRateMax: Int) {
        _selectedRateMax = newRateMax
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



    init {
        readSolutionPromptTextState()
        readSolutionGradeOptionState()
        updateAdview(getBannerAdView())
    }

}