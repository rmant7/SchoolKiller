package com.schoolkiller.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolkiller.presentation.screens.checking.CheckSolutionScreen
import com.schoolkiller.presentation.screens.home.HomeScreen
import com.schoolkiller.presentation.screens.info.ParametersScreen
import com.schoolkiller.presentation.screens.info.ParametersViewModel
import com.schoolkiller.presentation.screens.result.ResultScreen
import com.schoolkiller.presentation.screens.result.ResultViewModel
import kotlinx.serialization.Serializable


@Composable
fun NavigationController() {
    val navController = rememberNavController()
//    val homeViewModel: HomeViewModel = hiltViewModel()
//    val homeProperties = homeViewModel.homePropertiesState.collectAsStateWithLifecycle().value
//    val solutionViewModel: SolutionCheckingViewModel = hiltViewModel()
//    val solutionProperties = solutionViewModel.solutionPropertiesState.collectAsStateWithLifecycle().value
    val parametersViewModel: ParametersViewModel = hiltViewModel()
//    val parametersProperties = parametersViewModel.parametersPropertiesState.collectAsStateWithLifecycle().value
    val resultViewModel: ResultViewModel = hiltViewModel()
    val resultProperties = resultViewModel.resultPropertiesState.collectAsStateWithLifecycle().value
    val prompt =
        if (resultProperties.isSolveActionRequested) resultProperties.passedConvertedSolvePrompt
        else resultProperties.passedConvertedSolutionPrompt

    //systemInstruction should be in ResultProperties instead
    val parametersProps = parametersViewModel
        .parametersPropertiesState.collectAsStateWithLifecycle().value
    val systemInstruction =
        if (resultProperties.isSolveActionRequested)
           "Answer only in ${parametersProps.language}."
        else
            "Answer only in language identified on the picture."


    /** Maybe the best place to init the ads. here would be initialized before user goes to the screen
     * and will kept active. I have ready all view models instances for testing */


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
            resultViewModel.updateIsSolveActionRequested(true)
            ParametersScreen(
                viewModel = parametersViewModel,
                onNavigateToResultScreen = { navController.navigate(Screens.ResultScreen) }
            )
        }

        composable<Screens.CheckSolutionInformationScreen> {
            resultViewModel.updateIsSolveActionRequested(false)
            CheckSolutionScreen(
                onNavigateToResultScreen = { navController.navigate(Screens.ResultScreen) }
            )
        }

        composable<Screens.ResultScreen> {
            ResultScreen(
                passedPrompt = prompt,
                passedImageUri = resultProperties.passedImageUri,
                passedSystemInstruction = systemInstruction,
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
    data object HomeScreen : Screens()

    @Serializable
    data object ParametersScreen : Screens()

    @Serializable
    data object ResultScreen : Screens()

    @Serializable
    data object CheckSolutionInformationScreen : Screens()
}