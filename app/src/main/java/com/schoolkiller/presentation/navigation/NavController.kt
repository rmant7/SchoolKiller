package com.schoolkiller.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolkiller.data.Constants
import com.schoolkiller.presentation.screens.checking.CheckSolutionScreen
import com.schoolkiller.presentation.screens.home.HomeScreen
import com.schoolkiller.presentation.screens.info.ParametersScreen
import com.schoolkiller.presentation.screens.result.ResultScreen

@Composable
fun NavigationController() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {

        composable(route = Screens.HomeScreen.route) {
            HomeScreen(
                context = context,
                onNavigateToAdditionalInformationScreen = {
                    navController.navigate(Screens.ParametersScreen.route)
                },
                onNavigateToCheckSolutionOptionsScreen = {
                    navController.navigate(Screens.CheckSolutionInformationScreen.route)
                }
            )
        }

        composable(Screens.ParametersScreen.route) {
            ParametersScreen(
                context = context,
                onNavigateToResultScreen = {
                    navController.navigate(Screens.ResultScreen.route)
                }
            )
        }

        composable(Screens.CheckSolutionInformationScreen.route) {
            CheckSolutionScreen(
                context = context,
                onNavigateToResultScreen = {
                    navController.navigate(Screens.ResultScreen.route)
                }
            )
        }

        composable(Screens.ResultScreen.route) {
            ResultScreen(
                onNavigateToHomeScreen = {
                    navController.navigate(Screens.HomeScreen.route)
                }
            )
        }
    }
}

sealed class Screens(val route: String) {
    data object HomeScreen : Screens(Constants.HOME_SCREEN)
    data object ParametersScreen : Screens(Constants.PARAMETERS_SCREEN)
    data object ResultScreen : Screens(Constants.RESULT_SCREEN)
    data object CheckSolutionInformationScreen : Screens(Constants.CHECK_SOLUTION_INFORMATION_SCREEN)
}