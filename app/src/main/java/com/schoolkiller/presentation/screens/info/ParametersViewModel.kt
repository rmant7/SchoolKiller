package com.schoolkiller.presentation.screens.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.repositories.DataStoreRepository
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.SolutionLanguageOption
import com.schoolkiller.domain.model.ParameterProperties
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
class ParametersViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _parameterPropertiesState = MutableStateFlow(ParameterProperties())
    val parameterPropertiesState: StateFlow<ParameterProperties> =
        _parameterPropertiesState.asStateFlow()
    /*   Thinking of this approach, need more testing
    private val _parameterPropertiesState = MutableStateFlow(ParameterProperties())
    val parameterPropertiesState = _parameterPropertiesState
       .onStart {
           readSolvePromptState()
           readGradeOptionState()
           readLanguageOptionState()
           readExplanationLevelOptionState()
           readDescriptionOptionState()
       }
       .stateIn(
           viewModelScope,
           SharingStarted.WhileSubscribed(5000L),
           _parameterPropertiesState.value
       )
     */


    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

    fun updateSelectedGradeOption(newClassSelection: GradeOption) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(grade = newClassSelection)
        }
        persistGradeOptionState(newClassSelection)
    }

    fun updateSelectedLanguageOption(newLanguageSelection: SolutionLanguageOption) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(language = newLanguageSelection)
        }
        persistLanguageOptionState(newLanguageSelection)
    }

    fun updateSelectedExplanationLevelOption(newExplanationLevelSelection: ExplanationLevelOption) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(explanationLevel = newExplanationLevelSelection)
        }
        persistExplanationLevelOptionState(newExplanationLevelSelection)
    }

    fun updateDescriptionText(addedText: String) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(description = addedText)
        }
        persistDescriptionState(addedText)
    }

    private fun updateSolvePromptText(newSolvePromptText: String) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(solvePromptText = newSolvePromptText)
        }
        persistSolvePromptTextState(newSolvePromptText)
    }


    fun buildSolvingPrompt() {
        val originalPrompt = PromptText.SOLVE_PROMPT.promptText
        val selectedGradeStr = "${parameterPropertiesState.value.grade.arrayIndex}"
        val selectedLanguageStr = "${parameterPropertiesState.value.language.languageName}"
        val selectedExplanationStr = "${parameterPropertiesState.value.explanationLevel.code}"
        val description = " ${parameterPropertiesState.value.description}"

        val promptWithGradeOption = if (originalPrompt.contains("(as grade+th grader)")) {
            originalPrompt.replace("(as grade+th grader)", "as ${selectedGradeStr}th grader")
        } else {
            originalPrompt
        }
        val promptWithLanguageOption = if (promptWithGradeOption.contains("(language shown on this picture)")) {
            promptWithGradeOption.replace("(language shown on this picture)", selectedLanguageStr)
        } else {
            promptWithGradeOption
        }
        val promptWithExplanationOption = if (promptWithLanguageOption.contains("(briefly)")) {
            promptWithLanguageOption.replace("(briefly)", selectedExplanationStr)
        } else {
            promptWithLanguageOption
        }
        val promptWithDescription = promptWithExplanationOption.plus(description)

        updateSolvePromptText(promptWithDescription)
    }


    private fun persistGradeOptionState(gradeOption: GradeOption) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistGradeOptionState(gradeOption = gradeOption)
        }
    }

    private fun persistLanguageOptionState(solutionLanguageOption: SolutionLanguageOption) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistLanguageOptionState(languageOption = solutionLanguageOption)
        }
    }

    private fun persistExplanationLevelOptionState(explanationLevelOption: ExplanationLevelOption) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistExplanationLevelOptionState(explanationLevelOption = explanationLevelOption)
        }
    }

    private fun persistDescriptionState(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistDescriptionState(description = description)
        }
    }

    private fun persistSolvePromptTextState(newSolvePromptText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSolvePromptState(solvePrompt = newSolvePromptText)
        }
    }


    private fun readGradeOptionState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readGradeOptionState.map { GradeOption.valueOf(it) }
                    .collect { gradeOption ->
                        updateSelectedGradeOption(gradeOption)
                    }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading grade option state")
            updateSelectedGradeOption(GradeOption.NONE)
        }
    }

    private fun readLanguageOptionState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readLanguageOptionState.map { SolutionLanguageOption.valueOf(it) }
                    .collect { languageOption ->
                        updateSelectedLanguageOption(languageOption)
                    }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading language option state")
            updateSelectedLanguageOption(SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE)
        }
    }


    private fun readExplanationLevelOptionState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readExplanationLevelOptionState.map {
                    ExplanationLevelOption.valueOf(
                        it
                    )
                }
                    .collect { explanationLevelOption ->
                        updateSelectedExplanationLevelOption(explanationLevelOption)
                    }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading explanation level option state")
            updateSelectedExplanationLevelOption(ExplanationLevelOption.SHORT_EXPLANATION)
        }
    }

    private fun readDescriptionOptionState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readDescriptionState.collect { description ->
                    updateDescriptionText(description)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading description state")
            updateDescriptionText("")
        }
    }

    private fun readSolvePromptState() {
        try {
            viewModelScope.launch {
                dataStoreRepository.readSolvePromptState.collect { solvePromptText ->
                    updateSolvePromptText(solvePromptText)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading solve prompt text state")
            updateSolvePromptText(PromptText.SOLVE_PROMPT.promptText)
        }
    }




    init {
        readSolvePromptState()
        readDescriptionOptionState()
        readGradeOptionState()
        readLanguageOptionState()
        readExplanationLevelOptionState()
    }

}