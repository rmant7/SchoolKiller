package com.schoolkiller.presentation.screens.home

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data.repositories.DataStoreRepository
import com.schoolkiller.data.repositories.DeleteFileRepository
import com.schoolkiller.data.repositories.SaveFileRepository
import com.schoolkiller.domain.UploadFileMethodOptions
import com.schoolkiller.domain.model.HomeProperties
import com.schoolkiller.domain.usecases.ads.OpenAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val openAdUseCase: OpenAdUseCase,
    private val dataStoreRepository: DataStoreRepository,
    private val saveFileRepository: SaveFileRepository,
    private val deleteFileRepository: DeleteFileRepository
) : ViewModel() {

    val permissionDialogQueueList = mutableStateListOf<String>()

    fun onDismissPermissionDialog() {
        permissionDialogQueueList.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !permissionDialogQueueList.contains(permission)) {
            permissionDialogQueueList.add(permission)
        }
    }

    private val _homePropertiesState = MutableStateFlow(HomeProperties())
    val homePropertiesState: StateFlow<HomeProperties> = _homePropertiesState.asStateFlow()


    fun updateSelectedUploadMethodOption(newUploadMethodSelection: UploadFileMethodOptions) {
        _homePropertiesState.update { currentState ->
            currentState.copy(selectedUploadMethodOption = newUploadMethodSelection)
        }
    }

    fun updateSelectedImageIndex(newIndex: Int?) {
        _homePropertiesState.update { currentState ->
            currentState.copy(selectedImageIndex = newIndex)
        }
    }

    fun updateIsImageEnlarged(isEnlarged: Boolean) {
        _homePropertiesState.update { currentState ->
            currentState.copy(isImageEnlarged = isEnlarged)
        }
    }

    fun updateSelectedUri(newUri: Uri?) {
        _homePropertiesState.update { currentState ->
            currentState.copy(selectedImageUri = newUri)
        }
        viewModelScope.launch(Dispatchers.IO) { newUri?.let { persistImageState(it) } }
    }


    private fun updateListOfImages(newListOfImages: List<Uri>) {
        _homePropertiesState.update { currentState ->
            currentState.copy(listOfImages = newListOfImages)
        }
    }

    fun showAppOpenAd(context: Context) {
        openAdUseCase.showOpenAppAd(context)
    }


    fun clearImagesFromTheList() {
        _homePropertiesState.update { currentState ->
            currentState.copy(listOfImages = emptyList())
        }
    }


    fun insertImagesOnTheList(newImages: List<Uri>) {
        _homePropertiesState.update { currentState ->
            currentState.copy(
                listOfImages = newImages + currentState.listOfImages)
//                currentState.listOfImages.toMutableList()
//                    .apply { addAll(newImages) })
        }
        val updatedList = _homePropertiesState.value.listOfImages
        updateListOfImages(updatedList)
        viewModelScope.launch(Dispatchers.IO) { persistImageListState(updatedList) }
    }

    fun deleteImageFromTheList(imageToDelete: Uri) {
        _homePropertiesState.update { currentState ->
            currentState.copy(
                listOfImages = currentState.listOfImages.toMutableStateList()
                    .apply { remove(imageToDelete) })
        }
        val updatedList = _homePropertiesState.value.listOfImages
        updateListOfImages(updatedList)
        viewModelScope.launch(Dispatchers.IO) { persistImageListState(updatedList) }
    }

    suspend fun saveImage(bitmap: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            saveFileRepository.saveImage(bitmap)
        }
    }

    suspend fun getCameraSavedImageUri(): Uri? {

        return withContext(Dispatchers.IO) { saveFileRepository.getCameraSavedImageUri() }
    }

    fun checkUriValidity(uri: Uri): Boolean {
        return deleteFileRepository.checkUriValidity(uri)
    }

    fun getInvalidImageUris(): List<Uri> {
        return deleteFileRepository.getInvalidImageUris()
    }

    fun cleanInvalidImages(activity: Activity, invalidUris: List<Uri?>) {
        deleteFileRepository.cleanInvalidImages(activity, invalidUris)
    }

    fun deleteImageFromStorage(activity: Activity, imageUri: Uri) {
        deleteFileRepository.deleteImageFromStorage(activity, imageUri)
    }

    private suspend fun persistImageState(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistImageState(imageUri)
        }
    }

    private suspend fun persistImageListState(imageList: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.IO) {
                dataStoreRepository.persistImageListState(imageList = imageList)
            }
        }
    }

    private suspend fun readImageState() {
        viewModelScope.launch {
            try {
                dataStoreRepository.readImageState.collect { imageUri ->
                    withContext(Dispatchers.Main) {
                        updateSelectedUri(imageUri)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error reading image state")
                updateSelectedUri(null)
            }
        }
    }


    private suspend fun readImageListState() {
        viewModelScope.launch {
            try {
                dataStoreRepository.readImageListState.collect { imageList ->
                    withContext(Dispatchers.Main) {
                        updateListOfImages(imageList)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error reading image list  state")
                updateListOfImages(emptyList())
            }
        }
    }


    init {
        openAdUseCase.setOnLoaded { ad ->
            _homePropertiesState.update { currentState ->
                currentState.copy(appOpenAdd = ad)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            readImageState()
            readImageListState()
        }
    }

}