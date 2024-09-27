package com.schoolkiller.domain

import android.content.Context
import com.schoolkiller.R

enum class SolutionLanguageOptions(private val arrayIndex: Int, val code: String) {
    ORIGINAL_TASK_LANGUAGE(0, "Language shown on picture"),
    ENGLISH(1, "English language"),
    RUSSIAN(2, "Russian language"),
    HEBREW(3, "Hebrew language");


    fun getString(context: Context): String {
        val solutionLanguageArray = context.resources.getStringArray(R.array.languages)
        return solutionLanguageArray[arrayIndex]
    }

    companion object {
        fun fromString(context: Context, string: String): SolutionLanguageOptions? {
            val solutionLanguageArray = context.resources.getStringArray(R.array.languages)
            val index = solutionLanguageArray.indexOf(string)
            return if (index != -1) SolutionLanguageOptions.entries[index] else null
        }
    }
}