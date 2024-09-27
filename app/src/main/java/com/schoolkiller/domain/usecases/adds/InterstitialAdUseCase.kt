package com.schoolkiller.domain.usecases.adds

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.schoolkiller.presentation.screens.result.ResultViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class InterstitialAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private var interstitialAd: InterstitialAd? = null

    fun loadAd(adUnitId: String, viewModel: ResultViewModel) = viewModel.viewModelScope.launch {
        interstitialAd = callbackFlow {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        close(RuntimeException(adError.message)) // Close the flow with an error
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        trySend(ad).isSuccess // Send the loaded ad to the flow
                        close() // Close the flow after sending the ad
                    }
                }
            )
            awaitClose { } // Suspend until the flow is closed
        }.firstOrNull() // Get the first (and only) value from the flow, or null if there's an error

        viewModel.updateInterstitialAd(interstitialAd)
    }





//    fun showAd(activity: Activity, adUnitId: String, viewModel: SchoolKillerViewModel) {
//            if (interstitialAd != null) {
//                interstitialAd?.show(activity)
//                interstitialAd = null
//                // Reload the ad after showing it
//                viewModel.viewModelScope.launch {
//                    loadAd(adUnitId, viewModel)
//                }
//            } else {
//                Timber.tag("InterstitialAd").d("The interstitial ad wasn't ready yet.")
//            }
//        }
    }

