package com.schoolkiller.ui.reusable_components

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.schoolkiller.utils.UploadFileMethodOptions
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageCapture(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    selectedUploadMethodOption: UploadFileMethodOptions,
    onPictureCapture: (Uri) -> Unit,
    returnToNoOption: (UploadFileMethodOptions) -> Unit,
    onBackPress: (UploadFileMethodOptions) -> Unit
) {

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val writeStoragePermissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember { ImageCapture.Builder().build() }
    var requestWriteStoragePermission by remember { mutableStateOf(false) }


    BackHandler(enabled = selectedUploadMethodOption == UploadFileMethodOptions.TAKE_A_PICTURE) {
        onBackPress(UploadFileMethodOptions.NO_OPTION)
    }

    LaunchedEffect(requestWriteStoragePermission) {
        writeStoragePermissionState.launchPermissionRequest()
        requestWriteStoragePermission = false
    }

    if (cameraPermissionState.status.isGranted) {
        LaunchedEffect(lensFacing) {
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
        Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
            content = {
                AndroidView(factory = { previewView }, modifier = modifier.fillMaxSize())
                IconButton(
                    modifier = modifier.size(90.dp),
                    onClick = { }
                ) {
                    Box(
                        modifier = modifier
                            .size(120.dp)
                            .clickable {
                                if (writeStoragePermissionState.status.isGranted) {
                                    captureImage(
                                        imageCapture,
                                        context,
                                        onPictureCapture,
                                        returnToNoOption
                                    )
                                } else {
                                    requestWriteStoragePermission = true
                                }
                            },
                        contentAlignment = Alignment.Center,
                        content = {
                            Canvas(
                                modifier = modifier.size(120.dp)
                            ) {
                                drawCircle(
                                    color = Color.White,
                                    radius = size.minDimension / 2,
                                    center = center
                                )
                            }
                            Canvas(
                                modifier = modifier.size(83.dp)
                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    radius = size.minDimension / 2,
                                    center = center
                                )
                            }

                            Canvas(
                                modifier = modifier.size(70.dp)
                            ) {
                                drawCircle(
                                    color = Color.White,
                                    radius = size.minDimension / 2,
                                    center = center
                                )
                            }
                        }
                    )
                }
            }
        )
    } else {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
    }


}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }


private fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    onPictureCapture: (Uri) -> Unit,
    returnToNoOption: (UploadFileMethodOptions) -> Unit
) {

    val name = "SchoolKillerImage.jpeg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SchoolKiller_Images")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri
                savedUri?.let { onPictureCapture(it) }
                returnToNoOption(UploadFileMethodOptions.NO_OPTION)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Failed $exception", Toast.LENGTH_SHORT).show()
                // TODO { handle error case }
            }
        }
    )
}