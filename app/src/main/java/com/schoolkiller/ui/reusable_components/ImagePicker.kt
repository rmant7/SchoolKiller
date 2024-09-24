package com.schoolkiller.ui.reusable_components

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.schoolkiller.utils.UploadFileMethodOptions


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker(
    loadImages: (List<Uri>) -> Unit,
    returnToNoOption: (UploadFileMethodOptions) -> Unit,
) {


    val permissionsState =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    )
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            }

            else -> {
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    )
                )
            }
        }

    var requestPermissionsState by remember { mutableStateOf(false) }

    LaunchedEffect(requestPermissionsState) {
        permissionsState.launchMultiplePermissionRequest()
        requestPermissionsState = false
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val clipData = data?.clipData
            val uriList = mutableListOf<Uri>()

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    // Handle Android 14+ photo picker
//                    handlePhotoPickerResultAndroid14(context, data, loadImages, returnToNoOption)
                }
                else -> {
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            uriList.add(uri)
                        }
                    } else {
                        data?.data?.let { uriList.add(it) }
                    }
                    loadImages(uriList)
                    returnToNoOption(UploadFileMethodOptions.NO_OPTION)
                }
            }
        }
    }

    if (permissionsState.allPermissionsGranted) {
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
        requestPermissionsState = true
    }
}



