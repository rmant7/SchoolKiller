package com.schoolkiller.domain.usecases.prompt

import com.schoolkiller.domain.GradeOptions
import javax.inject.Inject

class ImportGradeToPromptUseCase @Inject constructor() {

    fun invoke(
        gradeOption: GradeOptions,
        originalPrompt: String
    ): String {
        return originalPrompt.replace(
            "(as grade+th grader)",
            "as ${gradeOption.code} th grader"
        )
    }

}