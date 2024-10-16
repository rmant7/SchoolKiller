package com.schoolkiller.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.schoolkiller.presentation.screens.checking.CheckSolutionScreen
import com.schoolkiller.presentation.screens.home.HomeScreen
import com.schoolkiller.presentation.screens.info.ParametersScreen
import com.schoolkiller.presentation.screens.ocr.OcrScreen
import com.schoolkiller.presentation.screens.result.ResultScreen
import com.schoolkiller.presentation.screens.result.ResultViewModel
import kotlinx.serialization.Serializable


@Composable
fun NavigationController(navController: NavHostController) {

    //val navController = rememberNavController()

//    val homeViewModel: HomeViewModel = hiltViewModel()
//    val homeProperties = homeViewModel.homePropertiesState.collectAsStateWithLifecycle().value
//    val solutionViewModel: SolutionCheckingViewModel = hiltViewModel()
//    val solutionProperties = solutionViewModel.solutionPropertiesState.collectAsStateWithLifecycle().value
//    val parametersProperties = parametersViewModel.parametersPropertiesState.collectAsStateWithLifecycle().value

    /*val resultViewModel: ResultViewModel = hiltViewModel()
    val resultProperties = resultViewModel.resultPropertiesState.collectAsStateWithLifecycle().value

   val prompt =
        if (resultProperties.isSolveActionRequested)
            resultProperties.passedConvertedSolvePrompt //+ " The task is: $recognizedText"
        else
            resultProperties.passedConvertedSolutionPrompt //+ " User's solution is: $recognizedText"
        */


    /** Maybe the best place to init the ads. here would be initialized before user goes to the screen
     * and will kept active. I have ready all view models instances for testing */

    /**
     * We can try it. In the past I had the same idea and asked Gleb if it's a good practise.
     * He said architecture wise it's better not to, but if it works we can add it.
     */

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
            //resultViewModel.updateIsSolveActionRequested(true)
            /** If it's not inconveniencing you, it's easier for me to work with
             * passed arguments than check through if-else or switch-case
             * */
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
            //resultViewModel.updateIsSolveActionRequested(false)
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
        val selectedImageUri: String //List<String>
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