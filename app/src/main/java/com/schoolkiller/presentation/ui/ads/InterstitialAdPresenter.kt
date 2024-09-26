package com.schoolkiller.presentation.ui.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.schoolkiller.presentation.view_model.SchoolKillerViewModel

@Composable
fun InterstitialAdPresenter(
    context: Context,
    interstitialAd: InterstitialAd,
    viewModel: SchoolKillerViewModel,
    showAd: Boolean
) {

    // Observe the showAd state and trigger ad display when it becomes true
    LaunchedEffect(key1 = showAd) {
        if (showAd) {
            interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    viewModel.updateInterstitialAd(null) // Reset after dismissed
                    if (viewModel.interstitialAd.value == null){
                        viewModel.loadInterstitialAd()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    viewModel.updateInterstitialAd(null) // Reset after failing to show
                    if (viewModel.interstitialAd.value == null){
                        viewModel.loadInterstitialAd()
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    viewModel.updateInterstitialAd(null) // Reset after showing
                    if (viewModel.interstitialAd.value == null){
                        viewModel.loadInterstitialAd()
                    }
                }
            }
            interstitialAd.show(context as Activity)
        }
    }
}