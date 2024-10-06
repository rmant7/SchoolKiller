package com.schoolkiller.presentation.screens.checking

import ExposedDropBox
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolkiller.R
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.presentation.ads.BannerAdContainer
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.AttentionAlertDialog
import com.schoolkiller.presentation.common.UniversalButton


@Composable
fun CheckSolutionScreen(
    modifier: Modifier = Modifier,
    onNavigateToResultScreen: (String) -> Unit
) {
    val viewModel: SolutionCheckingViewModel = hiltViewModel()
    val solutionProperties = viewModel.solutionPropertiesState.collectAsStateWithLifecycle().value


    /** Testing the prompt in a dialog if the values are correct. Check also the solve button*/
    var isAttentionDialogShowed by remember { mutableStateOf(false) }
    var proceedToResultScreen by remember { mutableStateOf(false) }
    AttentionAlertDialog(
        isShowed = isAttentionDialogShowed,
        message = solutionProperties.solutionPromptText,
        onDismiss = { isAttentionDialogShowed = false },
        onCancel = { isAttentionDialogShowed = false },
        onConfirm = {
            isAttentionDialogShowed = false
            proceedToResultScreen = true
        }
    )


    ApplicationScaffold(
        isShowed = true,
        columnHorizontalAlignment = Alignment.CenterHorizontally,
        content =
        {

            // Banner ad
            BannerAdContainer(adView = solutionProperties.adView)

            /*ScreenImage(
                modifier = modifier
                    .fillMaxHeight(0.35f), // adjust the height of the image from here
                image = R.drawable.check_solution_assistant,
                contentDescription = R.string.check_solution_ai_school_image_assistant_content_description
            )*/


            ExposedDropBox(
                maxHeightIn = 200.dp,
                label = R.string.grade_label,
                selectedOption = solutionProperties.grade,
                options = GradeOption.entries.toList(),
                onOptionSelected = {
                    viewModel.updateSelectedGradeOption(it)
                },
                optionToString = { option, context -> option.getString(context) }
            )


            // Rating Slider for max selected rating value, don't remove.
            // Text(stringResource(R.string.rating_TextField_label))
            // RatingSlider(viewModel)

            /**
             * PlaceHolder in the screen to place what needed
             */
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f),
                content = {
                    /* you can use here your composables or create
                    what has to be presented here but try to keep the height to 0.2f
                    or you have to play with button adjustments if the screen remains as it is
                     */
                }
            )

        },
        bottomBar = {
            UniversalButton(
                modifier = modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                label = R.string.check_solution_button_label,
            ) {

                //Updating rating scale in prompt, don't remove.
                /*val originalPrompt = viewModel.originalPrompt.value
                val selectedMaxRate = viewModel.selectedRateMax
                viewModel.updatePrompt(
                    originalPrompt.replace(
                        "(1–100)", selectedMaxRate.toString()
                    )
                )*/


                viewModel.buildSolutionPrompt()
                /** testing the prompt : uncomment */
//                isAttentionDialogShowed = true
                /** testing the prompt : uncomment */
//                if (proceedToResultScreen) {
                    /** testing the prompt : uncomment */
//                    isAttentionDialogShowed = false

                    // on back press from ResultScreen we have to restore requestGeminiResponse back to true
//                    resultViewModel.updateRequestGeminiResponse(true)

                    // reset TextGenerationResult to initialize the loading indicator
//                    viewModel.updateTextGenerationResult("")

                    onNavigateToResultScreen(solutionProperties.solutionPromptText)
                    /** testing the prompt : uncomment */
//                }

            }
        }
    )
}


@Composable
fun RatingSlider(viewModel: SolutionCheckingViewModel) {
    var sliderPosition by remember {
        mutableIntStateOf(100)
    }

    Column {
        Slider(
            value = sliderPosition.toFloat(),
            onValueChange = {
                val intVal = it.toInt()
                sliderPosition = intVal
                viewModel.updateSelectedRateMax(intVal)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 9,
            valueRange = 0f..100f,
        )
        Text(text = sliderPosition.toString())
    }
}


