package com.schoolkiller.domain.usecases.prompt

import android.content.Context
import com.schoolkiller.R
import com.schoolkiller.domain.GradeOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImportGradeToPromptUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val gradesArray: Array<String> = context.resources.getStringArray(R.array.grades)
//    private val originalPrompt: String = context.getString(R.string.prompt_text)

    fun invoke(
        gradeOption: GradeOptions,
        originalPrompt: String
    ): String {
        return when (gradeOption) {
            GradeOptions.NONE -> {
                originalPrompt.replace("(as grade+th grader)", "")
            }

            else -> {
                val gradeString = gradesArray.getOrNull(gradeOption.arrayIndex)
                    ?: "" // Handle potential out-of-bounds access
                originalPrompt.replace("(as grade+th grader)", "as $gradeString th grader")
            }
        }
    }

}