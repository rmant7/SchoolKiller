package com.schoolkiller.domain.usecases.prompt

import android.content.Context
import com.schoolkiller.R
import com.schoolkiller.utils.SolutionLanguageOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImportLanguageToPromptUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val languagesArray: Array<String> = context.resources.getStringArray(R.array.languages)
//    private val originalPrompt: String = context.getString(R.string.prompt_text)

    fun invoke(
        languageOption: SolutionLanguageOptions,
        originalPrompt: String,
    ): String {
        return when (languageOption) {
            SolutionLanguageOptions.ORIGINAL_TASK_LANGUAGE -> {
                val languageString = languagesArray.getOrNull(languageOption.arrayIndex)
                    ?: "" // Handle potential out-of-bounds access
//                originalPrompt.replace("/ chosen language", "")
                originalPrompt.replace("(the original task language/ chosen language)", " $languageString")
            }

            else -> {
                val languageString = languagesArray.getOrNull(languageOption.arrayIndex)
                    ?: "" // Handle potential out-of-bounds access
                originalPrompt.replace("(the original task language/ chosen language)", " $languageString language")
            }
        }
    }

}