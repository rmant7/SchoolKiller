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
) : AdUseCase() {

    private var stretchedBanner: AdView? = null

    private var mediumBanner: AdView? = AdView(context).apply {
        this.adUnitId = Constants.BANNER_AD_SAMPLE_ID
        this.setAdSize(AdSize.MEDIUM_RECTANGLE)
    }

    override fun load() {

        if (stretchedBanner == null) {
            val adSize = AdSize(AdSize.FULL_WIDTH, 650)

           /* val adaptiveSize = AdSize.getInlineAdaptiveBannerAdSize(
                AdSize.FULL_WIDTH,
                (maxScreenHeight * 0.2).roundToInt()
            )*/

            stretchedBanner = AdView(context).apply {
                this.adUnitId = Constants.BANNER_AD_SAMPLE_ID
                this.setAdSize(adSize) // AdSize.MEDIUM_RECTANGLE
            }
        }

        loadBanner(stretchedBanner)
        loadBanner(mediumBanner)
    }

    private fun loadBanner(
        adView: AdView?
    ) {

        adView?.apply {
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    getOnFailedAction().invoke(adError)
                    Timber.d("Adds server is not available${adError.message}")
                }

                override fun onAdLoaded() {
                    Timber.d("Ad loaded successfully")
                }
            }
        }

        adView?.loadAd(AdRequest.Builder().build())
    }

    fun getStretchedBannerAdView(): AdView? {
        return stretchedBanner
    }

    fun getMediumBannerAdView(): AdView? {
        return mediumBanner
    }

}


