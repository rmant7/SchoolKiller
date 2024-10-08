package com.schoolkiller.presentation.screens.result

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolkiller.R
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.ErrorAlertDialog
import com.schoolkiller.presentation.common.RoundIconButton
import com.schoolkiller.presentation.common.UniversalButton
import com.schoolkiller.presentation.toast.ShowToastMessage
import io.ktor.client.plugins.ServerResponseException


@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    passedPrompt: String,
    passedSystemInstruction: String,
    passedImageUri: Uri?,
    onNavigateToHomeScreen: () -> Unit,
) {
    val viewModel: ResultViewModel = hiltViewModel()
    val resultProperties = viewModel.resultPropertiesState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val responseListState = rememberLazyListState()
    val openAlertDialog = remember { mutableStateOf(resultProperties.error != null) }

    // ad views count, ad plays every 2 clicks or on first try
    val interstitialAdViewCount = remember { mutableIntStateOf(1) }

    // prompt which user can edit, initial value is passed prompt
    val shouldRecognizeText = remember { mutableStateOf(true) }
    val recognizedText = viewModel.recognizedText.collectAsState()

    val recognizedTextValue = stringResource(R.string.recognized_text_value)
    val solutionTextValue = stringResource(R.string.solution_text_value)

    // recognize text from image only once
    if (shouldRecognizeText.value) {
        if (passedImageUri != null)
            viewModel.geminiImageToText(
                imageUri = passedImageUri,
                fileName = passedImageUri.toString()
            )
        else ShowToastMessage.SOMETHING_WENT_WRONG.showToast()
        // and close the call
        shouldRecognizeText.value = false
    }

    if (resultProperties.requestGeminiResponse && !resultProperties.isResultFetchedStatus) {
        if (passedImageUri != null) {
            val editedSystemInstruction =
                if (recognizedText.value.isNotEmpty())
                // change to != error message
                    "$passedSystemInstruction " +
                            "Corrected task's text is: " + recognizedText.value
                else passedSystemInstruction

            viewModel.fetchGeminiResponse(
                imageUri = passedImageUri,
                fileName = passedImageUri.toString(),
                prompt = passedPrompt,
                systemInstruction = editedSystemInstruction
            )
            // result is fetched and this block wouldn't run
            // until new try request from user
            viewModel.updateResultFetchedStatus(true)

            interstitialAdViewCount.value += 1
            if (interstitialAdViewCount.intValue == 2)
                interstitialAdViewCount.intValue = 0

        } else {
            ShowToastMessage.SOMETHING_WENT_WRONG.showToast()
        }
    }

    ApplicationScaffold(
        isShowed = true,
        content = {
            // only show ad if it's loaded and user requested AI response

            if (resultProperties.error != null) {
                openAlertDialog.value = true

                val dialogData = getAlertWindowData(resultProperties.error)

                ErrorAlertDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = {
                        openAlertDialog.value = false
                        viewModel.clearError()
                        onNavigateToHomeScreen()
                    },
                    dialogTitle = stringResource(dialogData.first),
                    dialogText = stringResource(dialogData.second),
                    icon = Icons.Default.Info
                )
            }

            //Don't remove, solution images
            /*
            LazyColumn(
                modifier = modifier,
                    //.fillMaxHeight(0.35f),
                state = imageState,
                content = {
                    item {
                        image?.let {
                            SolutionImage(
                                image = it,
                                context = context,
                                contentDescription = resultText
                            )
                        }
                    }
                }
            )
            */

            LazyColumn(
                modifier = modifier
                    .fillMaxHeight(/*0.65f*/)
                    .padding(0.dp, 10.dp),
                state = responseListState,
                content = {


                    item { Spacer(modifier.height(16.dp)) }


                    item {
                        if (resultProperties.textGenerationResult.isBlank()
                            && resultProperties.error == null
                        ) {
                            Box(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center,
                                content = {

                                    if (resultProperties.requestGeminiResponse
                                        && interstitialAdViewCount.intValue == 0
                                    ) {
                                        viewModel.showInterstitialAd(context)
                                        viewModel.updateRequestGeminiResponse(false)
                                    } else {
                                        CircularProgressIndicator(modifier = modifier.size(80.dp))
                                    }

                                }
                            )
                        } else {

                            val isPromptReadOnly = remember { mutableStateOf(true) }
                            val isEditButtonVisible = remember { mutableStateOf(true) }
                            val isSendEditedPromptButtonVisible = remember {
                                mutableStateOf(false)
                            }
                            // user's editable prompt
                            Text(recognizedTextValue, fontSize = 30.sp)

                            SelectionContainer {
                                OutlinedTextField(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 10.dp),
                                    value = recognizedText.value,
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
                                    UniversalButton(
                                        label = R.string.send_edited_prompt
                                    ) {
                                        //prompt isn't editable, this button is invisible
                                        // edit button is visible
                                        isPromptReadOnly.value = true
                                        isEditButtonVisible.value = true
                                        isSendEditedPromptButtonVisible.value = false

                                        sendRequestToSolvePrompt(viewModel)
                                    }
                                }

                            }


                            // add vertical gap 30.dp
                            Spacer(Modifier.padding(0.dp, 15.dp))

                            // AI response
                            Text(solutionTextValue, fontSize = 30.sp)

                            SelectionContainer {
                                OutlinedTextField(
                                    modifier = modifier
                                        //.fillMaxSize(),
                                        .fillMaxWidth()
                                        .padding(0.dp, 10.dp),
                                    value = resultProperties.textGenerationResult,
                                    onValueChange = {},
                                    textStyle = TextStyle(
                                        fontSize = 25.sp,
                                        textAlign = TextAlign.Start
                                    ),
                                    readOnly = true
                                )
                            }
                        }
                    }
                }
            )
            //BannerAdContainer(adView = resultProperties.mediumBannerAdview)
        },
        bottomBar = {
            Column(
                modifier = Modifier.navigationBarsPadding(),
                content = {
                    // change to "error from gemini" instead of isEmpty check
                    if (recognizedText.value.isEmpty())
                        UniversalButton(
                            modifier = Modifier.fillMaxWidth(),
                            label = R.string.try_again
                        ) { sendRequestToSolvePrompt(viewModel) }

                    UniversalButton(
                        modifier = Modifier.fillMaxWidth(),
                        label = R.string.start_again
                    ) {
                        onNavigateToHomeScreen()
                    }
                }
            )
        }

    )
}

fun sendRequestToSolvePrompt(viewModel: ResultViewModel) {
    viewModel.updateTextGenerationResult("")
    viewModel.updateRequestGeminiResponse(true)
    viewModel.updateResultFetchedStatus(false)
}

private fun getAlertWindowData(t: Throwable?): Pair<Int, Int> {
    return when (t) {
        is ServerResponseException -> R.string.error_service_not_available_title to R.string.error_service_not_available
        else -> R.string.error_common_title to R.string.error_common_message
    }
}


