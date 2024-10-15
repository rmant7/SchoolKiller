package com.schoolkiller.domain.usecases.ads

import com.google.android.gms.ads.LoadAdError
import com.schoolkiller.BuildConfig
import timber.log.Timber



abstract class AdUseCase {

    private var onFailedError: (LoadAdError) -> Unit = {
        Timber.d(it.message)
    }

    private val isAdDisabled = BuildConfig.is_advertisement_disabled.toBoolean()

    // public function visible to other classes
    //if user has premium don't invoke
    open fun loadAdWithNoAdsCheck() {
        if (!isAdDisabled)
            load()
    }

    // every child has own implementation of this method
    protected abstract fun load()

    fun setOnFailedAction(onFailedError: (LoadAdError) -> Unit) {
        this.onFailedError = onFailedError
    }

    fun getOnFailedAction(): (LoadAdError) -> Unit {
        return onFailedError
    }
}