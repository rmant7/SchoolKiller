package com.schoolkiller.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolkiller.data.Constants
import com.schoolkiller.presentation.ui.ads.AppOpenAdHandler
import com.schoolkiller.presentation.ui.screens.AdditionalInformationScreen
import com.schoolkiller.presentation.ui.screens.CheckSolutionOptionsScreen
import com.schoolkiller.presentation.ui.screens.HomeScreen
import com.schoolkiller.presentation.ui.screens.ResultScreen
import com.schoolkiller.presentation.view_model.SchoolKillerViewModel

@Composable
fun NavigationController(
    modifier: Modifier = Modifier,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current



    NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {

        composable(route = Screens.HomeScreen.route) {

            // App Open Ad
            AppOpenAdHandler(
                context = context,
                viewModel = viewModel,
            )

            HomeScreen(
                context = context,
                lifecycleOwner = lifecycleOwner,
                viewModel = viewModel,
                onNavigateToAdditionalInformationScreen = {
                    navController.navigate(Screens.AdditionalInformationScreen.route)
                },
                onNavigateToCheckSolutionOptionsScreen = {
                    navController.navigate(Screens.CheckSolutionInformationScreen.route)
                }
            )
        }

        composable(Screens.AdditionalInformationScreen.route) {
            AdditionalInformationScreen(
                context = context,
                viewModel = viewModel,
                onNavigateToResultScreen = {
                    navController.navigate(Screens.ResultScreen.route)
                }
            )
        }

        composable(Screens.CheckSolutionInformationScreen.route) {
            CheckSolutionOptionsScreen(
                context = context,
                viewModel = viewModel,
                onNavigateToResultScreen = {
                    navController.navigate(Screens.ResultScreen.route)
                }
            )
        }

        composable(Screens.ResultScreen.route) {
            ResultScreen(
                context = context,
                viewModel = viewModel,
                onNavigateToHomeScreen = {
                    navController.navigate(Screens.HomeScreen.route)
                }
            )
        }

    }
}

sealed class Screens(val route: String) {
    data object HomeScreen : Screens(Constants.HOME_SCREEN)
    data object AdditionalInformationScreen : Screens(Constants.ADDITIONAL_INFORMATION_SCREEN)
    data object ResultScreen : Screens(Constants.RESULT_SCREEN)
    data object CheckSolutionInformationScreen :
        Screens(Constants.CHECK_SOLUTION_INFORMATION_SCREEN)
}