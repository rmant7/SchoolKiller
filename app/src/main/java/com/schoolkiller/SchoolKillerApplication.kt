package com.schoolkiller

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.webkit.WebView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.schoolkiller.presentation.ads.BannerAdUseCase
import com.schoolkiller.presentation.ads.InterstitialAdUseCase
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import io.appmetrica.analytics.push.AppMetricaPush
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.Bidi
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

        val processName = getProcessName()
        if (!packageName.equals(processName)) {
            WebView.setDataDirectorySuffix(processName)
        }

        // Create notification channel
        /*val notificationChannel = NotificationChannel(
            "app_channel",
            "SchoolKiller",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.setSound(null, null) // Disable sound for this channel
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Setting up the channel
        notificationManager.createNotificationChannel(notificationChannel)*/

        // Initialize the Google Mobile Ads SDK on a background thread.
        adsScope.launch(Dispatchers.IO + SupervisorJob()) {
            MobileAds.initialize(this@SchoolKillerApplication) {}
            MobileAds.setAppMuted(true)
        }

        // preloading ads
        // openAppAdUseCase.loadAdWithNoAdsCheck()
        bannerAdUseCase.loadAdWithNoAdsCheck()
        interstitialAdUseCase.loadAdWithNoAdsCheck()

        // Initialize AppMetrica on a background thread.
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


