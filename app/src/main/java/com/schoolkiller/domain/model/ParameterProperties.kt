package com.schoolkiller.domain.model

import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.SolutionLanguageOption

data class ParameterProperties(
    val grade: GradeOption = GradeOption.CLASS_5,
    val language: SolutionLanguageOption = SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE,
    val explanationLevel: ExplanationLevelOption = ExplanationLevelOption.SHORT_EXPLANATION,
    val description: String = "",
    val solvePromptText: String = ""/* PromptText.SOLVE_PROMPT.promptText */,
    val error: Throwable? = null
)
