package com.schoolkiller.presentation.screens.info

import ExposedDropBox
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.SolutionLanguageOption
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.ScreenImage
import com.schoolkiller.presentation.common.UniversalButton

@Composable
fun ParametersScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onNavigateToResultScreen: (String) -> Unit
) {
    val viewModel: ParametersViewModel = hiltViewModel()
    val selectedGrade = viewModel.selectedGradeOption.collectAsState()
    val selectedSolutionLanguage = viewModel.selectedSolutionLanguageOption.collectAsState()
    val selectedExplanationLevel = viewModel.selectedExplanationLevelOption.collectAsState()
    val descriptionText: String by viewModel.descriptionText.collectAsState() // changed to Val from Var

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
            selectedOption = selectedGrade.value,
            options = GradeOption.entries.toList(),
            onOptionSelected = {
                viewModel.updateSelectedGradeOption(it)
            },
            optionToString = { option, context -> option.getString(context) }
        )

        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.solution_language_label,
            selectedOption = selectedSolutionLanguage.value,
            options = SolutionLanguageOption.entries.toList(),
            onOptionSelected = {
                viewModel.updateSelectedLanguageOption(it)
            },
            optionToString = { option, context -> option.getString(context) }
        )

        ExposedDropBox(
            maxHeightIn = 200.dp,
            context = context,
            label = R.string.explanations_label,
            selectedOption = selectedExplanationLevel.value,
            options = ExplanationLevelOption.entries.toList(),
            onOptionSelected = {
                viewModel.updateSelectedExplanationLevelOption(it)
            },
            optionToString = { option, context -> option.getString(context) }
        )

        val textColor = if (descriptionText.isEmpty())
            MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.primary

        val defaultPlaceholderText =
            stringResource(R.string.additional_info_TextField_placeholder_text)
        val placeHolder = remember {
            mutableStateOf(defaultPlaceholderText)
        }

        OutlinedTextField(
            modifier = modifier
                .onFocusChanged {
                    placeHolder.value =
                        if (it.isFocused) ""
                        else defaultPlaceholderText
                }
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            value = descriptionText,
            onValueChange = {
                viewModel.updateDescriptionText(it)
            },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.additional_information_TextField_label
                    )
                )
            },
            //added for label always be visible
            visualTransformation = if (descriptionText.isEmpty())
                PlaceholderTransformation(placeholder = placeHolder.value)
            else VisualTransformation.None,
            textStyle = TextStyle(color = textColor)
        )

            UniversalButton(
                modifier = modifier.fillMaxWidth(),
                label = R.string.solve_button_label,
            ) {
                viewModel.buildPropertiesPrompt()
                onNavigateToResultScreen(viewModel.originalPrompt.value)
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






