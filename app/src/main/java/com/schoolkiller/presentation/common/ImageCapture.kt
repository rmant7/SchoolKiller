package com.schoolkiller.presentation.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.schoolkiller.domain.UploadFileMethodOptions
import java.io.File
import java.io.FileOutputStream

//lateinit var cameraLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageCapture(
    context: Context,
    cameraLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    selectedUploadMethodOption: UploadFileMethodOptions,
    onBackPress: (UploadFileMethodOptions) -> Unit,
    onPictureCapture: (Uri) -> Unit,
    returnToNoOption: (UploadFileMethodOptions) -> Unit
) {
    var capturedImage by remember { mutableStateOf<Uri?>(null) }

//    cameraLauncher =
//        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
//            val imageUri = result.data?.data
//            if (imageUri != null) {
//                capturedImage = imageUri
//            }
//        }

    var requestPermissionsState by remember { mutableStateOf(false) }

    val permissionsState =
        /* Even thought camera will save the image to the device storage,
        extra permissions don`t needed and also they caused troubles showing an empty screen */
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES,
//                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    )
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            }

            else -> {
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }


    BackHandler(enabled = selectedUploadMethodOption == UploadFileMethodOptions.TAKE_A_PICTURE) {
        onBackPress(UploadFileMethodOptions.NO_OPTION)
//        (context as? Activity)?.finish()
    }

    LaunchedEffect(requestPermissionsState) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
        requestPermissionsState = false
    }

    if (permissionsState.allPermissionsGranted) {
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
    } else {
        requestPermissionsState = true
    }
}


private fun captureImage(
    imageUri: Uri,
    onPictureCapture: (Uri) -> Unit,
    returnToNoOption: (UploadFileMethodOptions) -> Unit
) {
    onPictureCapture(imageUri)
    returnToNoOption(UploadFileMethodOptions.NO_OPTION)
}
