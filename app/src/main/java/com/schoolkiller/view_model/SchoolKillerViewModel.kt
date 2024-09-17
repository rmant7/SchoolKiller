package com.schoolkiller.view_model

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.data_Layer.entities.Picture
import com.schoolkiller.data_Layer.network.api.GeminiApiService
import com.schoolkiller.data_Layer.repositories.PictureRepository
import com.schoolkiller.domain.usecases.AddPictureUseCase
import com.schoolkiller.domain.usecases.DeletePictureUseCase
import com.schoolkiller.domain.usecases.ExtractGeminiResponseUseCase
import com.schoolkiller.domain.usecases.GetImageByteArrayUseCase
import com.schoolkiller.domain.usecases.ImagePickerUseCase
import com.schoolkiller.ui.UiState
import com.schoolkiller.utils.ExplanationLevelOptions
import com.schoolkiller.utils.GradeOptions
import com.schoolkiller.utils.SolutionLanguageOptions
import com.schoolkiller.utils.UploadFileMethodOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject


@HiltViewModel
class SchoolKillerViewModel @Inject constructor(
    private val pictureRepository: PictureRepository,
    private val addPictureUseCase: AddPictureUseCase,
    private val deletePictureUseCase: DeletePictureUseCase,
    private val geminiApiService: GeminiApiService,
    private val getImageByteArrayUseCase: GetImageByteArrayUseCase,
    private val extractGeminiResponseUseCase: ExtractGeminiResponseUseCase
) : ViewModel() {

    val allPictures = pictureRepository.allPictures

    private var _selectedGradeOption by mutableStateOf(GradeOptions.NONE)
    val selectedGradeOption: GradeOptions
        get() = _selectedGradeOption

    private var _selectedLanguageOption by mutableStateOf(SolutionLanguageOptions.ORIGINAL_TASK_LANGUAGE)
    val selectedSolutionLanguageOption: SolutionLanguageOptions
        get() = _selectedLanguageOption

    private var _selectedExplanationLevelOption by mutableStateOf(ExplanationLevelOptions.NO_EXPLANATION)
    val selectedExplanationLevelOption: ExplanationLevelOptions
        get() = _selectedExplanationLevelOption


    private val _selectedImages = MutableStateFlow(mutableStateListOf<Uri>())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages

    private var _selectedUploadMethodOption by mutableStateOf(UploadFileMethodOptions.TAKE_A_PICTURE)
    val selectedUploadMethodOption: UploadFileMethodOptions
        get() = _selectedUploadMethodOption


    fun updateSelectedGradeOption(newClassSelection: GradeOptions) {
        _selectedGradeOption = newClassSelection
    }

    fun updateSelectedLanguageOption(newLanguageSelection: SolutionLanguageOptions) {
        _selectedLanguageOption = newLanguageSelection
    }

    fun updateSelectedExplanationLevelOption(newExplanationLevelSelection: ExplanationLevelOptions) {
        _selectedExplanationLevelOption = newExplanationLevelSelection
    }

    fun updateSelectedUploadMethodOption(newUploadMethodSelection: UploadFileMethodOptions) {
        _selectedUploadMethodOption = newUploadMethodSelection
    }

    fun onImagesSelected(newImages: List<Uri>) {
        _selectedImages.value += newImages
    }

    fun onImageDeleted(imageToDelete: Uri) {
        _selectedImages.value -= imageToDelete
    }


    fun addPicture(picture: Picture) {
        viewModelScope.launch {
            addPictureUseCase(picture)
        }
    }

    fun deletePicture(picture: Picture) {
        viewModelScope.launch {
            deletePictureUseCase(picture)
        }
    }



//    private val _uiState: MutableStateFlow<UiState> =
//        MutableStateFlow(UiState.Initial)
//    val uiState: StateFlow<UiState> =
//        _uiState.asStateFlow()

    private val _textGenerationResult = MutableStateFlow<String?>("waiting...")
    val textGenerationResult = _textGenerationResult.asStateFlow()

    fun UpdateTextGenerationResult(resultText: String?) {
        _textGenerationResult.value = resultText
    }


    private val _uploadProgress = MutableStateFlow(0L)
    val uploadProgress: StateFlow<Long> = _uploadProgress


    fun uploadFile(
        imageUri: Uri,
        fileName: String,
        prompt: String
    ) {
        viewModelScope.launch {
            val fileByteArray = getImageByteArrayUseCase.invoke(imageUri = imageUri)
            val uploadResult = geminiApiService.uploadFileWithProgress(
                fileByteArray,
                fileName
            )

            uploadResult.onSuccess { uploadModel ->
                val fileUriResult = geminiApiService.uploadFileBytes(
                    uploadModel.uploadUrl,
                    fileByteArray
                )

                fileUriResult.onSuccess { fileUriJson ->
                    val actualFileUri = Json.parseToJsonElement(fileUriJson)
                        .jsonObject["file"]?.jsonObject?.get("uri")?.jsonPrimitive?.content

                    if (actualFileUri != null) {
                        val content = geminiApiService.generateContent(actualFileUri,prompt)
                        val textResponse = extractGeminiResponseUseCase.invoke(content)
                        UpdateTextGenerationResult(textResponse)
                    } else {
                        // Handle the case where the URI couldn't be extracted
                        UpdateTextGenerationResult("Something went wrong!")
                    }
                }

            }
        }
    }
}



