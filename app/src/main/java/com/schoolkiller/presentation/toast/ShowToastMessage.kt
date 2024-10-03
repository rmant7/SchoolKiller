package com.schoolkiller.presentation.toast

import android.content.Context
import android.widget.Toast
import com.schoolkiller.R
import dagger.hilt.android.qualifiers.ApplicationContext

sealed class ShowToastMessage(
    val stringMessage: Int
) {

    companion object {
        @ApplicationContext
        private lateinit var context: Context

        fun init(context: Context) {
            this.context = context
        }
    }

    fun showToast ()  {
        val message = context.getString(stringMessage)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    data object SOMETHING_WENT_WRONG : ShowToastMessage(stringMessage = R.string.something_went_wrong)
    data object IMAGE_FAIL__TO_LOAD_TO_THE_LIST : ShowToastMessage(stringMessage = R.string.fail_to_load_Uri)
    data object CORRUPTED_LOADED_FILE : ShowToastMessage(stringMessage = R.string.corrupted_loaded_file)
    data object CAMERA_FAIL_TO_OPEN : ShowToastMessage(stringMessage = R.string.camera_fail_to_open)
    data object DENIED_CAMERA_PERMISSION : ShowToastMessage(stringMessage = R.string.permanent_denied_camera_permission_explanation)
    data object DENIED_READ_STORAGE_PERMISSION : ShowToastMessage(stringMessage = R.string.permanent_denied_read_storage_permission_explanation)
    data object DENIED_WRITE_STORAGE_PERMISSION : ShowToastMessage(stringMessage = R.string.permanent_denied_write_storage_permission_explanation)
    data object DENIED_READ_MEDIA_PERMISSION : ShowToastMessage(stringMessage = R.string.permanent_denied_read_media_permission_explanation)
    data object CAMERA_PERMISSION_RATIONALE : ShowToastMessage(stringMessage = R.string.camera_permission_rationale)
    data object READ_STORAGE_PERMISSION_RATIONALE : ShowToastMessage(stringMessage = R.string.read_storage_permission_rationale)
    data object WRITE_STORAGE_PERMISSION_RATIONALE : ShowToastMessage(stringMessage = R.string.write_storage_permission_rationale)
    data object READ_MEDIA_PERMISSION_RATIONALE : ShowToastMessage(stringMessage = R.string.read_media_permission_rationale)



}

