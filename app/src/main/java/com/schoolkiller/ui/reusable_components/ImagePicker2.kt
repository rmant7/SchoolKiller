package com.schoolkiller.ui.reusable_components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
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
fun ImagePicker2(
    selectedUploadMethodOption: UploadFileMethodOptions,
    loadImages: (List<Uri>) -> Unit,
    returnToNoOption: (UploadFileMethodOptions) -> Unit,
) {

    // Android 14+ permission
    val readMediaSelectedPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)

    // Android 12+ permission
    val readMediaPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)

    // Android 11- permission
    val readExternalStoragePermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)


    var requestReadMediaSelectedPermissionsState by remember { mutableStateOf(false) }
    var requestReadMediaPermissionsState by remember { mutableStateOf(false) }
    var requestReadExternalStoragePermissionsState by remember { mutableStateOf(false) }
    var runAndroid14Launcher by remember { mutableStateOf(false) }
    var runLauncher by remember { mutableStateOf(false) }


    // Launcher for multiple image selection (Android 14+)
    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uri ->
            if (uri.isNotEmpty()) {
                loadImages(uri)
                returnToNoOption(UploadFileMethodOptions.NO_OPTION)
            } else {
                returnToNoOption(UploadFileMethodOptions.NO_OPTION)
            }
        }
    )

    // Launcher for older Android versions
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

    LaunchedEffect(requestReadMediaPermissionsState) {
        readMediaPermissionState.launchPermissionRequest()
        requestReadMediaPermissionsState = false
    }
    LaunchedEffect(requestReadMediaSelectedPermissionsState) {
        readMediaSelectedPermissionState.launchPermissionRequest()
        requestReadMediaSelectedPermissionsState = false
    }
    LaunchedEffect(requestReadExternalStoragePermissionsState) {
        readExternalStoragePermissionState.launchPermissionRequest()
        requestReadExternalStoragePermissionsState = false
    }

    if (runLauncher) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        launcher.launch(intent)
        runLauncher = false
    }

    if (runAndroid14Launcher) {
        pickMultipleMediaLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
        runAndroid14Launcher = false
    }


        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (!readMediaPermissionState.status.isGranted) {
                    requestReadMediaPermissionsState = true
                } else {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                    launcher.launch(intent)
                }
            }

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

            else -> {
                if (!readExternalStoragePermissionState.status.isGranted) {
                    requestReadExternalStoragePermissionsState = true
                } else {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                    launcher.launch(intent)
                }
            }

        }
}


