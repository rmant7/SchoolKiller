package com.schoolkiller.domain.usecases.adds

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.schoolkiller.data.Constants
import timber.log.Timber


class AppOpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null
    private var isShowingAd = false
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()

    private val isAdAvailable: Boolean
        get() = appOpenAd != null

    // Used
    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921" //test id
    }

    fun fetchAd(currentActivity: Activity) {
        if (isAdAvailable) return

        Timber.tag("tag").d("fetching... ")
        loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Timber.tag("error").d("Ad failed to load")
            }

            override fun onAdLoaded(ad: AppOpenAd) {
                super.onAdLoaded(ad)
                appOpenAd = ad
                Timber.tag("error").d("Ad is available, trying to show it.")
                showAdIfAvailable(currentActivity)
            }
        }

        val request = adRequest
        AppOpenAd.load(
            currentActivity,
            Constants.OPEN_AD_ID,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            loadCallback!!
        )

    }

    private fun createFullScreenContentCallback(): FullScreenContentCallback {
        return object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {

            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
    }

    private fun showAdIfAvailable(currentActivity: Activity) {
        Timber.tag("tag").d("$isShowingAd  - $isAdAvailable")

        if (!isShowingAd && isAdAvailable) {
            Timber.tag("tag").d("Showing ad ")

            currentActivity.let { activity ->
                appOpenAd?.run {
                    appOpenAd!!.fullScreenContentCallback =
                        createFullScreenContentCallback()
                    appOpenAd!!.show(activity)
                }
            }
        } else {
            Timber.tag("tag").d("Ad isn't available ")
        }
    }
}