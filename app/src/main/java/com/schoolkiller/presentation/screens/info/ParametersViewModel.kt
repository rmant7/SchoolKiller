package com.schoolkiller.presentation.screens.info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.SolutionLanguageOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor() : ViewModel() {
    private var _originalPrompt = MutableStateFlow("")
    val originalPrompt: StateFlow<String> = _originalPrompt.asStateFlow()

    private var _selectedGrade by mutableStateOf(GradeOption.NONE)
    val selectedGradeOption: GradeOption
        get() = _selectedGrade

    private var _selectedLanguage by mutableStateOf(SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE)
    val selectedSolutionLanguageOption: SolutionLanguageOption
        get() = _selectedLanguage

    private var _selectedExplanationLevel by mutableStateOf(ExplanationLevelOption.SHORT_EXPLANATION)
    val selectedExplanationLevelOption: ExplanationLevelOption
        get() = _selectedExplanationLevel

    private var _descriptionText = MutableStateFlow("")
    val descriptionText: StateFlow<String> = _descriptionText.asStateFlow()

    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

    fun updateSelectedGradeOption(newClassSelection: GradeOption) {
        _selectedGrade = newClassSelection
    }

    fun updateSelectedLanguageOption(newLanguageSelection: SolutionLanguageOption) {
        _selectedLanguage = newLanguageSelection
    }

    fun updateSelectedExplanationLevelOption(newExplanationLevelSelection: ExplanationLevelOption) {
        _selectedExplanationLevel = newExplanationLevelSelection
    }

    fun updateDescriptionText(addedText: String) {
        _descriptionText.value = addedText
    }

    fun updatePropertiesPrompt(
        gradeArray: Array<String>,
        languageArray: Array<String>,
        explanationArray: Array<String>
    ) {
        updatePrompt(getGradePrompt(gradeArray = gradeArray))
        updatePrompt(getLanguagePrompt(languageArray = languageArray))
        updatePrompt(getExplanationPrompt(explanationArray))
        updatePrompt("$originalPrompt ${descriptionText.value}")
    }

    private fun updatePrompt(convertedPrompt: String) {
        _originalPrompt.value = convertedPrompt
    }

    private fun getGradePrompt(
        gradeArray: Array<String>
    ): String {
        return when (selectedGradeOption) {
            GradeOption.NONE -> {
                originalPrompt.value.replace("(as grade+th grader)", "")
            }

            else -> {
                val gradeString = gradeArray.getOrNull(selectedGradeOption.arrayIndex)
                    ?: "" // Handle potential out-of-bounds access
                originalPrompt.value.replace("(as grade+th grader)", "as $gradeString th grader")
            }
        }
    }

    private fun getLanguagePrompt(
        languageArray: Array<String>
    ): String {
        val defaultLanguagePrompt =
            "(language shown on this picture)" //"(the original task language/ chosen language)"
        if (selectedSolutionLanguageOption != SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE) {
            val languageString = languageArray.getOrNull(selectedSolutionLanguageOption.arrayIndex)
                ?: "" // Handle potential out-of-bounds access
            return originalPrompt.value.replace(defaultLanguagePrompt, " $languageString language")
        }
        return originalPrompt.value
    }

    private fun getExplanationPrompt(
        explanationArray: Array<String>
    ): String {
        val explanationString = explanationArray.getOrNull(selectedExplanationLevelOption.arrayIndex)
            ?: "" // Handle potential out-of-bounds access
        return originalPrompt.value.replace("(briefly)", " in $explanationString")
    }
}