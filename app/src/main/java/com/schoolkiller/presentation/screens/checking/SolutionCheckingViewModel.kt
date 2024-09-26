package com.schoolkiller.presentation.screens.checking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.schoolkiller.domain.GradeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SolutionCheckingViewModel @Inject constructor() : ViewModel() {
    private var _originalPrompt = MutableStateFlow<String>("")
    val originalPrompt: StateFlow<String> = _originalPrompt

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
        _originalPrompt.value = convertedPrompt
    }

    fun updateTextGenerationResult(resultText: String?, error: Throwable? = null) {
        resultText?.let { text -> _textGenerationResult.update { text } }
        error?.let { err -> _error.update { err } }
    }

    fun importGradeToOriginalPrompt(gradeArray: Array<String>) {
        updatePrompt(
            getGradePrompt(gradeArray)
        )
    }

    fun updateSelectedRateMax(newRateMax: Int) {
        _selectedRateMax = newRateMax
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
}