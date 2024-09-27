package com.schoolkiller.domain

import android.content.Context
import com.schoolkiller.R

enum class ExplanationLevelOptions(private val arrayIndex: Int, val code: String) {

    SHORT_EXPLANATION(0, "briefly"),
    DETAILED_EXPLANATION(1, "in detail");

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