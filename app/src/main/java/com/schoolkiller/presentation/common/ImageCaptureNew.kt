package com.schoolkiller.presentation.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.FileProvider
import com.schoolkiller.domain.UploadFileMethodOptions
import java.io.File


fun imageCaptureNew(
    context: Context,
    cameraLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onPictureCapture: (Uri) -> Unit,
) {




//    BackHandler(enabled = selectedUploadMethodOption == UploadFileMethodOptions.TAKE_A_PICTURE) {
//        onBackPress(UploadFileMethodOptions.NO_OPTION)
////        (context as? Activity)?.finish()
//    }




        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a file in the app's private storage
        val imagesDir = context.filesDir.resolve("images")
        imagesDir.mkdirs()
        val photoFile = File(imagesDir, "SchoolKillerImage_${System.currentTimeMillis()}.jpeg")

        // Get a content URI using FileProvider
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        if (takePictureIntent.resolveActivity(context.packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
            onPictureCapture(photoUri)
        } else {
            Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
        }

}


