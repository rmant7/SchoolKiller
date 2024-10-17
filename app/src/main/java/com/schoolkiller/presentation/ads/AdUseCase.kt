package com.schoolkiller.presentation.ads

import com.google.android.gms.ads.LoadAdError
import com.schoolkiller.BuildConfig
import timber.log.Timber



abstract class AdUseCase {

    private var onFailedError: (LoadAdError) -> Unit = {
        Timber.d(it.message)
    }

    private val isAdDisabled = BuildConfig.is_advertisement_disabled.toBoolean()

    // Public function visible to other classes.
    // If user has premium don't invoke
    open fun loadAdWithNoAdsCheck() {
        if (!isAdDisabled) load()
    }

    // Every child has own implementation of this method.
    protected abstract fun load()

    fun setOnFailedAction(onFailedError: (LoadAdError) -> Unit) {
        this.onFailedError = onFailedError
    }

    fun getOnFailedAction(): (LoadAdError) -> Unit {
        return onFailedError
    }
}