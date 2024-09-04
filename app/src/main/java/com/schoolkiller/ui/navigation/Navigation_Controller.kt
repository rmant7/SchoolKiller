package com.schoolkiller.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolkiller.ui.screens.InstructionsScreen
import com.schoolkiller.ui.screens.UploadFilesScreen

@Composable
fun NavigationController(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screens.InstructionsScreen.route) {

        composable(Screens.InstructionsScreen.route) {
            InstructionsScreen(
                context = context,
                onNavigateToNextPage = {
                    navController.navigate(Screens.UploadFilesScreen.route)
                }
            )
        }

        composable(route = Screens.UploadFilesScreen.route) {
            UploadFilesScreen(context = context)
        }
    }
}

sealed class Screens(val route: String) {
    data object InstructionsScreen : Screens("Instructions_Screen")
    data object UploadFilesScreen : Screens("Upload_Files_Screen")
}