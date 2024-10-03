package com.schoolkiller.presentation.screens.home

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.R
import com.schoolkiller.domain.UploadFileMethodOptions

import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.AttentionAlertDialog
import com.schoolkiller.presentation.common.EnlargedImage
import com.schoolkiller.presentation.common.OpenCamera
import com.schoolkiller.presentation.common.OpenGallery
import com.schoolkiller.presentation.common.PictureItem
import com.schoolkiller.presentation.common.RoundIconButton
import com.schoolkiller.presentation.common.ScreenImage
import com.schoolkiller.presentation.common.UniversalButton
import com.schoolkiller.presentation.permissions.RequestMultiplePermissions
import com.schoolkiller.presentation.permissions.RequestMultiplePermissionsGallery
import com.schoolkiller.presentation.toast.ShowToastMessage
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
    listOfImages: SnapshotStateList<Uri>,
    onNavigateToParametersScreen: (Uri) -> Unit,
    onNavigateToCheckSolutionOptionsScreen: (Uri) -> Unit
) {


//    val pictures by viewModel.allPictures.collectAsState(initial = emptyList())

    val viewModel: HomeViewModel = hiltViewModel()

    /** Updating viewmodel with previous uploaded pictures,
    * for some reason list of images resets every time without this code line,
     * even though we have viewModel.insertImagesOnTheList(uris) in this screen
     */
    viewModel.updateListOfImages(listOfImages)
    val selectedUploadFileMethod = viewModel.selectedUploadMethodOption
    val images = viewModel.listOfImages.collectAsState()
    val selectedImageIndex = remember { mutableStateOf<Int?>(null) }
    val selectedImageUri = selectedImageIndex.value?.let { images.value[it] }
    var isImageEnlarged by remember { mutableStateOf(false) }
    val state = rememberLazyListState()

    var invalidImagesPlaceholder by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isAttentionDialogShowed by remember { mutableStateOf(false) }


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


    // Launcher for the ImagePicker (must be in the screen that is being called)
    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            try {
                viewModel.insertImagesOnTheList(uris)
            } catch (e: Exception) {
                e.printStackTrace()
                ShowToastMessage.SOMETHING_WENT_WRONG.showToast()
            }
        }
    )

    // Launcher for the Camera (must be in the screen that is being called)
    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    viewModel.viewModelScope.launch {
                        viewModel.saveImage(it) // save bitmap to storage
                        val getBitmapUri =
                            viewModel.getSavedImageUri() // load bitmap uri to the list
                        getBitmapUri?.let { viewModel.insertImagesOnTheList(listOf(it)) } ?: run {
                            ShowToastMessage.IMAGE_FAIL__TO_LOAD_TO_THE_LIST.showToast()
                        }
                    }
                }
            }
        }


    if (isImageEnlarged) {
        val isUriValid = selectedImageUri?.let { viewModel.checkUriValidity(selectedImageUri) }
        if (isUriValid == true) {
            EnlargedImage(
                context = context,
                image = selectedImageUri,
                isImageEnlarged = isImageEnlarged,
                onDismiss = { isImageEnlarged = false }
            )
        } else {
            isImageEnlarged = false
            ShowToastMessage.CORRUPTED_LOADED_FILE.showToast()
        }
    }


    // Show App Open Ad
    viewModel.showAppOpenAd(context)


    ApplicationScaffold(
        content = {

            when (selectedUploadFileMethod) {

                UploadFileMethodOptions.TAKE_A_PICTURE -> {

                    RequestMultiplePermissions(
                        functionOnPermissionGranted = {},
                        composableOnPermissionGranted = {
                            OpenCamera(
                                cameraLauncher = cameraLauncher
                            )
                        }
                    )
                    viewModel.updateSelectedUploadMethodOption(UploadFileMethodOptions.NO_OPTION)
                }

                UploadFileMethodOptions.UPLOAD_AN_IMAGE -> {

                    RequestMultiplePermissionsGallery(
                        functionOnPermissionGranted = {},
                        composableOnPermissionGranted = {

                            invalidImagesPlaceholder = viewModel.getInvalidImageUris()

                            if (invalidImagesPlaceholder.isNotEmpty()) {
                                isAttentionDialogShowed = true
                            } else {
                                OpenGallery(pickMultipleMediaLauncher)
                            }
                        }
                    )
                    viewModel.updateSelectedUploadMethodOption(UploadFileMethodOptions.NO_OPTION)

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

                    ScreenImage(
                        modifier = modifier
                            .fillMaxHeight(0.35f), // adjust the height of the image from here
                        image = R.drawable.upload_to_school_assistant,
                        contentDescription = R.string.upload_to_ai_school_image_assistant_content_description
                    )

                    /**
                     * Image upload selection buttons
                     */

                    Column(
                        modifier = modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        content = {
                            Text(
                                modifier = Modifier.padding(bottom = 10.dp),
                                text = stringResource(R.string.add_image),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Absolute.SpaceAround,
                                content = {
                                    RoundIconButton(
                                        modifier = Modifier.size(60.dp),
                                        icon = R.drawable.ic_add_image
                                    ) {
                                        viewModel.updateSelectedUploadMethodOption(
                                            UploadFileMethodOptions.UPLOAD_AN_IMAGE
                                        )
                                    }

                                    RoundIconButton(
                                        modifier = Modifier.size(60.dp),
                                        icon = R.drawable.ic_camera
                                    ) {
                                        viewModel.updateSelectedUploadMethodOption(
                                            UploadFileMethodOptions.TAKE_A_PICTURE
                                        )
                                    }
                                }
                            )
                        }
                    )

                    LazyColumn(
                        modifier = modifier.fillMaxHeight(),
                        state = state,
                        content = {

                            itemsIndexed(images.value) { index, imageUri ->
                                var offset by remember { mutableFloatStateOf(0f) }

                                val isSelected = index == selectedImageIndex.value

                                val imageModifier = Modifier
                                    .clickable {
                                        selectedImageIndex.value = index
                                    }
                                    .then(
                                        if (isSelected) Modifier.border(
                                            width = 4.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(16.dp)
                                        ) else Modifier
                                    )

                                PictureItem(
                                    imageModifier = imageModifier,
                                    imageUri = imageUri,
                                    onEnlarge = {
                                        selectedImageIndex.value = index
                                        isImageEnlarged = true
                                    },
                                    onRemove = {
                                        viewModel.deleteImageFromTheList(imageUri)
                                        if (selectedImageIndex.value == index) {
                                            selectedImageIndex.value = null
                                        }
                                    }
                                )
                            }

                        }
                    )

                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {

                val uploadImageWarningMessage = stringResource(R.string.upload_image_warning)
                val selectImageWarningMessage = stringResource(R.string.select_image_warning)

                fun onNextClick(onNavigate: () -> Unit) {
                    when {
                        images.value.isEmpty() -> {
                            Toast.makeText(
                                context,
                                uploadImageWarningMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        selectedImageUri != null -> {
                            val isUriValid = viewModel.checkUriValidity(selectedImageUri)
                            if (isUriValid) {
                                viewModel.updateSelectedUri(selectedImageUri)
                                onNavigate()
                            } else {
                                ShowToastMessage.CORRUPTED_LOADED_FILE.showToast()
                            }
                        }

                        else -> {
                            Toast.makeText(
                                context,
                                selectImageWarningMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.check_solution_button_label
                ) {
                    onNextClick {
                        onNavigateToCheckSolutionOptionsScreen(selectedImageUri!!)
                    }
                }

                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.solve_button_label,
                    onButtonClicked = {
                        onNextClick {
                            onNavigateToParametersScreen(selectedImageUri!!)
                        }
                    }
                )
            }
        }
    )
}







