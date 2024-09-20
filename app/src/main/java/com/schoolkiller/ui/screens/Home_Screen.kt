package com.schoolkiller.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.ui.reusable_components.ApplicationScaffold
import com.schoolkiller.ui.reusable_components.ImagePicker
import com.schoolkiller.ui.reusable_components.PictureItem
import com.schoolkiller.ui.reusable_components.ScreenImage
import com.schoolkiller.ui.reusable_components.UniversalButton
import com.schoolkiller.view_model.SchoolKillerViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
    onNavigateToAdditionalInformationScreen: () -> Unit,
    onNavigateToCheckSolutionOptionsScreen: () -> Unit
) {


    val pictures by viewModel.allPictures.collectAsState(initial = emptyList())
    val selectedUploadFileMethod = viewModel.selectedUploadMethodOption
    val images = viewModel.selectedImages.collectAsState()
    val selectedImageIndex = remember { mutableStateOf<Int?>(null) }
    val selectedImageUri = selectedImageIndex.value?.let { images.value[it] }
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()



    ApplicationScaffold(
    ) {

        ScreenImage(
            image = R.drawable.upload_to_school_assistant,
            contentDescription = R.string.upload_to_ai_school_image_assistant_content_description
        )

        /*DropBox(
            context = context,
            maxHeightIn = 200.dp,
            xDpOffset = 155.dp,
            yDpOffset = (-30).dp,
           label = R.string.upload_a_file_label,
            selectedOption = selectedUploadFileMethod,
           options = UploadFileMethodOptions.entries.toList(),
            onOptionSelected = { viewModel.updateSelectedUploadMethodOption(it) },
            optionToString = { option, context -> option.getString(context) }
        )*/
        ImagePicker(context = context, viewModel = viewModel)

        LazyColumn(
            modifier = modifier
                .fillMaxHeight(0.75f),
            state = state,
            content = {

                itemsIndexed(images.value) { index, imageUri ->
                    var offset by remember { mutableFloatStateOf(0f) }

                    val isSelected = index == selectedImageIndex.value

                    var imageModifier = Modifier
//                        .padding(start = 8.dp, end = 8.dp)
//                        .requiredSize(200.dp)
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
                        state = state,
                        scope = scope,
                        offsetValue = offset,
                        onOffsetChange = { newOffset -> offset = newOffset },
                        onRemove = {
                            viewModel.onImageDeleted(imageUri)
                            if (selectedImageIndex.value == index) {
                                selectedImageIndex.value = null
                            }
                        }
                    )
                }
            }
        )

//        LazyColumn(
//            modifier = modifier
//                .fillMaxHeight(),
//            state = state,
//            content = {
//
//                items(pictures, key = { it.id }) { picture ->
//                    PictureItem(
//                        picture = picture,
//                        onRemove = { viewModel.deletePicture(picture) }
//                    )
//                }
//            }
//        )


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

private fun onNext(
    images: State<SnapshotStateList<Uri>>,
    context: Context,
    selectedImageUri: Uri?,
    viewModel: SchoolKillerViewModel,
    onNavigateToNextScreen: () -> Unit
) {
    when {
        images.value.isEmpty() -> {
            Toast.makeText(
                context,
                "Please upload an image from the device", // TODO { hardcode string }
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
                "Please select an image from the list", // TODO { hardcode string }
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
