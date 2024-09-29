package com.schoolkiller.presentation.common

import android.Manifest
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.schoolkiller.data.Constants
import com.schoolkiller.domain.UploadFileMethodOptions
import timber.log.Timber


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker(
    selectedUploadMethodOption: UploadFileMethodOptions,
    pickMultipleMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
) {
    var requestReadMediaPermissionsState by remember { mutableStateOf(false) }
    var requestReadExternalStoragePermissionsState by remember { mutableStateOf(false) }


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val readMediaPermissionState =
            rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)

        LaunchedEffect(requestReadMediaPermissionsState) {
            readMediaPermissionState.launchPermissionRequest()
            requestReadMediaPermissionsState = false
        }

        if (selectedUploadMethodOption == UploadFileMethodOptions.UPLOAD_AN_IMAGE) {
            if (!readMediaPermissionState.status.isGranted) {
                requestReadMediaPermissionsState = true
            } else {
                try {
                    pickMultipleMediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                } catch (e: Exception) {
                    Timber.e("Error launching image picker: ${e.message}")
                    // ... handle the exception ...
                }
            }
        }

    } else {
        val readExternalStoragePermissionState =
            rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

        LaunchedEffect(requestReadExternalStoragePermissionsState) {
            readExternalStoragePermissionState.launchPermissionRequest()
            requestReadExternalStoragePermissionsState = false
        }

        if (selectedUploadMethodOption == UploadFileMethodOptions.UPLOAD_AN_IMAGE) {
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


fun deleteSchoolKillerImagesFromMediaStore(context: Context, activity: Activity) {

    var count = 0

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.RELATIVE_PATH
    )

    val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
    val selectionArgs = arrayOf("%SchoolKiller_Images%")

    try {


        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )


        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            count = it.count
            Timber.d("Found $count images in SchoolKiller_Images folder")

            while (it.moveToNext()) {
                val imageId = it.getLong(idColumn)
                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )
                Timber.d("Deleting image with URI: $imageUri")

                try {
                    val rowsDeleted = context.contentResolver.delete(imageUri, null, null)
                    if (rowsDeleted > 0) {
                        Timber.d("Successfully deleted image with URI: $imageUri"
                        )
                    } else {
                        Timber.w("Failed to delete image with URI: $imageUri")
                    }
                } catch (e: RecoverableSecurityException) {
                    Timber.e("RecoverableSecurityException for URI: $imageUri", e
                    )
                    val intentSender = e.userAction.actionIntent.intentSender
                    activity.startIntentSenderForResult(
                        intentSender,
                        Constants.DELETE_REQUEST_CODE,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (e: Exception) {
                    Timber.e("Error deleting image with URI: $imageUri", e)
                }
            }
        }

    } catch (e: Exception) {
        Timber.e("Error querying MediaStore", e)
    }
}





