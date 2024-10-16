package com.schoolkiller.domain.model

import com.google.android.gms.ads.AdView
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText

data class SolutionProperties(
    val adView: AdView? = null,
    val grade: GradeOption = GradeOption.CLASS_5,
    val solutionPromptText: String = "" /* PromptText.CHECK_SOLUTION_PROMPT.promptText */,
    val textGenerationResult: String = "",
    val error: Throwable? = null,
    val selectedRateMax: Int = 100
)
