package com.schoolkiller.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolkiller.ui.screens.InstructionsScreen
import com.schoolkiller.ui.screens.UploadFilesScreen
import com.schoolkiller.view_model.SchoolKillerViewModel

@Composable
fun NavigationController(
    modifier: Modifier = Modifier,
    viewModel: SchoolKillerViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screens.InstructionsScreen.route) {

        composable(Screens.InstructionsScreen.route) {
            InstructionsScreen(
                context = context,
                viewModel = viewModel,
                onNavigateToNextPage = {
                    navController.navigate(Screens.UploadFilesScreen.route)
                }
            )
        }

        composable(route = Screens.UploadFilesScreen.route) {
            UploadFilesScreen(context = context, viewModel = viewModel)
        }
    }
}

sealed class Screens(val route: String) {
    data object InstructionsScreen : Screens("Instructions_Screen")
    data object UploadFilesScreen : Screens("Upload_Files_Screen")
}