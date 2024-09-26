package com.schoolkiller.domain

import android.content.Context
import com.schoolkiller.R

enum class SolutionLanguageOption(val arrayIndex: Int) {
    ORIGINAL_TASK_LANGUAGE(0),
    ENGLISH(1),
    RUSSIAN(2),
    HEBREW(3);



    fun getString(context: Context): String {
        val solutionLanguageArray = context.resources.getStringArray(R.array.languages)
        return solutionLanguageArray[arrayIndex]
    }

    companion object {
        fun fromString(context: Context, string: String): SolutionLanguageOption? {
            val solutionLanguageArray = context.resources.getStringArray(R.array.languages)
            val index = solutionLanguageArray.indexOf(string)
            return if (index != -1) SolutionLanguageOption.entries[index] else null
        }
    }
}