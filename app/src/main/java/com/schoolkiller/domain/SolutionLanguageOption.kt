package com.schoolkiller.domain

import android.content.Context
import com.schoolkiller.R

enum class SolutionLanguageOption(private val arrayIndex: Int, val languageName: String) {
    ORIGINAL_TASK_LANGUAGE(0, "Language shown on picture"),
    ENGLISH(1, "English language"),
    RUSSIAN(2, "Russian language"),
    HEBREW(3, "Hebrew language");


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