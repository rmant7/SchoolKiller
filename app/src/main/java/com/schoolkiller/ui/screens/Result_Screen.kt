package com.schoolkiller.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.schoolkiller.R
import com.schoolkiller.ui.reusable_components.AlertDialog
import com.schoolkiller.ui.reusable_components.ApplicationScaffold
import com.schoolkiller.ui.reusable_components.UniversalButton
import com.schoolkiller.view_model.SchoolKillerViewModel


@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchoolKillerViewModel,
    onNavigateToHomeScreen: () -> Unit,
) {

    val resultText = viewModel.textGenerationResult.collectAsState()
    val resultError = viewModel.error.collectAsState()
    val image = viewModel.selectedUri.collectAsState()
    val prompt = viewModel.originalPrompt.collectAsState()
    val state = rememberLazyListState()
    var tryAgain by remember { mutableStateOf(true) }

    val openAlertDialog = remember { mutableStateOf(resultError.value != null) }

    LaunchedEffect(tryAgain) {
        image.value?.let {
            viewModel.fetchGeminiResponse(
                imageUri = it,
                fileName = "${image.value}",
                prompt = prompt.value
            )
        }
        tryAgain = false
    }

    ApplicationScaffold {
        if (resultError.value != null) {
            openAlertDialog.value = true
            AlertDialog(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    viewModel.clearError()
                    onNavigateToHomeScreen()
                },
                dialogTitle = stringResource(R.string.error_service_not_available_title),
                dialogText = stringResource(R.string.error_service_not_available),
                icon = Icons.Default.Info
            )
        }

        LazyColumn(
            modifier = modifier
                .fillMaxHeight(0.75f),
            state = state,
            content = {

                //Don't remove, solution image needs fix.
                /*
                item {
                    image.value?.let {
                        SolutionImage(
                            image = it,
                            context = context,
                            contentDescription = resultText.value
                        )
                    }
                }
                */

                item { Spacer(modifier.height(16.dp)) }


                item {
                    if (resultText.value.isBlank() && resultError.value == null) {
                        Box(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center,
                            content = {
                                CircularProgressIndicator(modifier = modifier.size(80.dp))
                            }
                        )
                    }
                }
            }
        )


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {

                UniversalButton(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    label = R.string.try_again
                ) {
                    viewModel.updateTextGenerationResult("")
                    tryAgain = true
                }

                UniversalButton(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    label = R.string.start_again
                ) {
                    onNavigateToHomeScreen()
                }
            }
        )
    }

}

