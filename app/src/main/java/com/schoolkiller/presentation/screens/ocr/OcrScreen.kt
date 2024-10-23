package com.schoolkiller.presentation.screens.ocr

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.presentation.NotificationHandler
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.button.RadioIndexButton
import com.schoolkiller.presentation.common.button.RoundIconButton
import com.schoolkiller.presentation.common.button.TextAlignmentButton
import com.schoolkiller.presentation.common.button.UniversalButton
import com.schoolkiller.presentation.common.dialog.ErrorAlertDialog
import com.schoolkiller.presentation.common.web_view.HtmlTextView


@Composable
fun OcrScreen(
    passedImageUri: Uri?,
    onNavigateToParametersScreen: (String) -> Unit,
    onNavigateToCheckSolutionOptionsScreen: (String) -> Unit,
    onNavigateToHomeScreen: () -> Unit
) {

    val viewModel: OcrViewModel = hiltViewModel()
    val context = LocalContext.current

    // Notification handler
   /* val notificationHandler = NotificationHandler(context)
    val shouldShowOcrNotification = viewModel.shouldShowOcrNotification.collectAsState()*/

    val htmlGeminiResponses = remember { viewModel.htmlGeminiResponses }
    val tesseractOcrResult = viewModel.tesseractOcrResult.collectAsState()

    // user chosen version of ocr
    val selectedOcrResultId = remember { mutableIntStateOf(0) }
    val selectedText = viewModel.selectedText.collectAsState()

    val ocrError = viewModel.ocrError.collectAsState()

    val shouldRecognizeText = viewModel.shouldRecognizeText.collectAsState()

    val recognizedTextLabel = stringResource(R.string.recognized_text_value)
    val invalidOcrResultText = stringResource(R.string.error_gemini_ocr_result_extraction)
    //val firstOcrResultIsNotReady = stringResource(R.string.first_ocr_result_is_not_ready)
    val promptIsEmptyWarning = stringResource(R.string.prompt_is_empty)

    val textDirection = viewModel.textDirection.collectAsState()

    val isPromptEditable = remember { mutableStateOf(false) }
    val isEditButtonVisible = remember { mutableStateOf(true) }
    val isSendEditedPromptButtonVisible = remember {
        mutableStateOf(false)
    }

    val shouldShowErrorMessage = remember { mutableStateOf(true) }

    if (shouldRecognizeText.value) {
        // replace first recognized result with placeholder string
        viewModel.clearRecognizedTextList()
        selectedOcrResultId.intValue = 0

        viewModel.updateOcrError(null)

        if (passedImageUri != null) {
            // viewModel.tessaractImageToText(passedImageUri, context)
            viewModel.geminiImageToText(
                imageUri = passedImageUri,
                fileName = passedImageUri.toString(),
                invalidOcrResultText
            )
        } else viewModel.updateOcrError(RuntimeException())
        // and close the call
        viewModel.updateShouldRecognizeText(false)
    }

    /* if (shouldShowOcrNotification.value) {
         notificationHandler.showNotification("Ocr",
             "Ocr result  ${htmlGeminiResponses.size} is ready")
         viewModel.updateShouldShowOcrNotification(false)
     }*/

    if (ocrError.value == null && htmlGeminiResponses.isEmpty())
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center,
            content = {
                CircularProgressIndicator(modifier = Modifier.size(80.dp))
            })
    else
        ApplicationScaffold(
            isShowed = true,
            content = {

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

                    /*
                // Compose text field, first result
                if (selectedOcrResultId.intValue == 0)
                    CompositionLocalProvider(
                        LocalLayoutDirection provides textFieldLayoutDir.value
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 10.dp),
                            value = noHtmlText.value,
                            onValueChange = {
                                viewModel.updateNoHtmlGeminiResponse(it)
                                viewModel.updateSelectedText(it)
                            },
                            textStyle = TextStyle(
                                fontSize = 25.sp,
                                textAlign = TextAlign.Start
                            ),
                            readOnly = !isPromptEditable.value,
                        )
                    }
                    */

                    // HTML view, first result
                    if (htmlGeminiResponses.isNotEmpty() && selectedOcrResultId.intValue == 0)
                        HtmlTextView(
                            htmlContent = htmlGeminiResponses[0],
                            isEditable = isPromptEditable.value,
                            onValueChange = {
                                viewModel.replaceRecognizedText(0, it)
                                viewModel.updateSelectedText(it)
                            },
                            textAlign = textDirection.value,
                        )

                    // HTML view, second result
                    if (selectedOcrResultId.intValue == 1)
                        HtmlTextView(
                            htmlContent = htmlGeminiResponses[1],
                            isEditable = isPromptEditable.value,
                            onValueChange = {
                                viewModel.replaceRecognizedText(1, it)
                                viewModel.updateSelectedText(it)
                            },
                            textAlign = textDirection.value
                        )

                    // Tesseract, third result
                    /*if (selectedOcrResultId.intValue == 2)
                    CompositionLocalProvider(
                        LocalLayoutDirection provides textDirection.value
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 10.dp),
                            value = tesseractOcrResult.value,
                            onValueChange = {
                                viewModel.updateTesseractOcrResult(it)
                                viewModel.updateSelectedText(it)
                            },
                            textStyle = TextStyle(
                                fontSize = 25.sp,
                                textAlign = TextAlign.Start
                            ),
                            readOnly = !isPromptEditable.value,
                        )
                    }*/

                }

                fun resetUI() {
                    isPromptEditable.value = false
                    isEditButtonVisible.value = true
                    isSendEditedPromptButtonVisible.value = false
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    RoundIconButton(
                        icon = R.drawable.retry_svg
                    ) {
                        viewModel.updateShouldRecognizeText(true)
                        viewModel.updateOcrError(null)
                    }

                    TextAlignmentButton(
                        layoutDirection = textDirection.value,
                        onUpdate = {
                            viewModel.updateTextDirection(it)
                        }
                    )

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
                            resetUI()
                        }
                    }
                }

                // Toggle buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    fun isSelected(index: Int): Boolean {
                        val isSelected = selectedOcrResultId.intValue == index
                        return isSelected
                    }

                    // Should be in Radio button group
                    RadioIndexButton(
                        index = 0,
                        isSelected = isSelected(0),
                        isEnabled = htmlGeminiResponses.size >= 1,
                        onSelected = {
                            resetUI()
                            selectedOcrResultId.intValue = 0
                            viewModel.updateSelectedText(htmlGeminiResponses[0])
                        }
                    )

                    RadioIndexButton(
                        index = 1,
                        isSelected = isSelected(1),
                        isEnabled = htmlGeminiResponses.size >= 2,
                        onSelected = {
                            resetUI()
                            selectedOcrResultId.intValue = 1
                            viewModel.updateSelectedText(htmlGeminiResponses[1])
                        }
                    )

                    /*RadioIndexButton(
                    index = 2,
                    selectedIndex = selectedOcrResultId,
                    isEnabled = tesseractOcrResult.value.isNotEmpty(),
                    onSelected = {
                        resetUI()
                        viewModel.updateSelectedText(tesseractOcrResult.value)
                    }
                )*/

                }
                // }

            }, bottomBar = {
                Column(
                    modifier = Modifier.navigationBarsPadding()
                ) {

                    fun onNextClick(
                        onNavigate: () -> Unit
                    ) {
                        if (selectedText.value.isBlank())
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
                                selectedText.value
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
                                selectedText.value
                            )
                        }
                    }
                }

            })

}



