package com.schoolkiller.ui.reusable_components

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.schoolkiller.utils.UploadFileMethodOptions


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker(
    selectedUploadMethodOption: UploadFileMethodOptions,
    pickMultipleMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
) {

    /* Android 14+ permission causes problems at the moment with infinity loop of asking permission
    READ_MEDIA_VISUAL_USER_SELECTED permission is commented out in Manifest

    val readMediaSelectedPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)

    var requestReadMediaSelectedPermissionsState by remember { mutableStateOf(false) }

    LaunchedEffect(requestReadMediaSelectedPermissionsState) {
            readMediaSelectedPermissionState.launchPermissionRequest()
            requestReadMediaSelectedPermissionsState = false
    }

     */

    // Android 12+ permission
    val readMediaPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)

    // Android 11- permission
    val readExternalStoragePermissionState =
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)


    var requestReadMediaPermissionsState by remember { mutableStateOf(false) }
    var requestReadExternalStoragePermissionsState by remember { mutableStateOf(false) }


    LaunchedEffect(requestReadMediaPermissionsState) {
        readMediaPermissionState.launchPermissionRequest()
        requestReadMediaPermissionsState = false
    }

    LaunchedEffect(requestReadExternalStoragePermissionsState) {
        readExternalStoragePermissionState.launchPermissionRequest()
        requestReadExternalStoragePermissionsState = false
    }


    if (selectedUploadMethodOption == UploadFileMethodOptions.UPLOAD_AN_IMAGE) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (!readMediaPermissionState.status.isGranted) {
                    requestReadMediaPermissionsState = true
                } else {
                    pickMultipleMediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            }
            /* Allow access to individuals images causes a loop of requesting permission for Android 14 devices
            READ_MEDIA_VISUAL_USER_SELECTED permission is commented out in Manifest

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                if (!readMediaSelectedPermissionState.status.isGranted) {
                    requestReadMediaSelectedPermissionsState = true
                    } else {
                    pickMultipleMediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            }

             */

            else -> {
                if (!readExternalStoragePermissionState.status.isGranted) {
                    requestReadExternalStoragePermissionsState = true
                } else {
                    pickMultipleMediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            }
        }
    }

}




