package com.schoolkiller.domain.usecases.prompt

import com.schoolkiller.domain.SolutionLanguageOptions
import javax.inject.Inject

class ImportLanguageToPromptUseCase @Inject constructor() {

    fun invoke(
        languageOption: SolutionLanguageOptions,
        originalPrompt: String,
    ): String {
        return originalPrompt.replace(
            "(language shown on this picture)", " ${languageOption.code}")
    }

}