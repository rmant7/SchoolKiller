package com.schoolkiller.presentation.screens.checking

import ExposedDropBox
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.presentation.ads.BannerAdContainer
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.ScreenImage
import com.schoolkiller.presentation.common.UniversalButton
import com.schoolkiller.presentation.common.getSystemLocale

@Composable
fun CheckSolutionScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onNavigateToResultScreen: (String) -> Unit
) {
    val viewModel: SolutionCheckingViewModel = hiltViewModel()
    val selectedGrade = viewModel.selectedGradeOption
    val adView = viewModel.adview.collectAsState()
    val systemLocale = getSystemLocale()

    LaunchedEffect(Unit) {
        viewModel.updatePrompt(
            context.getString(R.string.check_solution_text)
        )
    }


    ApplicationScaffold(
        columnHorizontalAlignment = Alignment.CenterHorizontally
    ) {

        ScreenImage(
            modifier = modifier
                .fillMaxHeight(0.35f), // adjust the height of the image from here
            image = R.drawable.check_solution_assistant,
            contentDescription = R.string.check_solution_ai_school_image_assistant_content_description
        )


        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.grade_label,
            selectedOption = selectedGrade,
            options = GradeOption.entries.toList(),
            onOptionSelected = {
                viewModel.updateSelectedGradeOption(it)
            },
            optionToString = { option, context -> option.getString(context) }
        )


        /**
         * what the following Spacer padding parameters are? like vertical, horizontal, top, bottom?
         */
//        Spacer(Modifier.padding(0.dp, 20.dp))
        // Rating Slider for max selected rating value, don't remove.
        // Text(stringResource(R.string.rating_TextField_label))
        // RatingSlider(viewModel)
        val gradeArray: Array<String> = stringArrayResource(R.array.grades)

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



        // Banner ad
        BannerAdContainer(adView = adView.value)



        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){

        //Reused Component
        UniversalButton(
            modifier = when (systemLocale.language) {
                "iw" -> {
                    modifier
                        .weight(1f)
                        .fillMaxHeight(0.5f)
                }

                "ru" -> {
                    modifier
                        .weight(1f)
                        .fillMaxHeight(0.5f)
                }

                else -> {
                    modifier
                        .weight(1f)
                        .fillMaxHeight(0.5f)
                }
            },
            label = R.string.check_solution_button_label,
        ) {
            viewModel.updateTextGenerationResult("")

            //Updating rating scale in prompt, don't remove.
            /*val originalPrompt = viewModel.originalPrompt.value
            val selectedMaxRate = viewModel.selectedRateMax
            viewModel.updatePrompt(
                originalPrompt.replace(
                    "(1–100)", selectedMaxRate.toString()
                )
            )*/
            viewModel.buildPropertiesPrompt()
            onNavigateToResultScreen(viewModel.originalPrompt.value)
        }
    }
    }
//        }
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


