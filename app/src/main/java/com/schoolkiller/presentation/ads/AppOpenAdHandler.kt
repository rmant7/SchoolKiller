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

@Composable
fun AppOpenAdHandler(
    context: Context,
    viewModel: HomeViewModel,
) {

    val cooldownInMillis = Constants.OPEN_AD_COOLDOWN

    val appOpenAd = viewModel.appOpenAd.collectAsState().value
    val openAdLoadTime = viewModel.openAdLoadTime
    val openAdLastAdShownTime = viewModel.openAdLastAdShownTime


    // Show the ad when it's available, not expired, and cooldown has passed
    LaunchedEffect(key1 = appOpenAd) {
        appOpenAd?.let { ad ->

            val timeSinceLoad = System.currentTimeMillis() - openAdLoadTime.value
            val timeSinceLastShown = System.currentTimeMillis() - openAdLastAdShownTime.value

            val expirationCheck = timeSinceLoad <= 4 * 60 * 60 * 1000 // 4 hour expiration check
            val cooldownCheck = timeSinceLastShown >= cooldownInMillis

            if (expirationCheck && cooldownCheck) {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        viewModel.updateAppOpenAd(null)
                        if (viewModel.appOpenAd.value == null) {
                            viewModel.loadOpenAd()
                        }

                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        viewModel.updateAppOpenAd(null)
                        if (viewModel.appOpenAd.value == null) {
                            viewModel.loadOpenAd()
                        }
                    }

                    override fun onAdShowedFullScreenContent() {
                        viewModel.updateOpenAdLastAdShownTime(System.currentTimeMillis())
                    }
                }
                ad.show(context as Activity)
            } else {
                // Ad expired or cooldown not yet passed, load a new one
                viewModel.updateAppOpenAd(null)
                if (viewModel.appOpenAd.value == null) {
                    viewModel.loadOpenAd()
                }
            }
        }
    }
}