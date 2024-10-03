package com.schoolkiller.presentation.screens.home

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.appopen.AppOpenAd
import com.schoolkiller.domain.UploadFileMethodOptions
import com.schoolkiller.domain.usecases.ads.OpenAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val openAdUseCase: OpenAdUseCase
) : ViewModel() {

    // OpenAd State
    private var _appOpenAd = MutableStateFlow<AppOpenAd?>(null)
    val appOpenAd: StateFlow<AppOpenAd?> = _appOpenAd

    private var _listOfImages = MutableStateFlow(mutableStateListOf<Uri>())
    var listOfImages: StateFlow<SnapshotStateList<Uri>> = _listOfImages

    private var _selectedUploadMethodOption by mutableStateOf(UploadFileMethodOptions.NO_OPTION)
    val selectedUploadMethodOption: UploadFileMethodOptions
        get() = _selectedUploadMethodOption

    private var _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedUri: StateFlow<Uri?> = _selectedImageUri

    fun showAppOpenAd(context: Context) {
        openAdUseCase.showOpenAppAd(context)
    }

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
        openAdUseCase.setOnLoaded { ad ->
            _appOpenAd.update { ad }
        }
    }

}