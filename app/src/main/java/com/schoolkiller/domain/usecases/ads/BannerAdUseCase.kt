package com.schoolkiller.domain.usecases.ads

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var adView: AdView? = null

    fun loadAd(
        adUnitId: String,
        adSize: AdSize
    ) {
        if (adView == null) {
            adView = AdView(context)
        }

        adView?.apply {
            this.adUnitId = adUnitId
            this.setAdSize(adSize)
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.d("Adds server is not available${adError.message}")
                }

                override fun onAdLoaded() {
                    Timber.d("Ad loaded successfully")
                }
            }
        }

        adView?.loadAd(AdRequest.Builder().build())

    }

    fun getBannerAdView(): AdView? {
        return adView
    }

}


