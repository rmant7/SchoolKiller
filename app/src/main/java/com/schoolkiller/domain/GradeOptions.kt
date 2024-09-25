package com.schoolkiller.domain

import android.content.Context
import com.schoolkiller.R

enum class GradeOptions(val arrayIndex: Int) {
    NONE(0),
    CLASS_1(1),
    CLASS_2(2),
    CLASS_3(3),
    CLASS_4(4),
    CLASS_5(5),
    CLASS_6(6),
    CLASS_7(7),
    CLASS_8(8),
    CLASS_9(9),
    CLASS_10(10),
    CLASS_11(11),
    CLASS_12(12);

    fun getString(context: Context): String {
        val classArray = context.resources.getStringArray(R.array.grades)
        return classArray[arrayIndex]
    }

    companion object {
        fun fromString(context: Context, string: String): GradeOptions? {
            val classArray = context.resources.getStringArray(R.array.grades)
            val index = classArray.indexOf(string)
            return if (index != -1) entries[index] else null
        }
    }
}