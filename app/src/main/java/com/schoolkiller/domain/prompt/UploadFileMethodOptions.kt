package com.schoolkiller.domain.prompt

import android.content.Context
import com.schoolkiller.R

enum class UploadFileMethodOptions(private val arrayIndex: Int) {

    NO_OPTION(0),
    TAKE_A_PICTURE(1),
    UPLOAD_AN_IMAGE(2),
    UPLOAD_A_FILE(3),
    PROVIDE_A_LINK(4);

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