package com.schoolkiller

import android.app.Application
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.schoolkiller.data.Constants
import com.schoolkiller.domain.usecases.ads.BannerAdUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SchoolKillerApplication : Application(){

    @Inject
    lateinit var bannerAdUseCase: BannerAdUseCase


    override fun onCreate() {
        super.onCreate()
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@SchoolKillerApplication) {}
            MobileAds.setAppMuted(true)
        }

        bannerAdUseCase.loadAd(adUnitId = Constants.BANNER_AD_ID, adSize = AdSize.LARGE_BANNER)

    }
}


