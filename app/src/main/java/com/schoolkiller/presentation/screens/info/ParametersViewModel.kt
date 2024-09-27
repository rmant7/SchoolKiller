package com.schoolkiller.presentation.screens.info

import androidx.lifecycle.ViewModel
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.SolutionLanguageOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor() : ViewModel() {
    private var _originalPrompt = MutableStateFlow("")
    val originalPrompt: StateFlow<String> = _originalPrompt.asStateFlow()

    private var _selectedGrade = MutableStateFlow(GradeOption.NONE)
    val selectedGradeOption: StateFlow<GradeOption>
        get() = _selectedGrade.asStateFlow()

    private var _selectedLanguage = MutableStateFlow(SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE)
    val selectedSolutionLanguageOption: StateFlow<SolutionLanguageOption>
        get() = _selectedLanguage.asStateFlow()

    private var _selectedExplanationLevel = MutableStateFlow(ExplanationLevelOption.SHORT_EXPLANATION)
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

    fun buildPropertiesPrompt(
        gradeArray: Array<String>,
        languageArray: Array<String>,
        explanationArray: Array<String>
    ) {
        _originalPrompt.update { "" }
        updatePromptState(gradeArray = gradeArray)
        updateLanguagePrompt(languageArray = languageArray)
        updateExplanationPrompt(explanationArray = explanationArray)
        _originalPrompt.update {
            "${_originalPrompt.value} ${descriptionText.value}"
        }
    }

    private fun updatePromptState(gradeArray: Array<String>) {
        when (_selectedGrade.value) {
            GradeOption.NONE -> {
                if (_originalPrompt.value.contains("(as grade+th grader)")) {
                    _originalPrompt.update {
                        _originalPrompt
                            .value
                            .replace("(as grade+th grader)", "")
                    }
                }
            }

            else -> {
                val gradeString = gradeArray.getOrNull(selectedGradeOption.value.arrayIndex)
                    ?: "" // Handle potential out-of-bounds access
                _originalPrompt.update {
                    return@update if (_originalPrompt.value.contains("(as grade+th grader)")) {
                        _originalPrompt
                            .value
                            .replace("(as grade+th grader)", "as $gradeString th grader")
                    } else {
                        _originalPrompt
                            .value
                            .plus("as $gradeString th grader")
                    }
                }
            }
        }
    }

    private fun updateLanguagePrompt(languageArray: Array<String>) {
        val defaultLanguagePrompt = "(language shown on this picture)" //"(the original task language/ chosen language)"

        if (selectedSolutionLanguageOption.value != SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE) {
            val languageString = languageArray.getOrNull(selectedSolutionLanguageOption.value.arrayIndex)
                ?: "" // Handle potential out-of-bounds access
            _originalPrompt.update {
                return@update if (_originalPrompt.value.contains(defaultLanguagePrompt)) {
                    _originalPrompt
                        .value
                        .replace(defaultLanguagePrompt, " $languageString language")
                } else {
                    _originalPrompt
                        .value
                        .plus(" $languageString language")
                }
            }
        }
    }

    private fun updateExplanationPrompt(explanationArray: Array<String>) {
        val explanationString = explanationArray.getOrNull(selectedExplanationLevelOption.value.arrayIndex)
            ?: "" // Handle potential out-of-bounds access

        _originalPrompt.update {
            if (_originalPrompt.value.contains("(briefly)")) {
                _originalPrompt.value.replace("(briefly)", " in $explanationString")
            } else {
                _originalPrompt.value.plus(" in $explanationString")
            }
        }
    }
}