package com.schoolkiller.presentation.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.schoolkiller.presentation.screens.result.ResultViewModel

@Composable
fun InterstitialAdPresenter(
    context: Context,
    interstitialAd: InterstitialAd,
    viewModel: ResultViewModel,
    showAd: Boolean
) {

    // Observe the showAd state and trigger ad display when it becomes true
    LaunchedEffect(key1 = showAd) {
        if (showAd) {
            interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    viewModel.updateInterstitialAd(null) // Reset after dismissed
                    // moved ad load to this function
                    viewModel.loadInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    viewModel.updateInterstitialAd(null) // Reset after failing to show
                }

                override fun onAdShowedFullScreenContent() {
                    viewModel.updateInterstitialAd(null) // Reset after showing
                }
            }

            interstitialAd.show(context as Activity)
        }
    }
}