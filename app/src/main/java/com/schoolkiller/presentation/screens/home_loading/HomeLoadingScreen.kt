package com.schoolkiller.presentation.screens.home_loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schoolkiller.presentation.common.ApplicationScaffold

@Composable
fun HomeLoadingScreen(
    //onNavigateToHomeScreen: (AppOpenAd?) -> Unit
    onNavigateToHomeScreen: () -> Unit
) {

    val viewModel: HomeLoadingViewModel = hiltViewModel()
    val isTimeOut = viewModel.isTimeOut.collectAsState()

    if (isTimeOut.value) {
        println("IT'S TIME OUT, NAVIGATE TO OTHER SCREEN")
        viewModel.updateTimeOut(false)
        onNavigateToHomeScreen() //viewModel.appOpenAd -> for args
    }
        ApplicationScaffold(
            columnHorizontalAlignment = Alignment.CenterHorizontally,
            columnVerticalArrangement = Arrangement.Center,
            content = {

                Text(
                    "Loading, please, wait...",
                    fontSize = 30.sp
                )

                Spacer(Modifier.padding(0.dp, 15.dp))

                LinearProgressIndicator(
                    Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                )
                /*LinearProgressIndicator(
                    progress = { loadingProgress.value.toFloat() / 6 },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(30.dp),
                )*/
            })
}