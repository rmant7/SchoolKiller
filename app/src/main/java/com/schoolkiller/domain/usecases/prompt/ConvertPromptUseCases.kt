package com.schoolkiller.domain.usecases.prompt

import javax.inject.Inject

class ConvertPromptUseCases @Inject constructor(
    val importGradeToPromptUseCase: ImportGradeToPromptUseCase,
    val importLanguageToPromptUseCase: ImportLanguageToPromptUseCase,
    val importAdditionalInfoToPromptUseCase: ImportAdditionalInfoToPromptUseCase
)