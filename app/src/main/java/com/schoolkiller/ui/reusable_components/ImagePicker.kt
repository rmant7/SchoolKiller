package com.schoolkiller.ui.reusable_components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.schoolkiller.utils.UploadFileMethodOptions


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker(
    loadImages: (List<Uri>) -> Unit,
    returnToNoOption: (UploadFileMethodOptions) -> Unit,
) {


    val permissionState = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    } else {
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_IMAGES)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val clipData = data?.clipData
            val uriList = mutableListOf<Uri>()

            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    uriList.add(uri)
                }
            } else {
                data?.data?.let { uriList.add(it) }
            }
            loadImages(uriList)
        }
        returnToNoOption(UploadFileMethodOptions.NO_OPTION)
    }

    if (permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                putExtra(
                    Intent.EXTRA_ALLOW_MULTIPLE,
                    true
                )
            }
            launcher.launch(intent)
        }
    } else {
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
    }
}



