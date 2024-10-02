package com.schoolkiller.presentation.permissions

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.schoolkiller.presentation.toast.ShowToastMessage


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestMultiplePermissionsGallery(
functionOnPermissionGranted: () -> Unit,
composableOnPermissionGranted: @Composable () -> Unit
) {

    var requestPermissionState by remember { mutableStateOf(false) }
    val permissionState =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                rememberMultiplePermissionsState(
                    listOf(
//                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES,
//                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    )
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                rememberMultiplePermissionsState(
                    listOf(
//                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            }

            else -> {
                rememberMultiplePermissionsState(
                    listOf(
//                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }

    LaunchedEffect(requestPermissionState) {
        if (requestPermissionState) {
            permissionState.launchMultiplePermissionRequest()
        }
        requestPermissionState = false
    }

    /**
     * Permission toast messages don`t showed yet, need to be addressed
     */
    when {
        !permissionState.allPermissionsGranted -> requestPermissionState = true
        permissionState.shouldShowRationale -> { ShowToastMessage.CAMERA_PERMISSION_RATIONALE.showToast() }
        !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> ShowToastMessage.DENIED_CAMERA_PERMISSION.showToast()
        else -> mutableStateOf(true)
    }

    if (!requestPermissionState) {
        functionOnPermissionGranted()
        composableOnPermissionGranted()
    }

}