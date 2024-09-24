package com.schoolkiller.ui.screens

import ExposedDropBox
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.schoolkiller.R
import com.schoolkiller.ui.reusable_components.ApplicationScaffold
import com.schoolkiller.ui.reusable_components.EnlargedImage
import com.schoolkiller.ui.reusable_components.ImageCapture
import com.schoolkiller.ui.reusable_components.ImagePicker
import com.schoolkiller.ui.reusable_components.PictureItem
import com.schoolkiller.ui.reusable_components.ScreenImage
import com.schoolkiller.ui.reusable_components.UniversalButton
import com.schoolkiller.utils.UploadFileMethodOptions
import com.schoolkiller.view_model.SchoolKillerViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner,
    onNavigateToAdditionalInformationScreen: () -> Unit,
    onNavigateToCheckSolutionOptionsScreen: () -> Unit
) {
//    val pictures by viewModel.allPictures.collectAsState(initial = emptyList())
    val selectedUploadFileMethod = viewModel.selectedUploadMethodOption
    val images = viewModel.listOfImages.collectAsState()
    val selectedImageIndex = remember { mutableStateOf<Int?>(null) }
    val selectedImageUri = selectedImageIndex.value?.let { images.value[it] }
    var isImageEnlarged by remember { mutableStateOf(false) }
    val state = rememberLazyListState()


    LaunchedEffect(Unit) {
        viewModel.updatePrompt(
            context.getString(R.string.prompt_text)
        )
    }

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


    ApplicationScaffold(
    ) {

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
                    loadImages = { viewModel.insertImagesOnTheList(it) },
                    returnToNoOption = { viewModel.updateSelectedUploadMethodOption(it) }
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
                    image = R.drawable.upload_to_school_assistant,
                    contentDescription = R.string.upload_to_ai_school_image_assistant_content_description
                )

                /**
                 * Image upload selection buttons
                 */
                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UniversalButton(
                        modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        R.string.upload_picture
                    ) {
                        viewModel.updateSelectedUploadMethodOption(
                            UploadFileMethodOptions.UPLOAD_AN_IMAGE
                        )
                    }

                    UniversalButton(
                        modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        R.string.take_a_picture
                    ) {
                        viewModel.updateSelectedUploadMethodOption(
                            UploadFileMethodOptions.TAKE_A_PICTURE
                        )
                    }
                }

                /**
                 * Changed to Image upload selection buttons
                 */
                /*
                ExposedDropBox(
                    context = context,
                    maxHeightIn = 200.dp,
                    label = R.string.upload_a_file_label,
                    selectedOption = selectedUploadFileMethod,
                    options = UploadFileMethodOptions.entries.toList().drop(1),
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
                                }
                            )
                        }

                    }
                )

                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
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

                    UniversalButton(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        label = R.string.check_solution_button_label
                    ) {
                        onNext(
                            images, context, selectedImageUri, viewModel
                        ) { onNavigateToCheckSolutionOptionsScreen() }
                    }

                    UniversalButton(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        label = R.string.solve_button_label
                    ) {
                        onNext(
                            images, context, selectedImageUri, viewModel
                        ) { onNavigateToAdditionalInformationScreen() }
                    }
                }
            }
        }
    }
}

private fun onNext(
    images: State<SnapshotStateList<Uri>>,
    context: Context,
    selectedImageUri: Uri?,
    viewModel: SchoolKillerViewModel,
    onNavigateToNextScreen: () -> Unit
) {
    val resources = context.resources
    when {
        images.value.isEmpty() -> {
            Toast.makeText(
                context,
                resources.getString(R.string.upload_image_warning),
                Toast.LENGTH_SHORT
            ).show()
        }

        selectedImageUri != null -> {
            viewModel.updateSelectedUri(selectedImageUri)
            onNavigateToNextScreen()
        }

        else -> {
            Toast.makeText(
                context,
                resources.getString(R.string.select_image_warning),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


