package com.schoolkiller.presentation.common

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.schoolkiller.domain.UploadFileMethodOptions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CaptureImage(
    selectedUploadMethodOption: UploadFileMethodOptions,
    onOpenCamera: @Composable () -> Unit,
) {

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

    LaunchedEffect(requestPermissionsState) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
        requestPermissionsState = false
    }

    if (selectedUploadMethodOption == UploadFileMethodOptions.TAKE_A_PICTURE) {
        if (permissionsState.allPermissionsGranted) {
            onOpenCamera()
        } else {
            requestPermissionsState = true
        }
    }
}