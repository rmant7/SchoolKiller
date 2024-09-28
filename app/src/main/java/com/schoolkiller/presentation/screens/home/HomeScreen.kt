package com.schoolkiller.presentation.screens.home

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.LifecycleOwner
import com.schoolkiller.R
import com.schoolkiller.domain.UploadFileMethodOptions
import com.schoolkiller.presentation.ads.AppOpenAdHandler
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.DeleteGhostImagesButton
import com.schoolkiller.presentation.common.EnlargedImage
import com.schoolkiller.presentation.common.ImageCapture
import com.schoolkiller.presentation.common.ImagePicker
import com.schoolkiller.presentation.common.PictureItem
import com.schoolkiller.presentation.common.RoundIconButton
import com.schoolkiller.presentation.common.ScreenImage
import com.schoolkiller.presentation.common.UniversalButton
import com.schoolkiller.presentation.common.deleteImageFromStorage
import com.schoolkiller.presentation.common.deleteSchoolKillerImagesFromMediaStore
import com.schoolkiller.presentation.common.getSystemLocale
import com.schoolkiller.presentation.ui.ads.AppOpenAdHandler


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    listOfImages: SnapshotStateList<Uri>,
    onNavigateToParametersScreen: (Uri) -> Unit,
    onNavigateToCheckSolutionOptionsScreen: (Uri) -> Unit
) {


//    val pictures by viewModel.allPictures.collectAsState(initial = emptyList())

    val viewModel: HomeViewModel = hiltViewModel()
    // updating viewmodel with previous uploaded pictures
    viewModel.updateListOfImages(listOfImages)
    val selectedUploadFileMethod = viewModel.selectedUploadMethodOption
    val images = viewModel.listOfImages.collectAsState()
    val selectedImageIndex = remember { mutableStateOf<Int?>(null) }
    val selectedImageUri = selectedImageIndex.value?.let { images.value[it] }
    var isImageEnlarged by remember { mutableStateOf(false) }
    var isDeleteButtonClicked by remember { mutableStateOf(false) }
    val state = rememberLazyListState()
    val systemLocale = getSystemLocale()

    viewModel.loadOpenAd()
    // App Open Ad
    AppOpenAdHandler(
        context = context,
        viewModel = viewModel,
    )

    // Launcher for the ImagePicker
    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                viewModel.insertImagesOnTheList(uris)
                viewModel.updateSelectedUploadMethodOption(UploadFileMethodOptions.NO_OPTION)

            } else {
                viewModel.updateSelectedUploadMethodOption(UploadFileMethodOptions.NO_OPTION)
            }
        }
    )



    if (isImageEnlarged) {
        if (selectedImageUri != null) {
            EnlargedImage(
                context = context,
                image = selectedImageUri,
                isImageEnlarged = isImageEnlarged,
                onDismiss = { isImageEnlarged = false }
            )
        }
    }


    ApplicationScaffold {

        // App Open Ad
        AppOpenAdHandler(
            context = context,
            viewModel = viewModel,
        )


        when (selectedUploadFileMethod) {
            UploadFileMethodOptions.TAKE_A_PICTURE -> {
                ImageCapture(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    selectedUploadMethodOption = selectedUploadFileMethod,
                    onPictureCapture = { viewModel.insertImagesOnTheList(listOf(it)) },
                    onBackPress = { viewModel.updateSelectedUploadMethodOption(it) },
                    returnToNoOption = { viewModel.updateSelectedUploadMethodOption(it) }
                )
            }

            UploadFileMethodOptions.UPLOAD_AN_IMAGE -> {
                ImagePicker(
                    selectedUploadMethodOption = selectedUploadFileMethod,
                    pickMultipleMediaLauncher = pickMultipleMediaLauncher,
                )
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

                                /**
                                 * Solves the problem with the ghost images leftover bug taken from the app camera
                                 * Need to implement it in a menu, this is not a suitable place
                                 * Delete all camera Images taken from the app
                                 * Request permission
                                 * if permission denied permanently we have an empty screen
                                 */
                                DeleteGhostImagesButton(
                                    modifier = Modifier.size(60.dp),
                                    icon = R.drawable.ic_delete_media_storage,
                                    showRationale = {
                                        Toast.makeText(
                                            context,
                                            "In order the app work as intended you must accept the permission", // TODO { hardcode string }
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onPermissionDeniedPermanently = {
                                        Toast.makeText(
                                            context,
                                            "You denied the permission, in order to accepted it you can go to the app settings", // TODO { hardcode string }
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@DeleteGhostImagesButton
                                    },
                                    onButtonClick = {
                                        if (images.value.isNotEmpty()) {
                                            Toast.makeText(
                                                context,
                                                "Remove the images from the list first", // TODO { hardcode string }
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            deleteSchoolKillerImagesFromMediaStore(
                                                context,
                                                context as Activity
                                            )
                                        }
                                    }
                                )




                            }
                        )
                    }
                )

                /**
                 * Changed to Image upload selection buttons
                 */
                /*
                ExposedDropBox(
                    context = context,
                    maxHeightIn = 200.dp,
                    label = R.string.upload_a_file_label,
                    selectedOption = selectedUploadFileMethod,
                    options = UploadFileMethodOptions.entries.toList().filter {
                        it == UploadFileMethodOptions.TAKE_A_PICTURE || it == UploadFileMethodOptions.UPLOAD_AN_IMAGE
                    },
                    onOptionSelected = { viewModel.updateSelectedUploadMethodOption(it) },
                    optionToString = { option, context -> option.getString(context) }
                )
                */

                LazyColumn(
                    modifier = modifier
                        .fillMaxHeight(0.75f),
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
                                context = context,
                                onEnlarge = {
                                    selectedImageIndex.value = index
                                    isImageEnlarged = true
                                },
                                onRemove = {
                                    viewModel.deleteImageFromTheList(imageUri)
                                    if (selectedImageIndex.value == index) {
                                        selectedImageIndex.value = null
                                    }
                                },
                                onDeleteFromStorage = {
                                    /** Not implement an Icon in PictureItem for Using it yet
                                     * lets see if we will keep it
                                     * check PictureItem composable
                                     */
                                    if (images.value.contains(imageUri)) {
                                        Toast.makeText(
                                            context,
                                            "Image is more than one time on the list", // TODO { hardcode string }
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        deleteImageFromStorage(context, context as Activity, imageUri)
                                    }
                                }
                            )
                        }

                    }
                )

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    //Cheat sheet button, can be removed (?)
                    /* UniversalButton(
                     modifier = modifier
                         .fillMaxWidth()
                         .padding(horizontal = 8.dp)
                         .weight(1f),
                     label = R.string.cheat_sheet_button_label
                 ) {
                     onNavigateToResultScreen()
                 }*/
                    val uploadImageWarningMessage = stringResource(R.string.upload_image_warning)
                    val selectImageWarningMessage = stringResource(R.string.select_image_warning)


                    UniversalButton(
                        modifier = when (systemLocale.language) {
                            "iw" -> {
                                modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            }

                            "ru" -> {
                                modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            }

                            else -> {
                                modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            }
                        },
                        label = R.string.check_solution_button_label
                    ) {

                        when {
                            images.value.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    uploadImageWarningMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            selectedImageUri == null -> {
                                Toast.makeText(
                                    context,
                                    selectImageWarningMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                viewModel.updateSelectedUri(selectedImageUri)
                                onNavigateToCheckSolutionOptionsScreen(selectedImageUri)
                            }
                        }
                    }

                    UniversalButton(
                        modifier = when (systemLocale.language) {
                            "iw" -> {
                                modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            }

                            "ru" -> {
                                modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            }

                            else -> {
                                modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            }
                        },
                        label = R.string.solve_button_label
                    ) {
                        when {
                            images.value.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    uploadImageWarningMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            selectedImageUri != null -> {
                                viewModel.updateSelectedUri(selectedImageUri)
                                onNavigateToParametersScreen(selectedImageUri)
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
                }
            }
        }
    }
}





