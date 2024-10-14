package com.schoolkiller.presentation.screens.ocr

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.HtmlTextView
import com.schoolkiller.presentation.common.RoundIconButton
import com.schoolkiller.presentation.common.UniversalButton
import com.schoolkiller.presentation.toast.ShowToastMessage

@Composable
fun OcrScreen(
    onNavigateToParametersScreen: (String) -> Unit,
    onNavigateToCheckSolutionOptionsScreen: (String) -> Unit,
    passedImageUri: Uri?,
) {

    val viewModel: OcrViewModel = hiltViewModel()
    val recognizedText = viewModel.recognizedText.collectAsState()
    val ocrError = viewModel.ocrError.collectAsState()

    val shouldRecognizeText = remember { mutableStateOf(true) }

    val recognizedTextLabel = stringResource(R.string.recognized_text_value)
    val invalidOcrResultText = stringResource(R.string.error_gemini_ocr_result_extraction)

    val isPromptEditable = remember { mutableStateOf(false) }
    val isEditButtonVisible = remember { mutableStateOf(true) }
    val isSendEditedPromptButtonVisible = remember {
        mutableStateOf(false)
    }

    val shouldShowErrorMessage = remember { mutableStateOf(true) }

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
            if (ocrError.value == null && recognizedText.value.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center,
                    content = {
                        CircularProgressIndicator(modifier = Modifier.size(80.dp))
                    })

            } else {

                if (ocrError.value != null && shouldShowErrorMessage.value) {
                    ShowToastMessage.SOMETHING_WENT_WRONG.showToast()
                    shouldShowErrorMessage.value = false
                }
                // editable prompt
                Text(recognizedTextLabel, fontSize = 30.sp)

                SelectionContainer(Modifier.fillMaxHeight(0.7f)) {
                    HtmlTextView(
                        recognizedText.value!!,
                        isPromptEditable //isPromptEditable.value
                    )
                    { viewModel.updateRecognizedText(it) }
                    /* OutlinedTextField(
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
                     )*/
                }


                Row {

                    RoundIconButton(
                        icon = R.drawable.retry_svg
                    ) {
                        shouldRecognizeText.value = true
                        viewModel.updateOcrError(null)
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
                            isPromptEditable.value = true
                            isEditButtonVisible.value = false
                            isSendEditedPromptButtonVisible.value = true
                        } else {
                        UniversalButton(label = R.string.Ok) {
                            //prompt isn't editable, this button is invisible
                            // edit button is visible
                            isPromptEditable.value = false
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

                fun onNextClick(onNavigate: () -> Unit) {
                    if (recognizedText.value.isNullOrBlank())
                        ShowToastMessage.PROMPT_IS_EMPTY.showToast()
                    else onNavigate()
                }

                // navigate to check solution options
                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.check_solution_button_label,
                ) {
                    onNextClick {
                        onNavigateToCheckSolutionOptionsScreen(recognizedText.value!!)
                    }
                }

                // navigate to solve task options
                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.solve_button_label,
                ) {
                    onNextClick {
                        onNavigateToParametersScreen(recognizedText.value!!)
                    }
                }
            }

        })

}

