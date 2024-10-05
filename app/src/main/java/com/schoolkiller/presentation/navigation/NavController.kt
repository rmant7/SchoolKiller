package com.schoolkiller.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.schoolkiller.presentation.screens.checking.CheckSolutionScreen
import com.schoolkiller.presentation.screens.home.HomeScreen
import com.schoolkiller.presentation.screens.home.HomeViewModel
import com.schoolkiller.presentation.screens.info.ParametersScreen
import com.schoolkiller.presentation.screens.result.ResultScreen
import com.schoolkiller.presentation.screens.result.ResultViewModel
import kotlinx.serialization.Serializable


@Composable
fun NavigationController() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val resultViewModel: ResultViewModel = hiltViewModel()
    val imageUri = resultViewModel.passedImageUri.collectAsState().value



    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen
    ) {
        composable<Screens.HomeScreen> {
            HomeScreen(
                onNavigateToParametersScreen = {
                    navController.navigate(Screens.ParametersScreen)
                },
                onNavigateToCheckSolutionOptionsScreen = {
                    navController.navigate(Screens.CheckSolutionInformationScreen)
                }
            )
        }

        composable<Screens.ParametersScreen> {
            ParametersScreen(
                onNavigateToResultScreen = { originalPrompt ->
                    navController.navigate(
                        Screens.ResultScreen(originalPrompt = originalPrompt)
                    )
                }
            )
        }

        composable<Screens.CheckSolutionInformationScreen> {
            CheckSolutionScreen(
                onNavigateToResultScreen = { originalPrompt ->
                    navController.navigate(
                        Screens.ResultScreen(originalPrompt = originalPrompt)
                    )
                }
            )
        }

        composable<Screens.ResultScreen> {
            val args = it.toRoute<Screens.ResultScreen>()

            ResultScreen(
                originalPrompt = args.originalPrompt,
                passedImageUri = imageUri,
                onNavigateToHomeScreen = {
                    navController.navigate(Screens.HomeScreen)
                }
            )
        }
    }
}


@Serializable
sealed class Screens {

    @Serializable
    data object HomeScreen: Screens()

    @Serializable
    data object ParametersScreen: Screens()

    @Serializable
    data class ResultScreen(
        val originalPrompt: String
    ) : Screens()

    @Serializable
    data object CheckSolutionInformationScreen : Screens()
}