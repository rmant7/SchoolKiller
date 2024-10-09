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

        // preloading ads
        // openAppAdUseCase.loadAdWithNoAdsCheck()
        bannerAdUseCase.loadAdWithNoAdsCheck()
        interstitialAdUseCase.loadAdWithNoAdsCheck()


        // it's recommended to initialize AppMetrica in the main process instead
        CoroutineScope(Dispatchers.IO).launch {
            Timber.d("Creating an extended library configuration.")
            val config = AppMetricaConfig.newConfigBuilder(BuildConfig.app_metrica_api_key).build()
            Timber.d("Initializing the AppMetrica SDK.")
            AppMetrica.activate(applicationContext, config)
            Timber.d("Initializing the AppMetricaPush.")
            AppMetricaPush.activate(applicationContext)
        }

    }

}


