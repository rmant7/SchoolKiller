package com.schoolkiller.presentation.screens.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.schoolkiller.presentation.common.UniversalButton


@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    passedPrompt: String,
    passedSystemInstruction: String,
    //passedImageUri: Uri?,
    onNavigateToHomeScreen: () -> Unit,
) {
    val viewModel: ResultViewModel = hiltViewModel()
    val resultProperties = viewModel.resultPropertiesState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val responseListState = rememberLazyListState()
    val openAlertDialog = remember { mutableStateOf(resultProperties.error != null) }

    // ad views count, ad plays every 2 clicks or on first try
    val interstitialAdViewCount = remember { mutableIntStateOf(1) }

    val solutionTextLabel = stringResource(R.string.solution_text_value)
    val invalidSolutionGenerationText = stringResource(
        R.string.error_gemini_solution_result_extraction
    )

    if (resultProperties.requestGeminiResponse && !resultProperties.isResultFetchedStatus) {
        //if (passedImageUri != null) {

        viewModel.geminiGenerateSolution(
            //imageUri = passedImageUri,
            //fileName = passedImageUri.toString(),
            systemInstruction = passedSystemInstruction,
            prompt = passedPrompt,
            textOnExtractionError = invalidSolutionGenerationText
        )
        // result is fetched and this block wouldn't run
        // until new try request from user
        viewModel.updateResultFetchedStatus(true)

        interstitialAdViewCount.value += 1
        if (interstitialAdViewCount.intValue == 2)
            interstitialAdViewCount.intValue = 0

        /*} else {
            ShowToastMessage.SOMETHING_WENT_WRONG.showToast()
        }*/
    }

    ApplicationScaffold(
        isShowed = true,
        content = {
            // only show ad if it's loaded and user requested AI response

            if (resultProperties.error != null) {
                openAlertDialog.value = true

                ErrorAlertDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = {
                        openAlertDialog.value = false
                        viewModel.clearError()
                        onNavigateToHomeScreen()
                    },
                    throwable = resultProperties.error,
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

                            // AI response
                            Text(solutionTextLabel, fontSize = 30.sp)

                            SelectionContainer {

                                /** For tests */

                                /*
                                HtmlTextView(
                                    resultProperties.textGenerationResult,
                                    // Should be just 2 Composables:
                                    // one with mutable state and other one with boolean
                                    remember { mutableStateOf(false) }
                                )
                                 */

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
        },
        bottomBar = {
            Column(
                modifier = Modifier.navigationBarsPadding(),
                content = {

                    UniversalButton(
                        modifier = Modifier.fillMaxWidth(),
                        label = R.string.try_again
                    ) {
                        viewModel.updateTextGenerationResult("")
                        viewModel.updateRequestGeminiResponse(true)
                        viewModel.updateResultFetchedStatus(false)
                    }

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


