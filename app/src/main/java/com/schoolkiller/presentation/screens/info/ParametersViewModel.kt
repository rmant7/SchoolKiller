package com.schoolkiller.presentation.screens.info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.schoolkiller.domain.ExplanationLevelOptions
import com.schoolkiller.domain.GradeOptions
import com.schoolkiller.domain.SolutionLanguageOptions
import com.schoolkiller.domain.usecases.prompt.ConvertPromptUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor(
    private val convertPromptUseCases: ConvertPromptUseCases
) : ViewModel() {

    private var _selectedGradeOption by mutableStateOf(GradeOptions.NONE)
    val selectedGradeOption: GradeOptions
        get() = _selectedGradeOption

    private var _selectedLanguageOption by mutableStateOf(SolutionLanguageOptions.ORIGINAL_TASK_LANGUAGE)
    val selectedSolutionLanguageOption: SolutionLanguageOptions
        get() = _selectedLanguageOption

    private var _selectedExplanationLevelOption by mutableStateOf(ExplanationLevelOptions.SHORT_EXPLANATION)
    val selectedExplanationLevelOption: ExplanationLevelOptions
        get() = _selectedExplanationLevelOption

    private var _originalPrompt = MutableStateFlow<String>("")
    val originalPrompt: StateFlow<String> = _originalPrompt.asStateFlow()

    private var _additionalInfoText = MutableStateFlow<String>("")
    val additionalInfoText: StateFlow<String> = _additionalInfoText.asStateFlow()

    private var _requestGeminiResponse = MutableStateFlow<Boolean>(true)
    val requestGeminiResponse: StateFlow<Boolean> = _requestGeminiResponse.asStateFlow()

    private val _textGenerationResult = MutableStateFlow("")
    val textGenerationResult = _textGenerationResult.asStateFlow()

    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

    fun updateSelectedGradeOption(newClassSelection: GradeOptions) {
        _selectedGradeOption = newClassSelection
    }

    fun updateSelectedLanguageOption(newLanguageSelection: SolutionLanguageOptions) {
        _selectedLanguageOption = newLanguageSelection
    }

    fun updateSelectedExplanationLevelOption(newExplanationLevelSelection: ExplanationLevelOptions) {
        _selectedExplanationLevelOption = newExplanationLevelSelection
    }

    fun importExplanationToOriginalPrompt() {
        updatePrompt(
            convertPromptUseCases.importExplanationToPromptUseCase.invoke(
                explanationOption = selectedExplanationLevelOption,
                originalPrompt = originalPrompt.value
            )
        )
    }

    private fun updatePrompt(convertedPrompt: String) {
        _originalPrompt.value = convertedPrompt
    }

    fun updateAdditionalInfoText(addedText: String) {
        _additionalInfoText.value = addedText
    }

    fun updateTextGenerationResult(resultText: String?, error: Throwable? = null) {
        resultText?.let { text -> _textGenerationResult.update { text } }
        error?.let { err -> _error.update { err } }
    }

    fun importGradeToOriginalPrompt() {
        updatePrompt(
            convertPromptUseCases.importGradeToPromptUseCase.invoke(
                gradeOption = selectedGradeOption,
                originalPrompt = originalPrompt.value
            )
        )
    }

    fun importLanguageToOriginalPrompt() {
        updatePrompt(
            convertPromptUseCases.importLanguageToPromptUseCase.invoke(
                languageOption = selectedSolutionLanguageOption,
                originalPrompt = originalPrompt.value
            )
        )
    }

    fun importAdditionalInfoToOriginalPrompt() {
        updatePrompt(
            convertPromptUseCases.importAdditionalInfoToPromptUseCase.invoke(
                originalPrompt = originalPrompt.value,
                additionalInformationText = additionalInfoText.value
            )
        )
    }

    fun updateRequestGeminiResponse(requestResponse: Boolean) {
        _requestGeminiResponse.update { requestResponse }
    }
}