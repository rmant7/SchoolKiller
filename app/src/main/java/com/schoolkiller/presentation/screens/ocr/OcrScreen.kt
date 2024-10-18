package com.schoolkiller.presentation.screens.ocr

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.button.RadioIndexButton
import com.schoolkiller.presentation.common.dialog.ErrorAlertDialog
import com.schoolkiller.presentation.common.button.RoundIconButton
import com.schoolkiller.presentation.common.button.UniversalButton

@Composable
fun OcrScreen(
    passedImageUri: Uri?,
    onNavigateToParametersScreen: (String) -> Unit,
    onNavigateToCheckSolutionOptionsScreen: (String) -> Unit,
    onNavigateToHomeScreen: () -> Unit
) {

    val viewModel: OcrViewModel = hiltViewModel()
    val context = LocalContext.current
    // user chosen version of ocr
    val selectedOcrResultId = remember { mutableIntStateOf(0) }
    val recognizedTextList = remember { viewModel.recognizedTextList }
    val ocrError = viewModel.ocrError.collectAsState()

    val shouldRecognizeText = remember { mutableStateOf(true) }

    val recognizedTextLabel = stringResource(R.string.recognized_text_value)
    val invalidOcrResultText = stringResource(R.string.error_gemini_ocr_result_extraction)
    val firstOcrResultIsNotReady = stringResource(R.string.first_ocr_result_is_not_ready)
    val promptIsEmptyWarning = stringResource(R.string.prompt_is_empty)

    val isPromptEditable = remember { mutableStateOf(false) }
    val isEditButtonVisible = remember { mutableStateOf(true) }
    val isSendEditedPromptButtonVisible = remember {
        mutableStateOf(false)
    }

    val shouldShowErrorMessage = remember { mutableStateOf(true) }

    if (shouldRecognizeText.value) {

        // reset list
        if (recognizedTextList.isNotEmpty())
            viewModel.clearRecognizedTextList()

        // replace first recognized result with placeholder string
        viewModel.replaceRecognizedText(0, firstOcrResultIsNotReady)
        selectedOcrResultId.intValue = 0

        viewModel.updateOcrError(null)

        if (passedImageUri != null)
            viewModel.geminiImageToText(
                imageUri = passedImageUri,
                fileName = passedImageUri.toString(),
                false,
                invalidOcrResultText
            )
        else viewModel.updateOcrError(RuntimeException())
        // and close the call
        shouldRecognizeText.value = false
    }

    ApplicationScaffold(
        isShowed = true,
        content = {

            /*if (ocrError.value == null && recognizedTextList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center,
                    content = {
                        CircularProgressIndicator(modifier = Modifier.size(80.dp))
                    })

            } else {*/

            if (ocrError.value != null && shouldShowErrorMessage.value) {

                ErrorAlertDialog(
                    onDismissRequest = {
                        shouldShowErrorMessage.value = false
                    },
                    onConfirmation = {
                        shouldShowErrorMessage.value = false
                        viewModel.updateOcrError(null)
                        onNavigateToHomeScreen()
                    },
                    throwable = ocrError.value!!,
                    icon = Icons.Default.Info
                )
            }

            // editable prompt
            Text(recognizedTextLabel, fontSize = 30.sp)

            SelectionContainer(Modifier.fillMaxHeight(0.7f)) {

                /**
                 * Both views are left for testing along with old and new prompts
                 */

                // HTML view
                /*
                    HtmlTextView(
                        recognizedText.value!!,
                        isPromptEditable
                    )
                    { viewModel.updateRecognizedText(it) }

                 */

                // Compose text field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp),
                    value = recognizedTextList[selectedOcrResultId.intValue],
                    onValueChange = {
                        viewModel.replaceRecognizedText(selectedOcrResultId.intValue, it)
                    },
                    textStyle = TextStyle(
                        fontSize = 25.sp,
                        textAlign = TextAlign.Start
                    ),
                    readOnly = !isPromptEditable.value
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                RoundIconButton(
                    icon = R.drawable.retry_svg
                ) {
                    shouldRecognizeText.value = true
                    viewModel.updateOcrError(null)
                }

                // Toggle buttons
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioIndexButton(
                        index = 0,
                        selectedIndex = selectedOcrResultId,
                        indexMax = {
                            recognizedTextList.size
                        }
                    )

                    RadioIndexButton(
                        index = 1,
                        selectedIndex = selectedOcrResultId,
                        indexMax = {
                            recognizedTextList.size
                        }
                    )

                    RadioIndexButton(
                        index = 2,
                        selectedIndex = selectedOcrResultId,
                        indexMax = {
                            recognizedTextList.size
                        }
                    )

                }

                //Spacer(Modifier.weight(1f))
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
            // }

        }, bottomBar = {
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {
                val selectedText = recognizedTextList[selectedOcrResultId.intValue]

                fun onNextClick(
                    onNavigate: () -> Unit
                ) {
                    if (selectedText.isBlank())
                        Toast.makeText(
                            context,
                            promptIsEmptyWarning, Toast.LENGTH_SHORT
                        ).show()
                    else onNavigate()
                }

                // navigate to check solution options
                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.check_solution_button_label,
                ) {
                    onNextClick {
                        onNavigateToCheckSolutionOptionsScreen(
                            selectedText
                        )
                    }
                }

                // navigate to solve task options
                UniversalButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = R.string.solve_button_label,
                ) {
                    onNextClick {
                        onNavigateToParametersScreen(
                            selectedText
                        )
                    }
                }
            }

        })

}


