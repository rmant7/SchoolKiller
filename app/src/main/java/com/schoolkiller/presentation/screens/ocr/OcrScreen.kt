package com.schoolkiller.presentation.screens.ocr

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolkiller.R
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.RoundIconButton
import com.schoolkiller.presentation.common.UniversalButton
import com.schoolkiller.presentation.toast.ShowToastMessage

@Composable
fun OcrScreen(
    viewModel: OcrViewModel,
    onNavigateToParametersScreen: () -> Unit,
    onNavigateToCheckSolutionOptionsScreen: () -> Unit,
    passedImageUri: Uri?,
) {

    val recognizedText = viewModel.recognizedText.collectAsState()
    val ocrError = viewModel.ocrError.collectAsState()

    val shouldRecognizeText = remember { mutableStateOf(true) }

    val recognizedTextLabel = stringResource(R.string.recognized_text_value)
    val invalidOcrResultText = stringResource(R.string.error_gemini_ocr_result_extraction)

    val isPromptReadOnly = remember { mutableStateOf(true) }
    val isEditButtonVisible = remember { mutableStateOf(true) }
    val isSendEditedPromptButtonVisible = remember {
        mutableStateOf(false)
    }

    if (shouldRecognizeText.value) {
        viewModel.updateRecognizedText("")
        viewModel.updateOcrError(null)

        if (passedImageUri != null)
            viewModel.geminiImageToText(
                imageUri = passedImageUri,
                fileName = passedImageUri.toString(),
                textOnExtractionError = invalidOcrResultText
            )
        else ShowToastMessage.SOMETHING_WENT_WRONG.showToast()
        // and close the call
        shouldRecognizeText.value = false
    }

    ApplicationScaffold(
        isShowed = true,
        content = {
            if (recognizedText.value.isNullOrEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center,
                    content = {
                        CircularProgressIndicator(modifier = Modifier.size(80.dp))
                    })

            } else {
                // editable prompt
                Text(recognizedTextLabel, fontSize = 30.sp)

                SelectionContainer {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp),
                        value = recognizedText.value!!,
                        onValueChange = {
                            viewModel.updateRecognizedText(it)
                        },
                        textStyle = TextStyle(
                            fontSize = 25.sp,
                            textAlign = TextAlign.Start
                        ),
                        readOnly = isPromptReadOnly.value
                    )
                }

                Row {

                    RoundIconButton(
                        icon = R.drawable.retry_svg
                    ) {
                        shouldRecognizeText.value = true
                        viewModel.updateRecognizedText("")
                    }

                    Spacer(Modifier.weight(1f))
                    if (isEditButtonVisible.value)
                        RoundIconButton(
                            iconModifier = Modifier.size(30.dp),
                            icon = R.drawable.edit_svg
                        ) {
                            //prompt is editable, this button is invisible
                            // send button is visible
                            isPromptReadOnly.value = false
                            isEditButtonVisible.value = false
                            isSendEditedPromptButtonVisible.value = true
                        } else {
                        UniversalButton(label = R.string.Ok) {
                            //prompt isn't editable, this button is invisible
                            // edit button is visible
                            isPromptReadOnly.value = true
                            isEditButtonVisible.value = true
                            isSendEditedPromptButtonVisible.value = false
                        }
                    }
                }
            }

        }, bottomBar = {
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {

                fun onNextClick(onNavigate: (/*String*/) -> Unit) {
                    if (ocrError.value != null || recognizedText.value.isNullOrBlank())
                        ShowToastMessage.PROMPT_IS_EMPTY.showToast()
                    else
                        onNavigate(/*recognizedText.value!!*/)
                    // it's better to pass string argument
                }

                // navigate to check solution options
                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.check_solution_button_label,
                ) {
                    onNextClick { onNavigateToCheckSolutionOptionsScreen() }
                }

                // navigate to solve task options
                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.solve_button_label,
                ) {
                    onNextClick { onNavigateToParametersScreen() }
                }
            }

        })

}