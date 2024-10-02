package com.schoolkiller.presentation.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.schoolkiller.data.Constants
import com.schoolkiller.presentation.screens.home.HomeViewModel
import com.schoolkiller.presentation.screens.home_loading.HomeLoadingViewModel

/*@Deprecated("Replaced with OpenAdUseCase")
@Composable
fun AppOpenAdHandler(
    context: Context,
    viewModel: HomeViewModel,
) {

    val appOpenAd = viewModel.appOpenAd ?: return

    appOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            viewModel.updateAppOpenAd(null)
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            viewModel.updateAppOpenAd(null)
        }

        override fun onAdShowedFullScreenContent() {
            viewModel.updateAppOpenAd(null)
            //viewModel.updateOpenAdLastAdShownTime(System.currentTimeMillis())
        }
    }
    appOpenAd.show(context as Activity)

}*/