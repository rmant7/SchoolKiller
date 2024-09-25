package com.schoolkiller.domain.usecases.adds

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.schoolkiller.view_model.SchoolKillerViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class InterstitialAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private var interstitialAd: InterstitialAd? = null

    fun loadAd(adUnitId: String, viewModel: SchoolKillerViewModel) = viewModel.viewModelScope.launch {
        interstitialAd = callbackFlow {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Timber.tag("InterstitialAd").d(adError.toString())
                        close(RuntimeException(adError.message)) // Close the flow with an error
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        Timber.tag("InterstitialAd").d("Ad was loaded.")
                        trySend(ad).isSuccess // Send the loaded ad to the flow
                        close() // Close the flow after sending the ad
                    }
                }
            )
            awaitClose { } // Suspend until the flow is closed
        }.firstOrNull() // Get the first (and only) value from the flow, or null if there's an error
    }



    fun showAd(activity: Activity) {
            if (interstitialAd != null) {
                interstitialAd?.show(activity)
                interstitialAd = null // Reset after showing
            } else {
                Timber.tag("InterstitialAd").d("The interstitial ad wasn't ready yet.")
            }
        }
    }

//    fun invoke(
//        adUnitId: String,
//        interstitialAd: InterstitialAd?
//    ){
//        val adRequest = AdRequest.Builder().build()
//        InterstitialAd.load(
//            context,
//            "ca-app-pub-3940256099942544/1033173712",
//            adRequest,
//            object : InterstitialAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Timber.tag("InterstitialAd").d(adError.toString())
//                    interstitialAd = null
//                }
//
//                override fun onAdLoaded(ad: InterstitialAd) {
//                    Timber.tag("InterstitialAd").d("Ad was loaded.")
//                    interstitialAd = ad
//                }
//            }
//        )


//    }
