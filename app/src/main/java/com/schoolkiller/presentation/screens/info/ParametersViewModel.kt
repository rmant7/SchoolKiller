package com.schoolkiller.presentation.screens.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.RequestDataState
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    private val _parameterPropertiesState = MutableStateFlow(ParameterProperties())
    val parameterPropertiesState: StateFlow<ParameterProperties> = _parameterPropertiesState.asStateFlow()

    private var _originalPrompt = MutableStateFlow(PromptText.SOLVE_PROMPT.promptText)
    val originalPrompt: StateFlow<String> = _originalPrompt.asStateFlow()

//    private var _selectedGrade = MutableStateFlow(GradeOption.NONE)
//    val selectedGradeOption: StateFlow<GradeOption>
//        get() = _selectedGrade.asStateFlow()
//
//    private var _selectedLanguage = MutableStateFlow(SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE)
//    val selectedSolutionLanguageOption: StateFlow<SolutionLanguageOption>
//        get() = _selectedLanguage.asStateFlow()
//
//    private var _selectedExplanationLevel =
//        MutableStateFlow(ExplanationLevelOption.SHORT_EXPLANATION)
//    val selectedExplanationLevelOption: StateFlow<ExplanationLevelOption>
//        get() = _selectedExplanationLevel.asStateFlow()
//
//    private var _descriptionText = MutableStateFlow("")
//    val descriptionText: StateFlow<String> = _descriptionText.asStateFlow()

    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

    fun updateSelectedGradeOption(newClassSelection: GradeOption) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(grade = newClassSelection)
        }
    }

    fun updateSelectedLanguageOption(newLanguageSelection: SolutionLanguageOption) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(language = newLanguageSelection)
        }
    }

    fun updateSelectedExplanationLevelOption(newExplanationLevelSelection: ExplanationLevelOption) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(explanationLevel = newExplanationLevelSelection)
        }
    }

    fun updateDescriptionText(addedText: String) {
        _parameterPropertiesState.update { currentState ->
            currentState.copy(description = addedText)
        }
    }

    fun buildPropertiesPrompt() {
        // reset
        _originalPrompt.update { PromptText.SOLVE_PROMPT.promptText }

        updateGradePrompt()
        updateLanguagePrompt()
        updateExplanationPrompt()
        _originalPrompt.update {
//            "${_originalPrompt.value} ${descriptionText.value}"
            "${_originalPrompt.value} ${parameterPropertiesState.value.description}"
        }
    }

    private fun updateGradePrompt() {

        _originalPrompt.update {
//            val selectedGradeStr = " ${_selectedGrade.value.arrayIndex}"
            val selectedGradeStr = " ${parameterPropertiesState.value.grade.arrayIndex}"
            return@update if (_originalPrompt.value.contains("(as grade+th grader)")) {
                _originalPrompt
                    .value
                    .replace(
                        "(as grade+th grader)",
                        "as ${selectedGradeStr}th grader"
                    )
            } else {
                _originalPrompt
                    .value
                    .plus(" Explain as ${selectedGradeStr}th grader.")
            }
        }
    }

    private fun updateLanguagePrompt() {
        val defaultLanguagePrompt = "(language shown on this picture)"

        _originalPrompt.update {
//            val selectedLanguageStr = " ${_selectedLanguage.value.languageName}"
            val selectedLanguageStr = " ${parameterPropertiesState.value.language.languageName}"
            return@update if (_originalPrompt.value.contains(defaultLanguagePrompt)) {
                _originalPrompt
                    .value
                    .replace(defaultLanguagePrompt, selectedLanguageStr)
            } else {
                _originalPrompt
                    .value
                    .plus(
                        " Explain only using ${selectedLanguageStr}."
                    )
            }
        }

    }

    private fun updateExplanationPrompt() {

        _originalPrompt.update {
//            val selectedExplanationStr = " ${_selectedExplanationLevel.value.code}"
            val selectedExplanationStr = " ${parameterPropertiesState.value.explanationLevel.code}"
            if (_originalPrompt.value.contains("(briefly)")) {
                _originalPrompt.value.replace(
                    "(briefly)",
                    selectedExplanationStr
                )
            } else {
                _originalPrompt.value.plus(" Explain ${selectedExplanationStr}.")
            }
        }
    }


    fun persistGradeOptionState(gradeOption: GradeOption) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistGradeOptionState(gradeOption = gradeOption)
        }
    }

    fun persistLanguageOptionState(solutionLanguageOption: SolutionLanguageOption) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistLanguageOptionState(languageOption = solutionLanguageOption)
        }
    }

    fun persistExplanationLevelOptionState(explanationLevelOption: ExplanationLevelOption) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistExplanationLevelOptionState(explanationLevelOption = explanationLevelOption)
        }
    }

    fun persistDescriptionState(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistDescriptionState(description = description)
        }
    }

    private fun readPrioritySortState() {
        
        updateTaskState(UpdateTaskState.PrioritySortState(prioritySortState = RequestDataState.Loading))
        updateTaskState(UpdateTaskState.UiState(uiState = UiState.Loading))
        try {
            viewModelScope.launch {
                dataStoreRepository.readPrioritySortState.map { TaskPriority.valueOf(it) }
                    .collect { priorityOrder ->
                        updateTaskState(
                            UpdateTaskState.PrioritySortState(
                                RequestState.Success(
                                    priorityOrder
                                )
                            )
                        )
                        updateTaskState(UpdateTaskState.UiState(uiState = UiState.Success))
                    }
            }
        } catch (e: Exception) {
            updateTaskState(UpdateTaskState.PrioritySortState(RequestDataState.Error(e)))
            updateTaskState(UpdateTaskState.UiState(uiState = UiState.Error))
        }
    }


    
}