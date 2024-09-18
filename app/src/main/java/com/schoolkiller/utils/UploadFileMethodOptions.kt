package com.schoolkiller.utils

import android.content.Context
import com.schoolkiller.R

enum class UploadFileMethodOptions(private val arrayIndex: Int) {

    TAKE_A_PICTURE(0),
    UPLOAD_AN_IMAGE(1),
    UPLOAD_A_FILE(2),
    PROVIDE_A_LINK(3);

    fun getString(context: Context): String {
        val uploadMethodsArray = context.resources.getStringArray(R.array.upload_file_methods)
        return uploadMethodsArray[arrayIndex]
    }

    companion object {
        fun fromString(context: Context, string: String): UploadFileMethodOptions? {
            val uploadMethodsArray = context.resources.getStringArray(R.array.upload_file_methods)
            val index = uploadMethodsArray.indexOf(string)
            return if (index != -1) entries[index] else null
        }
    }

}