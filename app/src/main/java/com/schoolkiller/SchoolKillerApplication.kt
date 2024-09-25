package com.schoolkiller

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SchoolKillerApplication : Application() {
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {
            Log.d("tag", "MobileAds init ")
        }
        appOpenAdManager = AppOpenAdManager(this)

        // Remove comment block to test
        /*
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            appOpenAdManager.defaultLifecycleObserver
        )
         */
    }

}

