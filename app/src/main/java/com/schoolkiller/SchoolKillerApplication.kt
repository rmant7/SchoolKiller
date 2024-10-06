package com.schoolkiller

import android.app.Application
import com.google.ai.client.generativeai.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.schoolkiller.domain.usecases.ads.BannerAdUseCase
import com.schoolkiller.domain.usecases.ads.InterstitialAdUseCase
import com.schoolkiller.domain.usecases.ads.OpenAdUseCase
import com.schoolkiller.presentation.toast.ShowToastMessage
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SchoolKillerApplication : Application(){

    @Inject
    lateinit var openAppAdUseCase: OpenAdUseCase

    @Inject
    lateinit var bannerAdUseCase: BannerAdUseCase

    @Inject
    lateinit var interstitialAdUseCase: InterstitialAdUseCase


    private val adsScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        // Take out logs of the release version with this set. logs decrease performance
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // initialize early the context of showToast function
        ShowToastMessage.init(this@SchoolKillerApplication)


        adsScope.launch(Dispatchers.IO + SupervisorJob()) {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@SchoolKillerApplication) {}
            MobileAds.setAppMuted(true)
        }

        // apply reload every 5 seconds on fail to load

//        openAppAdUseCase.apply {
//            setOnFailedAction { onReload(this) }
//        }
//
//
//        interstitialAdUseCase.apply {
//            setOnFailedAction { onReload(this) }
//        }
//
//        bannerAdUseCase.apply {
//            setOnFailedAction { onReload(this) }
//        }

        // preloading ads
        openAppAdUseCase.loadAdWithNoAdsCheck()
        bannerAdUseCase.loadAdWithNoAdsCheck()
        interstitialAdUseCase.loadAdWithNoAdsCheck()

    }


    // all ads loading must be in main thread
    /** no don`t do that, ads are fetched as data and blocking the UI with such a heavy task,
     * slow down all the app responses to ui and updates of the state properties
     * we must find another way */

//    private fun onReload(adUseCase: AdUseCase) = adsScope.launch(Dispatchers.Main + SupervisorJob()) {
//        delay(5000)
//        adUseCase.loadAdWithNoAdsCheck()
//    }

}


