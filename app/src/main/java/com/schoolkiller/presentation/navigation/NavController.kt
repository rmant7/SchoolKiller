package com.schoolkiller.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.schoolkiller.presentation.screens.checking.CheckSolutionScreen
import com.schoolkiller.presentation.screens.home.HomeScreen
import com.schoolkiller.presentation.screens.info.ParametersScreen
import com.schoolkiller.presentation.screens.result.ResultScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationController() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val listOfImages = remember { mutableStateListOf<Uri>() }

    NavHost(navController = navController, startDestination = Screens.HomeScreen) {
        composable<Screens.HomeScreen> {
            HomeScreen(
                context = context,
                listOfImages = listOfImages,
                onNavigateToParametersScreen = { selectedImageUri ->
                    navController.navigate(
                        Screens.ParametersScreen(
                            selectedImageUri = selectedImageUri.toString()
                        )
                    )
                },
                onNavigateToCheckSolutionOptionsScreen = { selectedImageUri ->
                    navController.navigate(
                        Screens.CheckSolutionInformationScreen(
                            selectedImageUri = selectedImageUri.toString()
                        )
                    )
                }
            )
        }

        composable<Screens.ParametersScreen> {
            val args = it.toRoute<Screens.ParametersScreen>()
            ParametersScreen(
                context = context,
                onNavigateToResultScreen = { originalPrompt ->
                    navController.navigate(
                        Screens.ResultScreen(
                            originalPrompt = originalPrompt,
                            selectedImageUri = args.selectedImageUri
                        )
                    )
                }
            )
        }

        composable<Screens.CheckSolutionInformationScreen> {
            val args = it.toRoute<Screens.CheckSolutionInformationScreen>()
            CheckSolutionScreen(
                context = context,
                onNavigateToResultScreen = { originalPrompt ->
                    navController.navigate(
                        Screens.ResultScreen(
                            originalPrompt = originalPrompt,
                            selectedImageUri = args.selectedImageUri
                        )
                    )
                }
            )
        }

        composable<Screens.ResultScreen> {
            val args = it.toRoute<Screens.ResultScreen>()

            ResultScreen(
                onNavigateToHomeScreen = {
                    navController.navigate(Screens.HomeScreen)
                },
                originalPrompt = args.originalPrompt,
                selectedImageUri = args.selectedImageUri
            )
        }
    }
}

@Serializable
sealed class Screens {
    @Serializable
    data object HomeScreen : Screens()

    @Serializable
    data class ParametersScreen(
        val selectedImageUri: String
    ) : Screens()

    @Serializable
    data class ResultScreen(
        val originalPrompt: String,
        val selectedImageUri: String
    ) : Screens()

    @Serializable
    data class CheckSolutionInformationScreen(
        val selectedImageUri: String
    ) : Screens()
}