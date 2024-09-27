package com.schoolkiller.presentation.screens.info

import androidx.lifecycle.ViewModel
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.SolutionLanguageOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor() : ViewModel() {
    private var _originalPrompt = MutableStateFlow(PromptText.SOLVE_PROMPT.promptText)
    val originalPrompt: StateFlow<String> = _originalPrompt.asStateFlow()

    private var _selectedGrade = MutableStateFlow(GradeOption.NONE)
    val selectedGradeOption: StateFlow<GradeOption>
        get() = _selectedGrade.asStateFlow()

    private var _selectedLanguage = MutableStateFlow(SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE)
    val selectedSolutionLanguageOption: StateFlow<SolutionLanguageOption>
        get() = _selectedLanguage.asStateFlow()

    private var _selectedExplanationLevel =
        MutableStateFlow(ExplanationLevelOption.SHORT_EXPLANATION)
    val selectedExplanationLevelOption: StateFlow<ExplanationLevelOption>
        get() = _selectedExplanationLevel.asStateFlow()

    private var _descriptionText = MutableStateFlow("")
    val descriptionText: StateFlow<String> = _descriptionText.asStateFlow()

    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

    fun updateSelectedGradeOption(newClassSelection: GradeOption) {
        _selectedGrade.update { newClassSelection }
    }

    fun updateSelectedLanguageOption(newLanguageSelection: SolutionLanguageOption) {
        _selectedLanguage.update { newLanguageSelection }
    }

    fun updateSelectedExplanationLevelOption(newExplanationLevelSelection: ExplanationLevelOption) {
        _selectedExplanationLevel.update { newExplanationLevelSelection }
    }

    fun updateDescriptionText(addedText: String) {
        _descriptionText.update { addedText }
    }

    fun buildPropertiesPrompt() {
        // reset
        _originalPrompt.update { PromptText.SOLVE_PROMPT.promptText }

        updateGradePrompt()
        updateLanguagePrompt()
        updateExplanationPrompt()
        _originalPrompt.update {
            "${_originalPrompt.value} ${descriptionText.value}"
        }
    }

    private fun updateGradePrompt() {

        _originalPrompt.update {
            val selectedGradeStr = " ${_selectedGrade.value.arrayIndex}"
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
            val selectedLanguageStr = " ${_selectedLanguage.value.languageName}"
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
            val selectedExplanationStr = " ${_selectedExplanationLevel.value.code}"
            if (_originalPrompt.value.contains("(briefly)")) {
                println("CONTAINS")
                _originalPrompt.value.replace(
                    "(briefly)",
                    selectedExplanationStr
                )
            } else {
                _originalPrompt.value.plus(" Explain ${selectedExplanationStr}.")
            }
        }
    }
}