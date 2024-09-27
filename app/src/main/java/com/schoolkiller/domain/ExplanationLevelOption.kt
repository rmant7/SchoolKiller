package com.schoolkiller.domain

import android.content.Context
import com.schoolkiller.R

enum class ExplanationLevelOption(val arrayIndex: Int, val code: String) {

    SHORT_EXPLANATION(0, "briefly"),
    DETAILED_EXPLANATION(1, "in detail");

    fun getString(context: Context): String {
        val explanationsArray = context.resources.getStringArray(R.array.explanations)
        return explanationsArray[arrayIndex]
    }
}