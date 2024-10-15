package com.schoolkiller.presentation.screens.home

import android.net.Uri
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdView
import com.schoolkiller.R
import com.schoolkiller.domain.UploadFileMethodOptions
import com.schoolkiller.presentation.RequestState
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.PictureItem
import com.schoolkiller.presentation.common.RoundIconButton
import com.schoolkiller.presentation.common.ScreenImage
import com.schoolkiller.presentation.common.UniversalButton
import com.schoolkiller.presentation.toast.ShowToastMessage

@Composable
fun HomeScreenUI(
    modifier: Modifier = Modifier,
    isHomeScreenUIShowed: Boolean,
    onNavigateToOcrScreen: (Uri?) -> Unit,
    /*onNavigateToParametersScreen: () -> Unit,
    onNavigateToCheckSolutionOptionsScreen: () -> Unit*/
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val stateProperties = viewModel.homePropertiesState.collectAsStateWithLifecycle().value
    val homeScreenRequestState =
        viewModel.homeScreenRequestState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val lazyColumnState = rememberLazyListState()
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    val selectedImageUri by remember(selectedImageIndex) {
        derivedStateOf {
            selectedImageIndex?.let {
                if (it >= 0 && it < stateProperties.listOfImages.size) {
                    stateProperties.listOfImages[it]
                } else {
                    null
                }
            }
        }
    }


    ApplicationScaffold(
        isShowed = isHomeScreenUIShowed,
        content = {

            ScreenImage(
                modifier = modifier
                    .fillMaxHeight(0.4f), // adjust the height of the image from here
                image = R.drawable.upload_to_school_assistant,
                contentDescription = R.string.upload_to_ai_school_image_assistant_content_description
            )

            /**
             * Image upload selection buttons
             */

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

            /** example of homeScreenRequestState use */
            when (homeScreenRequestState) {

                RequestState.Loading -> {
                    Box(
                        modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                        content = {
                            CircularProgressIndicator(
                                modifier.size(100.dp)
                            )
                        }
                    )
                }

                else -> {

                    LazyColumn(
                        modifier = modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        state = lazyColumnState,
                        content = {
                            itemsIndexed(stateProperties.listOfImages) { index, imageUri ->

                                val isSelected = index == selectedImageIndex

                                val imageModifier = Modifier
                                    .clickable {
                                        selectedImageIndex = index
                                        viewModel.updateSelectedUri(imageUri)
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
                                    onEnlarge = { uri ->
                                        selectedImageIndex = index
                                        viewModel.updateSelectedUri(uri)
                                        viewModel.updateIsImageEnlarged(true)
                                    },
                                    onRemove = {
                                        viewModel.removeImageFromTheList(imageUri)
                                        if (selectedImageIndex == index) {
                                            selectedImageIndex = null
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

                fun onNextClick(/*onNavigate: () -> Unit*/) {
                    when {
                        stateProperties.listOfImages.isEmpty() -> {
                            Toast.makeText(
                                context,
                                uploadImageWarningMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        selectedImageUri != null -> {
                            val isUriValid = viewModel.checkUriValidity(selectedImageUri!!)
                            if (isUriValid) {
                                viewModel.updateSelectedUri(selectedImageUri)
                                // onNavigate()
                                onNavigateToOcrScreen(selectedImageUri)
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
                    label = R.string.recognize_text_button_label,
                    onButtonClicked = { onNextClick() }
                )

                /*UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.check_solution_button_label
                ) {
                    val index = selectedImageIndex
                    onNextClick {
                        val uri = index?.let {
                            if (it >= 0 && it < stateProperties.listOfImages.size) {
                                stateProperties.listOfImages[it]
                            } else {
                                null
                            }
                        }
                        viewModel.updateSelectedUri(uri)
                        onNavigateToCheckSolutionOptionsScreen()
                    }
                }*/

                /*UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.solve_button_label,
                    onButtonClicked = {
                        val index = selectedImageIndex
                        onNextClick {
                            val uri = index?.let {
                                if (it >= 0 && it < stateProperties.listOfImages.size) {
                                    stateProperties.listOfImages[it]
                                } else {
                                    null
                                }
                            }
                            viewModel.updateSelectedUri(uri)
                            onNavigateToParametersScreen()
                        }
                    }
                )*/
            }
        }
    )

}