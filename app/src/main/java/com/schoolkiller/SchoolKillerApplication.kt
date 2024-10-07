package com.schoolkiller

import android.app.Application
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.schoolkiller.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.schoolkiller.domain.usecases.ads.BannerAdUseCase
import com.schoolkiller.domain.usecases.ads.InterstitialAdUseCase
import com.schoolkiller.presentation.toast.ShowToastMessage
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import io.appmetrica.analytics.push.AppMetricaPush
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SchoolKillerApplication : Application() {

    /* @Inject
     lateinit var openAppAdUseCase: OpenAdUseCase*/

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


        setMaxScreenSize()

        // preloading ads
        // openAppAdUseCase.loadAdWithNoAdsCheck()
        bannerAdUseCase.loadAdWithNoAdsCheck()
        interstitialAdUseCase.loadAdWithNoAdsCheck()


        CoroutineScope(Dispatchers.IO).launch {
            Timber.d("Creating an extended library configuration.")
            val config = AppMetricaConfig.newConfigBuilder(BuildConfig.app_metrica_api_key).build()
            Timber.d("Initializing the AppMetrica SDK.")
            AppMetrica.activate(applicationContext, config)
            Timber.d("Initializing the AppMetricaPush.")
            AppMetricaPush.activate(applicationContext)
        }

    }

    private fun setMaxScreenSize(){
        val height: Int
        val width: Int
        val windowManager = this.getSystemService(
            WindowManager::class.java
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            height = metrics.bounds.height()
            width = metrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.getDefaultDisplay().getMetrics(displayMetrics)
            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }
        bannerAdUseCase.setMaxHeight(height)
        bannerAdUseCase.setMaxWidth(width)

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


