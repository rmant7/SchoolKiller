package com.schoolkiller.domain.usecases.prompt

import android.content.Context
import com.schoolkiller.R
import com.schoolkiller.domain.ExplanationLevelOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImportExplanationToPromptUseCase @Inject constructor(
@ApplicationContext
private val context: Context,
) {
    private val explanationArray: Array<String> = context.resources.getStringArray(R.array.explanations)

    fun invoke(
        explanationOption: ExplanationLevelOptions,
        originalPrompt: String
    ): String {
        val explanationString = explanationArray.getOrNull(explanationOption.arrayIndex)
            ?: "" // Handle potential out-of-bounds access
        return originalPrompt.replace("(briefly)", " in $explanationString")
    }
}