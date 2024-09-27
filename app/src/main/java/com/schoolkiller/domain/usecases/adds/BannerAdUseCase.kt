package com.schoolkiller.domain.usecases.adds

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.schoolkiller.presentation.screens.checking.SolutionCheckingViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BannerAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var adView: AdView? = null

    fun loadAd(
        adUnitId: String,
        viewModel: SolutionCheckingViewModel,
        adSize: AdSize) {
        if (adView == null) {
            adView = AdView(context)
        }

        adView?.apply {
            this.adUnitId = adUnitId
            this.setAdSize(adSize)
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    viewModel.loadBannerAd()
                }

                override fun onAdLoaded() { }
            }
        }

        adView?.let {
            it.loadAd(AdRequest.Builder().build())
        }

        viewModel.updateAdview(adView)
    }

//    fun getBannerAd(): AdView? {
//        return adView
//    }
}


