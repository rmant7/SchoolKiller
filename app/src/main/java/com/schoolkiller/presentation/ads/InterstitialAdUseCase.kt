package com.schoolkiller.presentation.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.schoolkiller.data.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : AdUseCase(){

    private var interstitialAd: InterstitialAd? = null


    override fun load() {

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            Constants.INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    getOnFailedAction().invoke(adError)
                    Timber.d("Adds server is not available${adError.message}")
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
            }
        )
    }


    fun show(context: Context) {
        if(interstitialAd == null) return

        interstitialAd!!.show(context as Activity)

        interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadAdWithNoAdsCheck()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }
        }

    }

}


