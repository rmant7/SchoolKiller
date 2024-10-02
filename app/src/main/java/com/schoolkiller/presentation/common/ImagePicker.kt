package com.schoolkiller.presentation.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.schoolkiller.domain.UploadFileMethodOptions
import timber.log.Timber

/**
 * Is going to be deleted
 */

//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun ImagePicker(
//    context: Context,
//    selectedUploadMethodOption: UploadFileMethodOptions,
//    pickMultipleMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
//) {
//    var requestReadMediaPermissionsState by remember { mutableStateOf(false) }
//    var requestReadExternalStoragePermissionsState by remember { mutableStateOf(false) }
//
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        val readMediaPermissionState =
//            rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
//
//        LaunchedEffect(requestReadMediaPermissionsState) {
//                readMediaPermissionState.launchPermissionRequest()
//            requestReadMediaPermissionsState = false
//        }
//
//        if (selectedUploadMethodOption == UploadFileMethodOptions.UPLOAD_AN_IMAGE) {
//            if (!readMediaPermissionState.status.isGranted) {
//                requestReadMediaPermissionsState = true
//            } else {
//                try {
//                    pickMultipleMediaLauncher.launch(
//                        PickVisualMediaRequest(
//                            ActivityResultContracts.PickVisualMedia.ImageOnly
//                        )
//                    )
//                } catch (e: Exception) {
//                    Timber.e("Error launching image picker: ${e.message}")
//                    // ... handle the exception ...
//                }
//            }
//        }
//
//    } else {
//        val readExternalStoragePermissionState =
//            rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
//
//        LaunchedEffect(requestReadExternalStoragePermissionsState) {
//                readExternalStoragePermissionState.launchPermissionRequest()
//            requestReadExternalStoragePermissionsState = false
//        }
//
//        if (selectedUploadMethodOption == UploadFileMethodOptions.UPLOAD_AN_IMAGE) {
//            if (!readExternalStoragePermissionState.status.isGranted) {
//                requestReadExternalStoragePermissionsState = true
//            } else {
//                try {
//                    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
//                    pickMultipleMediaLauncher.launch(
//                        PickVisualMediaRequest(
//                            ActivityResultContracts.PickVisualMedia.ImageOnly
//                        )
//                    )
//                } catch (e: Exception){
//                    e.printStackTrace()
//                }
//
//            }
//        }
//    }
//}











