package com.schoolkiller.presentation.screens.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.repositories.DataStoreRepository
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.SolutionLanguageOption
import com.schoolkiller.domain.model.ParameterProperties
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
class ParametersViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _parametersPropertiesState = MutableStateFlow(ParameterProperties())
    val parametersPropertiesState: StateFlow<ParameterProperties> = _parametersPropertiesState
        .onStart {
            /** This is like the init block */
            readSolvePromptState()
            readGradeOptionState()
            readLanguageOptionState()
            readExplanationLevelOptionState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ParameterProperties()
        )

    /** we can use this for handling errors, easier debugging with logging, and
     * show circular indicator when something is delaying to showed in the UI */
    private val _parametersScreenRequestState = MutableStateFlow<RequestState<ParameterProperties>>(
        RequestState.Idle
    )
    val parametersScreenRequestState: StateFlow<RequestState<ParameterProperties>> =
        _parametersScreenRequestState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = RequestState.Idle
            )

    /*
    init {
        readSolvePromptState()
        readDescriptionOptionState()
        readGradeOptionState()
        readLanguageOptionState()
        readExplanationLevelOptionState()
    }

     */


    fun updateSelectedGradeOption(newClassSelection: GradeOption) {
        _parametersPropertiesState.update { currentState ->
            currentState.copy(grade = newClassSelection)
        }
        persistGradeOptionState(newClassSelection)
    }

    fun updateSelectedLanguageOption(newLanguageSelection: SolutionLanguageOption) {
        _parametersPropertiesState.update { currentState ->
            currentState.copy(language = newLanguageSelection)
        }
        persistLanguageOptionState(newLanguageSelection)
    }

    fun updateSelectedExplanationLevelOption(newExplanationLevelSelection: ExplanationLevelOption) {
        _parametersPropertiesState.update { currentState ->
            currentState.copy(explanationLevel = newExplanationLevelSelection)
        }
        persistExplanationLevelOptionState(newExplanationLevelSelection)
    }

    fun updateDescriptionText(addedText: String) {
        _parametersPropertiesState.update { currentState ->
            currentState.copy(description = addedText)
        }
        persistDescriptionState(addedText)
    }

    private fun updateSolvePromptText(newSolvePromptText: String) {
        _parametersPropertiesState.update { currentState ->
            currentState.copy(solvePromptText = newSolvePromptText)
        }
        persistSolvePromptTextState(newSolvePromptText)
    }

/*
    fun buildSolvingPrompt() {
        val originalPrompt = PromptText.SOLVE_PROMPT.promptText
        val selectedGradeStr = "${parametersPropertiesState.value.grade.arrayIndex}"
        // val selectedLanguageStr = "${parametersPropertiesState.value.language.languageName}"
        val selectedExplanationStr = "${parametersPropertiesState.value.explanationLevel.code}"
        val description = " ${parametersPropertiesState.value.description}"

        val promptWithGradeOption = if (originalPrompt.contains("(as grade+th grader)")) {
            originalPrompt.replace("(as grade+th grader)", "as ${selectedGradeStr}th grader")
        } else {
            originalPrompt
        }
        /*
                val promptWithLanguageOption =
                    if (promptWithGradeOption.contains("(language in the user's task in prompt.)")) {
                        promptWithGradeOption.replace(
                            "(language in the user's task in prompt.)",
                            "${selectedLanguageStr.uppercase()} ONLY"
                        )
                    } else {
                        promptWithGradeOption
                    }*/
        val promptWithExplanationOption = if (promptWithGradeOption.contains("(briefly)")) {
            promptWithGradeOption.replace("(briefly)", selectedExplanationStr)
        } else {
            promptWithGradeOption
        }
        val promptWithDescription = promptWithExplanationOption.plus(description)

        updateSolvePromptText(promptWithDescription)
    }
*/

    // Alternative string builder
    fun buildSolvingPrompt(recognizedText: String?) : String{
        val selectedGradeStr = "${parametersPropertiesState.value.grade.arrayIndex}"
        val selectedExplanationStr = parametersPropertiesState.value.explanationLevel.code
        val description = " ${parametersPropertiesState.value.description}"

        val solvingPrompt = StringBuilder()
            .append("Solve this task as $selectedGradeStr}th grader. ")
            .append("Show the solution and explain $selectedExplanationStr how to get there. ")
            .append("If there are multiple tasks, solve them all separately. ")
            .append("Use a chain of thoughts before answering. ")
            .append("$description ")
            .append("User's solution is: $recognizedText")
        return solvingPrompt.toString()
    }

    fun buildSystemInstruction(hasHtmlTags: Boolean): String {
        val selectedLanguageStr = parametersPropertiesState.value.language.languageName

        val systemInstruction = StringBuilder()
            .append("Answer only in ${selectedLanguageStr}.")

        if (hasHtmlTags)
            systemInstruction.append(PromptText.HTML_REQUEST.promptText)
        else
            systemInstruction.append(PromptText.NO_HTML_REQUEST.promptText)

        return systemInstruction.toString()
    }


    /** This should be inside buildSolvingPrompt.
     *  Maybe even let user to choose if they want to do OCR or not.
     */
    fun getPrompt(recognizedText: String?): String {
        return parametersPropertiesState.value.solvePromptText +
                " User's solution is: $recognizedText"
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
            updateSolvePromptText("")
            //updateSolvePromptText(PromptText.SOLVE_PROMPT.promptText)
        }
    }


}