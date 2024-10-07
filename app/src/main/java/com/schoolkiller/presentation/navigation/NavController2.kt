package com.schoolkiller.presentation.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.schoolkiller.presentation.screens.checking.CheckSolutionScreen
import com.schoolkiller.presentation.screens.home.HomeScreen
import com.schoolkiller.presentation.screens.home.HomeViewModel
import com.schoolkiller.presentation.screens.info.ParametersScreen
import com.schoolkiller.presentation.screens.result.ResultScreen
import kotlinx.serialization.Serializable


//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//@Composable
//fun NavigationController() {
//    val navController = rememberNavController()
//    val context = LocalContext.current
//    val homeViewModel: HomeViewModel = hiltViewModel()
//    val listOfImages = remember { mutableStateListOf<Uri>() }
//
//
//    NavHost(
//        navController = navController,
//        startDestination = Screens.HomeScreen
//    ) {
//        composable<Screens.HomeScreen> {
//            HomeScreen(
//                listOfImages = listOfImages,
//                onNavigateToParametersScreen = { selectedImageUri ->
//                    navController.navigate(
//                        Screens.ParametersScreen(selectedImageUri.toString())
//                    )
//                },
//                onNavigateToCheckSolutionOptionsScreen = { selectedImageUri ->
//                    navController.navigate(
//                        Screens.CheckSolutionInformationScreen(
//                            selectedImageUri = selectedImageUri.toString()
//                        )
//                    )
//                }
//            )
//        }
//
//        composable<Screens.ParametersScreen> {
//            val args = it.toRoute<Screens.ParametersScreen>()
//            ParametersScreen(
//                context = context,
//                //selectedImageUri = args.selectedImageUri,
//                onNavigateToResultScreen = { originalPrompt ->
//                    navController.navigate(
//                        Screens.ResultScreen(
//                            originalPrompt = originalPrompt,
//                            selectedImageUri = args.selectedImageUri
//                        )
//                    )
//                }
//            )
//        }
//
//        composable<Screens.CheckSolutionInformationScreen> {
//            val args = it.toRoute<Screens.CheckSolutionInformationScreen>()
//            CheckSolutionScreen(
//                context = context,
//               // selectedImageUri = args.selectedImageUri,
//                onNavigateToResultScreen = { originalPrompt ->
//                    navController.navigate(
//                        Screens.ResultScreen(
//                            originalPrompt = originalPrompt,
//                            selectedImageUri = args.selectedImageUri
//                        )
//                    )
//                }
//            )
//        }
//
//        composable<Screens.ResultScreen> {
//            val args = it.toRoute<Screens.ResultScreen>()
//
//            ResultScreen(
//                context = context,
//                onNavigateToHomeScreen = {
//                    navController.navigate(Screens.HomeScreen)
//                },
//                originalPrompt = args.originalPrompt,
//                selectedImageUri = args.selectedImageUri
//            )
//        }
//    }
//}
//
//
//@Serializable
//sealed class Screens {
//
//    @Serializable
//    data object HomeScreen: Screens()
//
//    @Serializable
//    data class ParametersScreen(
//        val selectedImageUri: String
//    ) : Screens()
//
//    @Serializable
//    data class ResultScreen(
//        val originalPrompt: String,
//        val selectedImageUri: String
//    ) : Screens()
//
//    @Serializable
//    data class CheckSolutionInformationScreen(
//        val selectedImageUri: String
//    ) : Screens()
//}