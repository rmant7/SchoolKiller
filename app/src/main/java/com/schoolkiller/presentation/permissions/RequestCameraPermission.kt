package com.schoolkiller.presentation.permissions

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.schoolkiller.presentation.toast.ShowToastMessage

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission(
    functionOnPermissionGranted: () -> Unit,
    composableOnPermissionGranted: @Composable () -> Unit
) {

    var requestCameraPermission by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(requestCameraPermission) {
        if (requestCameraPermission) {
            cameraPermissionState.launchPermissionRequest()
        }
        requestCameraPermission = false
    }

    /**
     * Permission toast messages don`t showed yet, need to be addressed
     */
    when {
        !cameraPermissionState.status.isGranted -> requestCameraPermission = true
        cameraPermissionState.status.shouldShowRationale -> ShowToastMessage.CAMERA_PERMISSION_RATIONALE.showToast()
        !cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale -> ShowToastMessage.DENIED_CAMERA_PERMISSION.showToast()
        else -> mutableStateOf(true)
    }

    if (!requestCameraPermission) {
        functionOnPermissionGranted()
        composableOnPermissionGranted()
    }

    /**
     * Permission toast messages don`t showed yet, need to be addressed
     */

}