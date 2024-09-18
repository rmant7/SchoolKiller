package com.schoolkiller.ui.screens

import ExposedDropBox
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.ui.reusable_components.ApplicationScaffold
import com.schoolkiller.ui.reusable_components.UniversalButton
import com.schoolkiller.utils.AiModelOptions
import com.schoolkiller.utils.ExplanationLevelOptions
import com.schoolkiller.utils.GradeOptions
import com.schoolkiller.utils.SolutionLanguageOptions
import com.schoolkiller.view_model.SchoolKillerViewModel

@Composable
fun ResultScreen(
    context: Context,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onNavigateToNextPage: () -> Unit
) {

    //    val classOptions = remember { context.resources.getStringArray(R.array.classes).toList() }
//    val languageOptions = remember { context.resources.getStringArray(R.array.languages).toList() }
//    val explanationOptions = remember { context.resources.getStringArray(R.array.explanations).toList() }
//    var selectedGrade by remember { mutableStateOf(classOptions[0]) }
//    var selectedLanguage by remember { mutableStateOf(languageOptions[0]) }
//    var selectedExplanation by remember { mutableStateOf(explanationOptions[0]) }
    val selectedAiModel = viewModel.selectedAiModelOption
    val selectedGrade = viewModel.selectedGradeOption
    val selectedSolutionLanguage = viewModel.selectedSolutionLanguageOption
    val selectedExplanationLevel = viewModel.selectedExplanationLevelOption
    var additionalInformationText by remember { mutableStateOf("") }


    // remove
//    val classOptions = remember { context.resources.getStringArray(R.array.grades).toList() }
//    val languageOptions = remember { context.resources.getStringArray(R.array.languages).toList() }
//    val explanationOptions = remember { context.resources.getStringArray(R.array.explanations).toList() }
//    var selectedGrade by remember { mutableStateOf(classOptions[0]) }
//    var selectedLanguage by remember { mutableStateOf(languageOptions[0]) }
//    var selectedExplanation by remember { mutableStateOf(explanationOptions[0]) }
//    var additionalInformationText by remember { mutableStateOf("") }


    ApplicationScaffold {

        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.grade_label,
            selectedOption = selectedAiModel,
            options = AiModelOptions.entries.toList(),
            onOptionSelected = { viewModel.updateSelectedAiModelOption(it) },
            optionToString = { option, context -> option.getString(context) }
        )


        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.grade_label,
            selectedOption = selectedGrade,
            options = GradeOptions.entries.toList(),
            onOptionSelected = { viewModel.updateSelectedGradeOption(it) },
            optionToString = { option, context -> option.getString(context) }
        )

        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.solution_language_label,
            selectedOption = selectedSolutionLanguage,
            options = SolutionLanguageOptions.entries.toList(),
            onOptionSelected = { viewModel.updateSelectedLanguageOption(it) },
            optionToString = { option, context -> option.getString(context) }
        )

        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.explanations_label,
            selectedOption = selectedExplanationLevel,
            options = ExplanationLevelOptions.entries.toList(),
            onOptionSelected = { viewModel.updateSelectedExplanationLevelOption(it) },
            optionToString = { option, context -> option.getString(context) }
        )

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 95.dp),
            value = additionalInformationText,
            onValueChange = { additionalInformationText = it },
            label = {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.additional_information_TextField_label),
                    textAlign = TextAlign.Start
                )
            }
        )

        UniversalButton(
            modifier = modifier.fillMaxWidth(),
            label = R.string.next_button_label,
        ) {
            viewModel.selectedUri?.let {
                viewModel.fetchAIResponse(it, "", viewModel.selectedAiModelOption)
            }
            onNavigateToNextPage()
        }

    }
}


