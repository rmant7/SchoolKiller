package com.schoolkiller.ui.screens

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
import com.schoolkiller.ui.reusable_components.DropBox
import com.schoolkiller.ui.reusable_components.ScreenImage
import com.schoolkiller.ui.reusable_components.UniversalButton
import com.schoolkiller.utils.ClassOptions
import com.schoolkiller.utils.ExplanationLevelOptions
import com.schoolkiller.utils.SolutionLanguageOptions
import com.schoolkiller.view_model.SchoolKillerViewModel

@Composable
fun InstructionsScreen(
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
    val selectedClass = viewModel.selectedClassOption
    val selectedSolutionLanguage = viewModel.selectedSolutionLanguageOption
    val selectedExplanationLevel = viewModel.selectedExplanationLevelOption
    var additionalInformationText by remember { mutableStateOf("") }




    ApplicationScaffold(
    ) {

        ScreenImage(
            image = R.drawable.ai_school_assistant,
            contentDescription = R.string.ai_school_assistant_image_content_description
        )

        DropBox(
            context = context,
            maxHeightIn = 200.dp,
            xDpOffset = 155.dp,
            yDpOffset = (-30).dp,
            label = R.string.class_label,
            selectedOption = selectedClass,
            options = ClassOptions.entries.toList(),
            onOptionSelected = { viewModel.updateSelectedClassOption(it) },
            optionToString = { option, context -> option.getString(context) }
        )

        DropBox(
            context = context,
            maxHeightIn = 200.dp,
            xDpOffset = 155.dp,
            yDpOffset = (-30).dp,
            label = R.string.solution_language_label,
            selectedOption = selectedSolutionLanguage,
            options = SolutionLanguageOptions.entries.toList(),
            onOptionSelected = { viewModel.updateSelectedLanguageOption(it) },
            optionToString = { option, context -> option.getString(context) }
        )

        DropBox(
            context = context,
            maxHeightIn = 200.dp,
            xDpOffset = 155.dp,
            yDpOffset = (-30).dp,
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

        UniversalButton(label = R.string.next_button_label) {
            onNavigateToNextPage()
        }

    }
}


