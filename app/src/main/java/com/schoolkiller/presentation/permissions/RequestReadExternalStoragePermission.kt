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
fun RequestReadExternalStoragePermission(
    functionOnPermissionGranted: () -> Unit,
    composableOnPermissionGranted: @Composable () -> Unit
) {

    var requestReadExternalStoragePermission by remember { mutableStateOf(false) }
    val readExternalStoragePermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    LaunchedEffect(requestReadExternalStoragePermission) {
        if (requestReadExternalStoragePermission) {
            readExternalStoragePermissionState.launchPermissionRequest()
        }
        requestReadExternalStoragePermission = false
    }

    /**
     * Permission toast messages don`t showed yet, need to be addressed
     */
    when {
        !readExternalStoragePermissionState.status.isGranted -> requestReadExternalStoragePermission = true
        readExternalStoragePermissionState.status.shouldShowRationale -> ShowToastMessage.READ_STORAGE_PERMISSION_RATIONALE.showToast()
        !readExternalStoragePermissionState.status.isGranted && !readExternalStoragePermissionState.status.shouldShowRationale -> ShowToastMessage.DENIED_READ_STORAGE_PERMISSION.showToast()
        else -> mutableStateOf(true)
    }

    if (!requestReadExternalStoragePermission) {
        functionOnPermissionGranted()
        composableOnPermissionGranted()
    }

}