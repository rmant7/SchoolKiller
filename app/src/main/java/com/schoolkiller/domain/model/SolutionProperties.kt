package com.schoolkiller.domain.model

import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.SolutionLanguageOption

data class SolutionProperties(
    val grade: GradeOption = GradeOption.NONE,
    val solutionPromptText: String = PromptText.CHECK_SOLUTION_PROMPT.promptText,
    val textGenerationResult: String = ""
//    val language: SolutionLanguageOption = SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE,
//    val explanationLevel: ExplanationLevelOption = ExplanationLevelOption.SHORT_EXPLANATION,
//    val description: String = ""
)
