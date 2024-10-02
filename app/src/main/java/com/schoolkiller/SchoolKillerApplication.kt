package com.schoolkiller

import android.app.Application
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.schoolkiller.domain.usecases.ads.BannerAdUseCase
import com.schoolkiller.domain.usecases.ads.InterstitialAdUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SchoolKillerApplication : Application() {

    @Inject
    lateinit var bannerAdUseCase: BannerAdUseCase

    @Inject
    lateinit var interstitialAdUseCase: InterstitialAdUseCase


    override fun onCreate() {
        super.onCreate()
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@SchoolKillerApplication) {}
            MobileAds.setAppMuted(true)
        }

        // preloading ads
        bannerAdUseCase.loadBannerAd(adSize = AdSize.MEDIUM_RECTANGLE)
        interstitialAdUseCase.loadAd()

    }
}


