package com.schoolkiller.presentation.common

import android.Manifest
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
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DeleteGhostImagesButton(
    modifier: Modifier = Modifier,
    icon: Int,
    onButtonClick: () -> Unit,
    showRationale: () -> Unit,
    onPermissionDeniedPermanently: () -> Unit
) {

    var requestPermission by remember { mutableStateOf(false) }

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.READ_EXTERNAL_STORAGE
    )
    LaunchedEffect(requestPermission) {
        if (requestPermission) {
            permissionState.launchPermissionRequest()
        }
        if (requestPermission && permissionState.status.shouldShowRationale) {
            showRationale()
        }
        if  (requestPermission && !permissionState.status.isGranted && !permissionState.status.shouldShowRationale) {
            onPermissionDeniedPermanently()
        }

        requestPermission = false
    }



    IconButton(
        modifier = modifier
            .clip(CircleShape)
            .size(50.dp)
            .background(MaterialTheme.colorScheme.primary),
        onClick = {
            if (!permissionState.status.isGranted) {
                requestPermission = true
            } else {
                onButtonClick()
            }
        },
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Delete ghost images button",  // TODO { hardcode string }
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape),
            tint = MaterialTheme.colorScheme.background,
        )
    }
}