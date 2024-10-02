package com.schoolkiller.domain.usecases.ads

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.schoolkiller.data.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) : AdUseCase{

    private var adView: AdView? = null

    fun loadBannerAd(adSize: AdSize) {
        shouldShowAdsCheck()

        if (adView == null) {
            adView = AdView(context).apply {
                this.adUnitId = Constants.BANNER_AD_ID
                this.setAdSize(adSize)
            }
        }

        adView?.apply {
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


