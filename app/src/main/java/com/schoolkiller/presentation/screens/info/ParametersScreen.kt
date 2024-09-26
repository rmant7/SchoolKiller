package com.schoolkiller.presentation.screens.info

import ExposedDropBox
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
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
    onNavigateToResultScreen: () -> Unit
) {
    val viewModel: ParametersViewModel = hiltViewModel()
    val selectedGrade = viewModel.selectedGradeOption
    val selectedSolutionLanguage = viewModel.selectedSolutionLanguageOption
    val selectedExplanationLevel = viewModel.selectedExplanationLevelOption
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
            selectedOption = selectedGrade,
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
            selectedOption = selectedSolutionLanguage,
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
            selectedOption = selectedExplanationLevel,
            options = ExplanationLevelOption.entries.toList(),
            onOptionSelected = {
                viewModel.updateSelectedExplanationLevelOption(it)
            },
            optionToString = { option, context -> option.getString(context) }
        )

        //todo: need to fix. Use color from theme. This color doesn't feet for dark theme
        //gray color for placeholder
        //black color for input text
        val textColor = if (descriptionText.isEmpty())
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
                .height(60.dp)
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
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val gradeArray: Array<String> = stringArrayResource(R.array.grades)
            val languageArray: Array<String> = stringArrayResource(R.array.languages)
            val explanationArray: Array<String> = stringArrayResource(R.array.explanations)

            UniversalButton(
                modifier = modifier.fillMaxWidth(),
                label = R.string.solve_button_label,
            ) {
                viewModel.updatePropertiesPrompt(
                    gradeArray = gradeArray,
                    languageArray = languageArray,
                    explanationArray = explanationArray
                )
                onNavigateToResultScreen()
            }
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






