package com.schoolkiller.utils

import android.content.Context
import com.schoolkiller.R

enum class ExplanationLevelOptions(private val arrayIndex: Int) {

    NO_EXPLANATION(0),
    SHORT_EXPLANATION(1),
    DETAILED_EXPLANATION(2);

    fun getString(context: Context): String {
        val explanationsArray = context.resources.getStringArray(R.array.explanations)
        return explanationsArray[arrayIndex]
    }

    companion object {
        fun fromString(context: Context, string: String): ExplanationLevelOptions? {
            val explanationsArray = context.resources.getStringArray(R.array.explanations)
            val index = explanationsArray.indexOf(string)
            return if (index != -1) entries[index] else null
        }
    }

}