package com.schoolkiller

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.schoolkiller.domain.usecases.ads.AdUseCase
import com.schoolkiller.domain.usecases.ads.BannerAdUseCase
import com.schoolkiller.domain.usecases.ads.InterstitialAdUseCase
import com.schoolkiller.domain.usecases.ads.OpenAdUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class SchoolKillerApplication : Application() {

    @Inject
    lateinit var openAppAdUseCase: OpenAdUseCase

    @Inject
    lateinit var bannerAdUseCase: BannerAdUseCase

    @Inject
    lateinit var interstitialAdUseCase: InterstitialAdUseCase

    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@SchoolKillerApplication) {}
            MobileAds.setAppMuted(true)
        }

        // apply reload every 5 seconds on fail to load

        openAppAdUseCase.apply {
            setOnFailedAction { onReload(this) }
        }

        interstitialAdUseCase.apply {
            setOnFailedAction { onReload(this) }
        }

        bannerAdUseCase.apply {
            setOnFailedAction { onReload(this) }
        }

        // preloading ads
        openAppAdUseCase.loadAdWithNoAdsCheck()
        bannerAdUseCase.loadAdWithNoAdsCheck()
        interstitialAdUseCase.loadAdWithNoAdsCheck()

    }

    // all ads loading must be in main thread
    private fun onReload(adUseCase: AdUseCase) = mainScope.launch(Dispatchers.Main) {
        delay(5000)
        adUseCase.loadAdWithNoAdsCheck()
    }
}


