package com.schoolkiller

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.schoolkiller.data.network.CloudVisionApi
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
import java.io.IOException
import java.net.URL
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

        // google vision test
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url =
                    URL("https://static-00.iconduck.com/assets.00/text-plain-icon-2048x2026-yskbze1r.png")
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                CloudVisionApi().getAnnotatedImage(image)
            } catch (e: IOException) {
                println(e)
            }
        }

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

        CoroutineScope(Dispatchers.IO).launch {
            // Init FirebaseApp for all processes
            FirebaseApp.initializeApp(this@SchoolKillerApplication)

            Timber.d("Creating an extended library configuration.")
            val config = AppMetricaConfig
                .newConfigBuilder(BuildConfig.app_metrica_api_key)
                //.handleFirstActivationAsUpdate(true)
                .withLogs()
                .build()
            Timber.d("Initializing the AppMetrica SDK.")
            AppMetrica.activate(applicationContext, config)
            // Automatic tracking of user activity.
            AppMetrica.enableActivityAutoTracking(this@SchoolKillerApplication)
            Timber.d("Initializing the AppMetricaPush.")
            AppMetricaPush.activate(applicationContext)
        }

    }

}


