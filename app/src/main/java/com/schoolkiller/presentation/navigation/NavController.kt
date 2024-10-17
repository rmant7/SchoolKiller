package com.schoolkiller.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.schoolkiller.presentation.screens.checking.CheckSolutionScreen
import com.schoolkiller.presentation.screens.home.HomeScreen
import com.schoolkiller.presentation.screens.info.ParametersScreen
import com.schoolkiller.presentation.screens.ocr.OcrScreen
import com.schoolkiller.presentation.screens.result.ResultScreen
import kotlinx.serialization.Serializable


@Composable
fun NavigationController(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen
    ) {
        composable<Screens.HomeScreen> {
            HomeScreen(
                onNavigateToOcrScreen = { uri ->
                    navController.navigate(Screens.OcrScreen(uri.toString()))
                }
            )
        }

        composable<Screens.OcrScreen> {
            val args = it.toRoute<Screens.OcrScreen>()
            OcrScreen(
                passedImageUri = Uri.parse(args.selectedImageUri),
                onNavigateToParametersScreen = { recognizedText ->
                    navController.navigate(
                        Screens.ParametersScreen(recognizedText)
                    )
                },
                onNavigateToCheckSolutionOptionsScreen = { recognizedText ->
                    navController.navigate(
                        Screens.CheckSolutionInformationScreen(recognizedText)
                    )
                },
                onNavigateToHomeScreen = { navController.navigate(Screens.HomeScreen) }
            )
        }


        composable<Screens.ParametersScreen> {

            val args = it.toRoute<Screens.ParametersScreen>()
            ParametersScreen(
                recognizedText = args.recognizedText,
                onNavigateToResultScreen = { prompt: String, systemInstruction: String ->
                    navController.navigate(
                        Screens.ResultScreen(prompt, systemInstruction)
                    )
                }
            )
        }

        composable<Screens.CheckSolutionInformationScreen> {

            val args = it.toRoute<Screens.CheckSolutionInformationScreen>()
            CheckSolutionScreen(
                recognizedText = args.recognizedText,
                onNavigateToResultScreen = { prompt: String, systemInstruction: String ->
                    navController.navigate(
                        Screens.ResultScreen(prompt, systemInstruction)
                    )
                }
            )
        }

        composable<Screens.ResultScreen> {
            val args = it.toRoute<Screens.ResultScreen>()
            ResultScreen(
                // passedImageUri = resultProperties.passedImageUri,
                passedPrompt = args.prompt,
                passedSystemInstruction = args.systemInstruction,
                onNavigateToHomeScreen = {
                    // Navigate to Home screen and clear back stack
                    // so that user can't navigate back to Result screen there
                    navController.popBackStack(Screens.HomeScreen, false)
                }
            )
        }
    }
}


@Serializable
sealed class Screens {

    @Serializable
    data object HomeScreen : Screens()

    @Serializable
    data class OcrScreen(
        val selectedImageUri: String // List<String>
    ) : Screens()

    @Serializable
    data class ParametersScreen(
        val recognizedText: String
    ) : Screens()

    @Serializable
    data class ResultScreen(
        val prompt: String,
        val systemInstruction: String
    ) : Screens()

    @Serializable
    data class CheckSolutionInformationScreen(
        val recognizedText: String
    ) : Screens()
}