package com.schoolkiller.presentation.screens.home

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.appopen.AppOpenAd
import com.schoolkiller.data.Constants
import com.schoolkiller.domain.UploadFileMethodOptions
import com.schoolkiller.domain.usecases.ads.OpenAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val openAdUseCase: OpenAdUseCase
) : ViewModel() {



    // OpenAd State
    private var _appOpenAd = MutableStateFlow<AppOpenAd?>(null)
    val appOpenAd: StateFlow<AppOpenAd?> = _appOpenAd
    private var _isOpenAdLoading = MutableStateFlow<Boolean>(false)
    val isOpenAdLoading: StateFlow<Boolean> = _isOpenAdLoading
    private var _openAdLoadTime = MutableStateFlow<Long>(0L)
    val openAdLoadTime: StateFlow<Long> = _openAdLoadTime
    private var _openAdLastAdShownTime = MutableStateFlow<Long>(0L)
    val openAdLastAdShownTime: StateFlow<Long> = _openAdLastAdShownTime


    fun updateAppOpenAd(newAd: AppOpenAd?) {
        _appOpenAd.update { newAd }
    }

    fun updateIsOpenAdLoading(isLoading: Boolean) {
        _isOpenAdLoading.update { isLoading }
    }

    fun updateOpenAdLoadTime(newAdLoadTime: Long) {
        _openAdLoadTime.update { newAdLoadTime }
    }

    fun updateOpenAdLastAdShownTime(newLastAdShowTime: Long) {
        _openAdLastAdShownTime.update { newLastAdShowTime }
    }


    fun loadOpenAd() = viewModelScope.launch {
        openAdUseCase.loadOpenAd(
            adUnitId = Constants.OPEN_AD_ID,
            viewModel = this@HomeViewModel
        )
    }


    private var _listOfImages = MutableStateFlow(mutableStateListOf<Uri>())
    var listOfImages: StateFlow<SnapshotStateList<Uri>> = _listOfImages

    private var _selectedUploadMethodOption by mutableStateOf(UploadFileMethodOptions.NO_OPTION)
    val selectedUploadMethodOption: UploadFileMethodOptions
        get() = _selectedUploadMethodOption

    private var _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedUri: StateFlow<Uri?> = _selectedImageUri



    fun insertImagesOnTheList(newImages: List<Uri>) {
        _listOfImages.update { it.apply { addAll(newImages) } }
    }

    fun deleteImageFromTheList(imageToDelete: Uri) {
        _listOfImages.update { it.apply { remove(imageToDelete) } }
    }

    fun updateSelectedUploadMethodOption(newUploadMethodSelection: UploadFileMethodOptions) {
        _selectedUploadMethodOption = newUploadMethodSelection
    }

    fun updateSelectedUri(newUri: Uri?) {
        _selectedImageUri.value = newUri
    }

    fun updateListOfImages(listOfImages: SnapshotStateList<Uri>) {
        _listOfImages.value = listOfImages
    }


    init {
        loadOpenAd()
    }

}