package com.schoolkiller.presentation.navigation

import androidx.compose.runtime.Composable
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

    NavHost(navController = navController, startDestination = Screens.HomeScreen) {
        composable<Screens.HomeScreen> {
            HomeScreen(
                context = context,
                onNavigateToAdditionalInformationScreen = {
                    navController.navigate(Screens.ParametersScreen)
                },
                onNavigateToCheckSolutionOptionsScreen = {
                    navController.navigate(Screens.CheckSolutionInformationScreen)
                }
            )
        }

        composable<Screens.ParametersScreen> {
            ParametersScreen(
                context = context,
                onNavigateToResultScreen = { originalPrompt ->
                    navController.navigate(
                        Screens.ResultScreen(
                            originalPrompt = originalPrompt
                        )
                    )
                }
            )
        }

        composable<Screens.CheckSolutionInformationScreen> {
            CheckSolutionScreen(
                context = context,
                onNavigateToResultScreen = {
                    navController.navigate(Screens.ResultScreen)
                }
            )
        }

        composable<Screens.ResultScreen> {
            val args = it.toRoute<Screens.ResultScreen>()

            ResultScreen(
                onNavigateToHomeScreen = {
                    navController.navigate(Screens.HomeScreen)
                },
                originalPrompt = args.originalPrompt
            )
        }
    }
}

@Serializable
sealed class Screens {
    @Serializable
    data object HomeScreen : Screens()

    @Serializable
    data object ParametersScreen : Screens()

    @Serializable
    data class ResultScreen(
        val originalPrompt: String
    ) : Screens()

    @Serializable
    data object CheckSolutionInformationScreen : Screens()
}