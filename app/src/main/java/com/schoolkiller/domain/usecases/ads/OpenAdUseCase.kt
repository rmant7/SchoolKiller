package com.schoolkiller.domain.usecases.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.schoolkiller.data.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : AdUseCase {

    var appOpenAd: AppOpenAd? = null

    var openAdLoadTime: Long = 0L
    var openAdLastAdShownTime: Long = 0L
    var isOpenAdLoading: Boolean = false

    fun loadA() {
        loadAd(onLoaded = {
            Timber.d("Successfully loaded Open App Ad")
        },
            onFailedError = {
                Timber.d("Open App Ad server is not available ${it.message}")
            })
    }

    fun loadAd(
        onLoaded: (AppOpenAd) -> Unit,
        onFailedError: (LoadAdError) -> Unit
    ) {
        shouldShowAdsCheck()

        if (isOpenAdLoading && appOpenAd != null) return

        isOpenAdLoading = true
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            Constants.OPEN_AD_ID,
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    openAdLoadTime = System.currentTimeMillis()
                    isOpenAdLoading = false

                    onLoaded(ad)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Timber.d(loadAdError.message)
                    isOpenAdLoading = false
                    appOpenAd = null

                    onFailedError(loadAdError)
                }
            }
        )
    }

    fun showOpenAppAd(context: Context) {
        val timeSinceLoad = System.currentTimeMillis() - openAdLoadTime
        val timeSinceLastShown = System.currentTimeMillis() - openAdLastAdShownTime

        // isn't expired yet
        val expirationCheck = timeSinceLoad <= 4 * 60 * 60 * 1000 // 4 hour expiration check
        // cooldown is over
        val cooldownCheck = timeSinceLastShown >= Constants.OPEN_AD_COOLDOWN

        // appOpenAd isn't loaded or time out and cooldown isn't over yet
        if (appOpenAd == null || (expirationCheck && !cooldownCheck)) return

        appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                loadA()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
            }

            override fun onAdShowedFullScreenContent() {
                appOpenAd = null
                openAdLastAdShownTime = System.currentTimeMillis()
            }
        }
        appOpenAd!!.show(context as Activity)

    }

}