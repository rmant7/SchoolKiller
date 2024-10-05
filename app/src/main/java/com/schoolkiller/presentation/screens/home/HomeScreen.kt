package com.schoolkiller.presentation.screens.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.R
import com.schoolkiller.domain.UploadFileMethodOptions
import com.schoolkiller.presentation.common.AttentionAlertDialog
import com.schoolkiller.presentation.common.EnlargedImage
import com.schoolkiller.presentation.common.PermissionMessageRationale
import com.schoolkiller.presentation.common.PermissionRequestDialog
import com.schoolkiller.presentation.permissions.PermissionSet
import com.schoolkiller.presentation.toast.ShowToastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber


@Composable
fun HomeScreen(
    onNavigateToParametersScreen: () -> Unit,
    onNavigateToCheckSolutionOptionsScreen: () -> Unit
) {

    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current
    val stateProperties = viewModel.homePropertiesState.collectAsState().value
    val dialogQueueList = viewModel.permissionDialogQueueList
    var isHomeScreenUIShowed by remember { mutableStateOf(false) }
    var isAttentionDialogShowed by remember { mutableStateOf(false) }
    var invalidImagesPlaceholder by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var launchGallery by remember { mutableStateOf<Boolean?>(null) }
    var launchCamera by remember { mutableStateOf<Boolean?>(null) }


    /** Invalid images auto cleaned on every HomeScreen creation*/
    LaunchedEffect(Unit) {
        if (PermissionSet().isCleanInvalidImagesActive(context)) {
            viewModel.viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
                invalidImagesPlaceholder = viewModel.getInvalidImageUris()
                if (invalidImagesPlaceholder.isNotEmpty()) {
                    viewModel.cleanInvalidImages(context as Activity, invalidImagesPlaceholder)
                    invalidImagesPlaceholder.forEach { uri ->
                        viewModel.deleteImageFromTheList(uri)
                    }
                }
            }
        }
    }

    /** Pop up dialog for deleting invalid images */
    /*
    AttentionAlertDialog(
        isShowed = isAttentionDialogShowed,
        message = stringResource(R.string.invalid_images_message),
        icon = R.drawable.attention,
        onDismiss = { isAttentionDialogShowed = false },
        onCancel = { isAttentionDialogShowed = false },
        onConfirm = {
            isAttentionDialogShowed = false
            viewModel.cleanInvalidImages(context as Activity, invalidImagesPlaceholder)
        }
    )
     */


    /** Permission Launcher */
    val permissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            perms.keys.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
                if (perms.all { entry -> entry.value }) {
                    if (stateProperties.selectedUploadMethodOption == UploadFileMethodOptions.UPLOAD_AN_IMAGE) {
                        launchGallery = true
                    }
                    if (stateProperties.selectedUploadMethodOption == UploadFileMethodOptions.TAKE_A_PICTURE) {
                        launchCamera = true
                    }
                }
                viewModel.updateSelectedUploadMethodOption(UploadFileMethodOptions.NO_OPTION)
            }
        }
    )
    dialogQueueList
        .reversed()
        .forEach { permission ->
            PermissionRequestDialog(
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    context as Activity,
                    permission
                ),
                onDismiss = viewModel::onDismissPermissionDialog,
                onGoToAppSettings = { context.openAppSettings() },
                onConfirm = {
                    permissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                permissionMessageRationale = when (permission) {
                    Manifest.permission.CAMERA -> {
                        PermissionMessageRationale.CameraPermissionMessage()
                    }

                    Manifest.permission.READ_MEDIA_IMAGES -> {
                        PermissionMessageRationale.ReadMediaPermissionMessage()
                    }

                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        PermissionMessageRationale.ReadStoragePermissionMessage()
                    }

                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        PermissionMessageRationale.WriteStoragePermissionMessage()
                    }

                    else -> return@forEach
                }
            )
        }

    /** Launcher for the Gallery */
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            try {
                viewModel.insertImagesOnTheList(uris)
            } catch (e: Exception) {
                Timber.w(e, "Image don`t inserted to the list")
                ShowToastMessage.SOMETHING_WENT_WRONG.showToast()
            }
        }
    )
    LaunchedEffect(key1 = launchGallery) {
        if (launchGallery == true) {
            try {
                galleryLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            } catch (e: Exception) {
                Timber.w(e, "gallery launcher activity failed")
                ShowToastMessage.SOMETHING_WENT_WRONG.showToast()
            }
            launchGallery = null
        }


    }

    /** Launcher for the Camera */
    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    viewModel.viewModelScope.launch {
                        viewModel.saveImage(it) // save bitmap to storage
                        val getBitmapUri =
                            viewModel.getCameraSavedImageUri() // load bitmap uri to the list
                        getBitmapUri?.let { viewModel.insertImagesOnTheList(listOf(it)) } ?: run {
                            ShowToastMessage.IMAGE_FAIL_TO_LOAD_TO_THE_LIST.showToast()
                        }
                    }
                }
            }
        }
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    LaunchedEffect(key1 = launchCamera) {
        if (launchCamera == true) {
            try {
                cameraLauncher.launch(takePictureIntent)
            } catch (e: Exception) {
                Timber.w(e, "camera launcher activity failed")
                ShowToastMessage.CAMERA_FAIL_TO_OPEN.showToast()
            }
            launchCamera = null
        }
    }

    /** Enlarging the Image */
    if (stateProperties.isImageEnlarged) {
        val isUriValid =
            stateProperties.selectedImageUri?.let { viewModel.checkUriValidity(stateProperties.selectedImageUri) }
        if (isUriValid == true) {
            EnlargedImage(
                image = stateProperties.selectedImageUri,
                isImageEnlarged = stateProperties.isImageEnlarged,
                onDismiss = { viewModel.updateIsImageEnlarged(false) }
            )
        } else {
            viewModel.updateIsImageEnlarged(false)
            ShowToastMessage.CORRUPTED_LOADED_FILE.showToast()
        }
    }


    /** This is not in a good place, like just as a raw call. Every time screen load or refresh will call this function.
     * This brings unnecessary calls because of the cooldown and will impact performance.
     * Try to condition it with the cooldown.
     */
    // Show App Open Ad
    viewModel.showAppOpenAd(context)


    /** Button Cases */
    when (stateProperties.selectedUploadMethodOption) {

        UploadFileMethodOptions.TAKE_A_PICTURE -> {
            val cameraPermissionSet = PermissionSet().getCameraPermissionSet()
            permissionResultLauncher.launch(cameraPermissionSet)
        }


        UploadFileMethodOptions.UPLOAD_AN_IMAGE -> {
            val galleryPermissionSet = PermissionSet().getGalleryPermissionSet()
            permissionResultLauncher.launch(galleryPermissionSet)
        }

        UploadFileMethodOptions.UPLOAD_A_FILE -> {
            viewModel.updateSelectedUploadMethodOption(UploadFileMethodOptions.NO_OPTION)
            Toast.makeText(
                context,
                "Function is not ready yet",
                Toast.LENGTH_SHORT
            ).show()
        }

        UploadFileMethodOptions.PROVIDE_A_LINK -> {
            viewModel.updateSelectedUploadMethodOption(UploadFileMethodOptions.NO_OPTION)
            Toast.makeText(
                context,
                "Function is not ready yet",
                Toast.LENGTH_SHORT
            ).show()
        }

        UploadFileMethodOptions.NO_OPTION -> {
            isHomeScreenUIShowed = true
        }
    }

    /** Main UI screen */
    HomeScreenUI(
        isHomeScreenUIShowed = isHomeScreenUIShowed,
        onNavigateToParametersScreen = { onNavigateToParametersScreen() },
        onNavigateToCheckSolutionOptionsScreen = { onNavigateToCheckSolutionOptionsScreen() }
    )


}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}



