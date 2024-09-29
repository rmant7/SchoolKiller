package com.schoolkiller.presentation.screens.result

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.schoolkiller.presentation.ads.InterstitialAdPresenter
import com.schoolkiller.presentation.common.AlertDialog
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.common.UniversalButton
import com.schoolkiller.presentation.common.getSystemLocale
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
    val systemLocale = getSystemLocale()

    val interstitialAd = viewModel.interstitialAd.collectAsState()


    /**
     * Gemini sometimes fetch 2-3 times with one call
     */
    if (requestGeminiResponse.value) {
        viewModel.fetchGeminiResponse(
            imageUri = selectedImageUri.toUri(),
            fileName = selectedImageUri.toUri().toString(),
            prompt = originalPrompt
        )
    }


    ApplicationScaffold {

        if (interstitialAd.value != null) {

            InterstitialAdPresenter(
                context = context,
                interstitialAd = interstitialAd.value!!,
                viewModel = viewModel,
                showAd = requestGeminiResponse.value
            ).apply {
                viewModel.updateRequestGeminiResponse(false)
                viewModel.loadInterstitialAd()
            }

        }



        if (resultError != null) {
            openAlertDialog.value = true

            val dialogData = getAlertWindowData(resultError)

            AlertDialog(
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
                .fillMaxHeight(0.65f),
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
                                CircularProgressIndicator(modifier = modifier.size(80.dp))
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

        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = {

                UniversalButton(
                    modifier = when(systemLocale.language) {
                        "iw" -> {
                            modifier
                                .weight(1f)
                                .fillMaxHeight(0.4f)
                        }

                        "ru" -> {
                            modifier
                                .weight(1f)
                                .fillMaxHeight(0.4f)
                        }

                        else -> {
                            modifier
                                .weight(1f)
                                .fillMaxHeight(0.4f)
                        }
                    },
                    label = R.string.try_again
                ) {
                    viewModel.updateTextGenerationResult("")
                    viewModel.updateRequestGeminiResponse(true)
                }

                UniversalButton(
                    modifier = when(systemLocale.language) {
                        "iw" -> {
                            modifier
                                .weight(1f)
                                .fillMaxHeight(0.4f)
                        }

                        "ru" -> {
                            modifier
                                .weight(1f)
                                .fillMaxHeight(0.4f)
                        }

                        else -> {
                            modifier
                                .weight(1f)
                                .fillMaxHeight(0.4f)
                        }
                    },
                    label = R.string.start_again
                ) {
                    onNavigateToHomeScreen()
                }
            }
        )
    }
}


private fun getAlertWindowData(t: Throwable?): Pair<Int, Int> {
    return when (t) {
        is ServerResponseException -> R.string.error_service_not_available_title to R.string.error_service_not_available
        else -> R.string.error_common_title to R.string.error_common_message
    }
}


