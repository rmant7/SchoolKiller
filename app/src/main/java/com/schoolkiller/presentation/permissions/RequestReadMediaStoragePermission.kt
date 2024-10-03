package com.schoolkiller.presentation.permissions

import android.Manifest
import android.os.Build
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
import com.google.accompanist.permissions.shouldShowRationale
import com.schoolkiller.presentation.toast.ShowToastMessage

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestReadMediaStoragePermission(
    functionOnPermissionGranted: () -> Unit,
    composableOnPermissionGranted: @Composable () -> Unit
) {

    var requestReadMediaStoragePermission by remember { mutableStateOf(false) }
    val readMediaStoragePermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)

    LaunchedEffect(requestReadMediaStoragePermission) {
        if (requestReadMediaStoragePermission) {
            readMediaStoragePermissionState.launchPermissionRequest()
        }
        requestReadMediaStoragePermission = false
    }

    /**
     * Permission toast messages don`t showed yet, need to be addressed
     */
    when {
        !readMediaStoragePermissionState.status.isGranted -> requestReadMediaStoragePermission = true
        readMediaStoragePermissionState.status.shouldShowRationale -> ShowToastMessage.READ_MEDIA_PERMISSION_RATIONALE.showToast()
        !readMediaStoragePermissionState.status.isGranted && !readMediaStoragePermissionState.status.shouldShowRationale -> ShowToastMessage.DENIED_READ_MEDIA_PERMISSION.showToast()
        else -> mutableStateOf(true)
    }

    if (!requestReadMediaStoragePermission) {
        functionOnPermissionGranted()
        composableOnPermissionGranted()
    }

}