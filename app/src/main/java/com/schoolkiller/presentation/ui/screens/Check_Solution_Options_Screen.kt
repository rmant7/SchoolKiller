package com.schoolkiller.presentation.ui.screens

import ExposedDropBox
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.presentation.ui.reusable_components.ApplicationScaffold
import com.schoolkiller.presentation.ui.reusable_components.UniversalButton
import com.schoolkiller.domain.GradeOptions
import com.schoolkiller.presentation.view_model.SchoolKillerViewModel

@Composable
fun CheckSolutionOptionsScreen(
    context: Context,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
    onNavigateToResultScreen: () -> Unit
) {
    val selectedGrade = viewModel.selectedGradeOption

    LaunchedEffect(true) {
        viewModel.updatePrompt(
            context.getString(R.string.check_solution_text)
        )
    }

    ApplicationScaffold {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        )
        {
            ExposedDropBox(
                maxHeightIn = 200.dp,
                context = context,
                label = R.string.grade_label,
                selectedOption = selectedGrade,
                options = GradeOptions.entries.toList(),
                onOptionSelected = {
                    viewModel.updateSelectedGradeOption(it)
                },
                optionToString = { option, context -> option.getString(context) }
            )

            Spacer(Modifier.padding(0.dp, 20.dp))
            // Rating Slider for max selected rating value, don't remove.
            // Text(stringResource(R.string.rating_TextField_label))
            // RatingSlider(viewModel)

            //Reused Component
            UniversalButton(
                modifier = Modifier.fillMaxWidth(),
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
                viewModel.importGradeToOriginalPrompt()
                onNavigateToResultScreen()
            }
        }
    }
}

@Composable
fun RatingSlider(viewModel: SchoolKillerViewModel) {

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
