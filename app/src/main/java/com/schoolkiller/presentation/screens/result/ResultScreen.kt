package com.schoolkiller.presentation.screens.result

import android.content.Context
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.R
import com.schoolkiller.presentation.common.AlertDialog
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.UniversalButton
import io.ktor.client.plugins.ServerResponseException


@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    context: Context,
    originalPrompt: String,
    selectedImageUri: String,
    onNavigateToHomeScreen: () -> Unit,
) {
    val viewModel: ResultViewModel = hiltViewModel()
    val resultText: String by viewModel.textGenerationResult.collectAsState()
    val resultError: Throwable? by viewModel.error.collectAsState()

    val responseListState = rememberLazyListState()
    val requestGeminiResponse = viewModel.requestGeminiResponse.collectAsState()
    val openAlertDialog = remember { mutableStateOf(resultError != null) }

    val isResultFetched = viewModel.isResultFetchedStatus.collectAsState()

    /**
     * Attempt to fix following issue:
     * Gemini sometimes fetch 2-3 times with one call
     */

    // fetch only when user requested AI response
    // and result wasn't fetched yet
    if (requestGeminiResponse.value && !isResultFetched.value) {
        viewModel.fetchGeminiResponse(
            imageUri = selectedImageUri.toUri(),
            fileName = selectedImageUri.toUri().toString(),
            prompt = originalPrompt
        )
        // result is fetched and this block wouldn't run
        // until new try request from user
        viewModel.updateResultFetchedStatus(true)
    }


    ApplicationScaffold(
        content = {
            // only show ad if it's loaded and user requested AI response

            if (resultError != null) {
                openAlertDialog.value = true

                val dialogData = getAlertWindowData(resultError)

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
                    .fillMaxHeight(0.65f)
                    .padding(0.dp, 10.dp),
                state = responseListState,
                content = {


                    item { Spacer(modifier.height(16.dp)) }


                    item {
                        if (resultText.isBlank() && resultError == null) {
                            Box(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center,
                                content = {

                                    if (requestGeminiResponse.value) {
                                       viewModel.showInterstitialAd(context)
                                       viewModel.updateRequestGeminiResponse(false)
                                    } else {
                                        CircularProgressIndicator(modifier = modifier.size(80.dp))
                                    }

                                }
                            )
                        } else {

                            SelectionContainer {
                                OutlinedTextField(
                                    modifier = modifier
                                        .fillMaxWidth(),
                                    value = resultText,
                                    onValueChange = {},
                                    textStyle = TextStyle(
                                        fontSize = 20.sp,
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
        })
}


private fun getAlertWindowData(t: Throwable?): Pair<Int, Int> {
    return when (t) {
        is ServerResponseException -> R.string.error_service_not_available_title to R.string.error_service_not_available
        else -> R.string.error_common_title to R.string.error_common_message
    }
}


