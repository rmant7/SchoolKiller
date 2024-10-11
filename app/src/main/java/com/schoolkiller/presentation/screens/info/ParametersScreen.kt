package com.schoolkiller.presentation.screens.info

import ExposedDropBox
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolkiller.R
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.SolutionLanguageOption
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.AttentionAlertDialog
import com.schoolkiller.presentation.common.ScreenImage
import com.schoolkiller.presentation.common.UniversalButton


@Composable
fun ParametersScreen(
    modifier: Modifier = Modifier,
    recognizedText: String?,
    onNavigateToResultScreen: (String, String) -> Unit
) {
    val viewModel: ParametersViewModel = hiltViewModel()
    val parameterScreenProperties =
        viewModel.parametersPropertiesState.collectAsStateWithLifecycle().value

    /** Testing the prompt in a dialog if the values are correct. Check also the solve button*/
    var isAttentionDialogShowed by remember { mutableStateOf(false) }
    var proceedToResultScreen by remember { mutableStateOf(false) }
    AttentionAlertDialog(
        isShowed = isAttentionDialogShowed,
        message = parameterScreenProperties.solvePromptText,
        onDismiss = { isAttentionDialogShowed = false },
        onCancel = { isAttentionDialogShowed = false },
        onConfirm = {
            isAttentionDialogShowed = false
            proceedToResultScreen = true
        }
    )


    ApplicationScaffold(
        isShowed = true,
        content = {

            ScreenImage(
                modifier = modifier
                    .fillMaxHeight(0.35f), // adjust the height of the image from here
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
                label = R.string.grade_label,
                selectedOption = parameterScreenProperties.grade,
                options = GradeOption.entries.toList(),
                onOptionSelected = {
                    viewModel.updateSelectedGradeOption(it)
                },
                optionToString = { option, context -> option.getString(context) }
            )

            ExposedDropBox(
                maxHeightIn = 200.dp,
                label = R.string.solution_language_label,
                selectedOption = parameterScreenProperties.language,
                options = SolutionLanguageOption.entries.toList(),
                onOptionSelected = {
                    viewModel.updateSelectedLanguageOption(it)
                },
                optionToString = { option, context -> option.getString(context) }
            )

            ExposedDropBox(
                maxHeightIn = 200.dp,
                label = R.string.explanations_label,
                selectedOption = parameterScreenProperties.explanationLevel,
                options = ExplanationLevelOption.entries.toList(),
                onOptionSelected = {
                    viewModel.updateSelectedExplanationLevelOption(it)
                },
                optionToString = { option, context -> option.getString(context) }
            )

            val textColor = if (parameterScreenProperties.description.isEmpty())
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
                value = parameterScreenProperties.description,
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
                visualTransformation = if (parameterScreenProperties.description.isEmpty())
                    PlaceholderTransformation(placeholder = placeHolder.value)
                else VisualTransformation.None,
                textStyle = TextStyle(color = textColor)
            )

        }, bottomBar = {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                //Reused Component
                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.solve_button_label,
                ) {

                    viewModel.buildSolvingPrompt()
                    /** testing the prompt : uncomment */
//                    isAttentionDialogShowed = true
                    /** testing the prompt : uncomment */
//                    if (proceedToResultScreen) {
                    /** testing the prompt : uncomment */
//                        isAttentionDialogShowed = false

                    // on back press from ResultScreen we have to restore requestGeminiResponse back to true
//                        resultViewModel.updateRequestGeminiResponse(true)

                    // reset TextGenerationResult to initialize the loading indicator
//                        resultViewModel.updateTextGenerationResult("")

                    onNavigateToResultScreen(
                        parameterScreenProperties.solvePromptText
                                + " User's solution is: $recognizedText",
                        "Answer only in ${parameterScreenProperties.language.languageName}." +
                                "Answer only in plain text. Do not use markdown."
                    )
                    /** testing the prompt : uncomment */
//                    }

                }
            }
        }
    )
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






