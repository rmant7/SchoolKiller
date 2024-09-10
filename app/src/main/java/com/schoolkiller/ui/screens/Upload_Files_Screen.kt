package com.schoolkiller.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
fun UploadFilesScreen(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
) {

    val pictures by viewModel.allPictures.collectAsState(initial = emptyList())
    val selectedUploadFileMethod = viewModel.selectedUploadMethodOption
    val images = viewModel.selectedImages.collectAsState()
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()


    ApplicationScaffold(
    ) {

        ScreenImage(
            image = R.drawable.upload_to_school_assistant,
            contentDescription = R.string.upload_to_ai_school_image_assistant_content_description
        )

//        DropBox(
//            context = context,
//            maxHeightIn = 200.dp,
//            xDpOffset = 155.dp,
//            yDpOffset = (-30).dp,
//            label = R.string.upload_a_file_label,
//            selectedOption = selectedUploadFileMethod,
//            options = UploadFileMethodOptions.entries.toList(),
//            onOptionSelected = { viewModel.updateSelectedUploadMethodOption(it) },
//            optionToString = { option, context -> option.getString(context) }
//        )
        ImagePicker(context = context, viewModel = viewModel)

        LazyColumn(
            modifier = modifier
                .height(260.dp),
            state = state,
            content = {

                items(images.value) { imageUri ->
                    var offset by remember { mutableFloatStateOf(0f) }

                    PictureItem(
                        imageUri = imageUri,
                        state = state,
                        scope = scope,
                        offsetValue = offset,
                        onOffsetChange = { newOffset -> offset = newOffset },
                        onRemove = {
                            viewModel.onImageDeleted(imageUri)
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


        UniversalButton(label = R.string.solve_button_label) {
            // TODO { Implement Click Action }
        }

    }
}
