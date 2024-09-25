package com.schoolkiller.presentation.screens

import ExposedDropBox
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.domain.ExplanationLevelOptions
import com.schoolkiller.domain.GradeOptions
import com.schoolkiller.domain.SolutionLanguageOptions
import com.schoolkiller.presentation.SchoolKillerViewModel
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.ScreenImage
import com.schoolkiller.presentation.common.UniversalButton

@Composable
fun AdditionalInformationScreen(
    // moved modifier as first parameter as it should be always
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
    onNavigateToResultScreen: () -> Unit
) {

    //    val classOptions = remember { context.resources.getStringArray(R.array.classes).toList() }
//    val languageOptions = remember { context.resources.getStringArray(R.array.languages).toList() }
//    val explanationOptions = remember { context.resources.getStringArray(R.array.explanations).toList() }
//    var selectedGrade by remember { mutableStateOf(classOptions[0]) }
//    var selectedLanguage by remember { mutableStateOf(languageOptions[0]) }
//    var selectedExplanation by remember { mutableStateOf(explanationOptions[0]) }
//    val selectedAiModel = viewModel.selectedAiModelOption
    val selectedGrade = viewModel.selectedGradeOption
    val selectedSolutionLanguage = viewModel.selectedSolutionLanguageOption
    val selectedExplanationLevel = viewModel.selectedExplanationLevelOption
    val additionalInformationText = viewModel.additionalInfoText.collectAsState() // changed to Val from Var

    // remove
//    val classOptions = remember { context.resources.getStringArray(R.array.grades).toList() }
//    val languageOptions = remember { context.resources.getStringArray(R.array.languages).toList() }
//    val explanationOptions = remember { context.resources.getStringArray(R.array.explanations).toList() }
//    var selectedGrade by remember { mutableStateOf(classOptions[0]) }
//    var selectedLanguage by remember { mutableStateOf(languageOptions[0]) }
//    var selectedExplanation by remember { mutableStateOf(explanationOptions[0]) }
//    var additionalInformationText by remember { mutableStateOf("") }


    ApplicationScaffold {

        ScreenImage(
            image = R.drawable.ai_school_assistant,
            contentDescription = R.string.ai_school_assistant_image_content_description
        )

        //Don't remove, for feature development
        /*        ExposedDropBox(
                    maxHeightIn = 200.dp,
                    context = context,
                    label = R.string.grade_label,
                    selectedOption = selectedAiModel,
                    options = AiModelOptions.entries.toList(),
                    onOptionSelected = { viewModel.updateSelectedAiModelOption(it) },
                    optionToString = { option, context -> option.getString(context) }
                )
        */
        //Reused Component
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

        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.solution_language_label,
            selectedOption = selectedSolutionLanguage,
            options = SolutionLanguageOptions.entries.toList(),
            onOptionSelected = {
                viewModel.updateSelectedLanguageOption(it)
            },
            optionToString = { option, context -> option.getString(context) }
        )

        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.explanations_label,
            selectedOption = selectedExplanationLevel,
            options = ExplanationLevelOptions.entries.toList(),
            onOptionSelected = {
                viewModel.updateSelectedExplanationLevelOption(it)
            },
            optionToString = { option, context -> option.getString(context) }
        )

        //gray color for placeholder
        //black color for input text
        val textColor = if (additionalInformationText.value.isEmpty())
            Color.Gray
        else Color.Black

        val defaultPlaceholderText =
            stringResource(R.string.additional_info_TextField_placeholder_text)
        val placeHolder = remember {
            mutableStateOf(defaultPlaceholderText)
        }

        OutlinedTextField(
            modifier = modifier
                .onFocusChanged {
                    if (it.isFocused) {
                        //placeholder isn't visible on user input focus
                        placeHolder.value = ""
                    }
                }
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            value = additionalInformationText.value,
            onValueChange = {
                viewModel.updateAdditionalInfoText(it)
            },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.additional_information_TextField_label
                    )
                )
            },
            //added for label always be visible
            visualTransformation = if (additionalInformationText.value.isEmpty())
                PlaceholderTransformation(placeholder = placeHolder.value)
            else VisualTransformation.None,
            textStyle = TextStyle(color = textColor)
        )

        //Reused Component
        UniversalButton(
            modifier = modifier.fillMaxWidth(),
            label = R.string.solve_button_label,
        ) {
            viewModel.updateTextGenerationResult("")

            /**
             * Imports for updated prompt are moved here
             * as otherwise imports are included only by clicking on options
             * on this screen and are overwritten by original prompt every time
             * when user doesn't select options on this screen
             * and return to the Home_Screen.
             * Code line in Home_Screen which causes overwrite:
             * viewModel.updatePrompt(
             *             context.getString(R.string.prompt_text)
             *         )
             */

            viewModel.importGradeToOriginalPrompt()
            viewModel.importLanguageToOriginalPrompt()
            viewModel.importExplanationToOriginalPrompt()
            viewModel.importAdditionalInfoToOriginalPrompt()

            // on back press from ResultScreen we have to restore requestGeminiResponse back to true
            viewModel.updateRequestGeminiResponse(true)
            onNavigateToResultScreen()
        }

    }
}

class PlaceholderTransformation(private val placeholder: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return placeholderFilter(placeholder)
    }
}

fun placeholderFilter(placeholder: String): TransformedText {

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return 0
        }

        override fun transformedToOriginal(offset: Int): Int {
            return 0
        }
    }

    return TransformedText(AnnotatedString(placeholder), numberOffsetTranslator)
}






