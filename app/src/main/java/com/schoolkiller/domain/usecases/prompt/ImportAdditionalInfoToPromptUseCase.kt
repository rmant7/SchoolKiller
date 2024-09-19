package com.schoolkiller.domain.usecases.prompt

import javax.inject.Inject

class ImportAdditionalInfoToPromptUseCase @Inject constructor() {

    fun invoke(
        originalPrompt: String,
        additionalInformationText: String
    ): String {
        return "$originalPrompt $additionalInformationText"
    }
}