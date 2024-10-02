package com.schoolkiller.presentation.common

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import com.schoolkiller.presentation.toast.ShowToastMessage


@Composable
fun OpenCamera(
    cameraLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) {
    /* this method for checking if a camera app exist don`t let the camera to open on Android 13 device and above
        if (takePictureIntent.resolveActivity(context.packageManager) != null) { }
     */

    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    try {
        cameraLauncher.launch(takePictureIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        ShowToastMessage.CAMERA_FAIL_TO_OPEN.showToast()
    }

}
