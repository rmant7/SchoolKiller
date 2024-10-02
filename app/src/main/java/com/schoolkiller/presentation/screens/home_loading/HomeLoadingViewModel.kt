package com.schoolkiller.presentation.screens.home_loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.domain.usecases.ads.OpenAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeLoadingViewModel @Inject constructor(
    private val openAdUseCase: OpenAdUseCase
) : ViewModel() {

    private var _isTimeOut = MutableStateFlow(false)
    val isTimeOut: StateFlow<Boolean> = _isTimeOut

    fun updateTimeOut(isTimeOut: Boolean) {
        _isTimeOut.value = isTimeOut
    }

    init {
        viewModelScope.launch {
            openAdUseCase.loadAd(
                onLoaded = {
                    updateTimeOut(true)
                },
                onFailedError = {
                    updateTimeOut(true)
                }
            )
        }
    }

}