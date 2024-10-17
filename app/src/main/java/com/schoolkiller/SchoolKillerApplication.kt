package com.schoolkiller

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.schoolkiller.presentation.ads.BannerAdUseCase
import com.schoolkiller.presentation.ads.InterstitialAdUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SchoolKillerApplication : Application() {

    private val adsScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        //content://media/picker/0/com.android.providers.media.photopicker/media/57
        // google vision test
        /*CoroutineScope(Dispatchers.IO).launch {
            try {
                val url =
                    URL("https://static-00.iconduck.com/assets.00/text-plain-icon-2048x2026-yskbze1r.png")
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                CloudVisionApi().getAnnotatedImage(image)
            } catch (e: IOException) {
                println(e)
            }
        }*/

        // Take out logs of the release version with this set. logs decrease performance
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize the Google Mobile Ads SDK on a background thread.
        adsScope.launch(Dispatchers.IO + SupervisorJob()) {
            MobileAds.initialize(this@SchoolKillerApplication) {}
            MobileAds.setAppMuted(true)
        }

        // Initialize AppMetrica on a background thread.
        /*CoroutineScope(Dispatchers.IO).launch {
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
        }*/

    }

}


