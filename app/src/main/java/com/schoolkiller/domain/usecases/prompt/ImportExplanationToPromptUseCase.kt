package com.schoolkiller.domain.usecases.prompt

import com.schoolkiller.domain.ExplanationLevelOptions
import javax.inject.Inject

class ImportExplanationToPromptUseCase @Inject constructor() {

    fun invoke(
        explanationOption: ExplanationLevelOptions,
        originalPrompt: String
    ): String {
        return originalPrompt.replace("(briefly)",
            " ${explanationOption.code}")
    }
}