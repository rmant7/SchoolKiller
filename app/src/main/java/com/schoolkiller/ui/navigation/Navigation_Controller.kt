package com.schoolkiller.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolkiller.ui.screens.HomeScreen
import com.schoolkiller.ui.screens.ResultScreen
import com.schoolkiller.utils.Constants
import com.schoolkiller.view_model.SchoolKillerViewModel

@Composable
fun NavigationController(
    modifier: Modifier = Modifier,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {

        composable(route = Screens.HomeScreen.route) {
            HomeScreen(
                context = context,
                viewModel = viewModel,
                onNavigateToResultScreen = {  navController.navigate(Screens.ResultScreen.route) },
//                onNavigateToGeminiAnswerScreen = { navController.navigate(Screens.GeminiAnswerScreen) }
            )
        }

        composable(Screens.ResultScreen.route) {
            ResultScreen(
                context = context,
                viewModel = viewModel,
                onNavigateToNextPage = {
                    navController.navigate(Screens.HomeScreen.route)
                }
            )
        }

//        composable(Screens.GeminiAnswerScreen.route){
//            GeminiAnswerScreen(
//                context = context,
//                viewModel = viewModel
//            )
//        }

    }
}

sealed class Screens(val route: String) {
    data object ResultScreen : Screens(Constants.RESULT_SCREEN)
    data object HomeScreen : Screens(Constants.HOME_SCREEN)
//    data object GeminiAnswerScreen : Screens(Constants.GEMINI_ANSWER_SCREEN)
}