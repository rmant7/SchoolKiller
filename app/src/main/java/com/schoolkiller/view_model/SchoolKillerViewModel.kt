package com.schoolkiller.view_model

import android.app.Application
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.schoolkiller.SchoolKillerApplication
import com.schoolkiller.data_Layer.entities.Picture
import com.schoolkiller.data_Layer.network.api.GeminiApiService
import com.schoolkiller.data_Layer.repositories.PictureRepository
import com.schoolkiller.domain.usecases.AddPictureUseCase
import com.schoolkiller.domain.usecases.DeletePictureUseCase
import com.schoolkiller.domain.usecases.ExtractGeminiResponseUseCase
import com.schoolkiller.domain.usecases.GetImageByteArrayUseCase
import com.schoolkiller.utils.AiModelOptions
import com.schoolkiller.utils.ExplanationLevelOptions
import com.schoolkiller.utils.GradeOptions
import com.schoolkiller.utils.SolutionLanguageOptions
import com.schoolkiller.utils.UploadFileMethodOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.output.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class SchoolKillerViewModel @Inject constructor(
    private val pictureRepository: PictureRepository,
    private val addPictureUseCase: AddPictureUseCase,
    private val deletePictureUseCase: DeletePictureUseCase,
    private val geminiApiService: GeminiApiService,
    private val getImageByteArrayUseCase: GetImageByteArrayUseCase,
    private val extractGeminiResponseUseCase: ExtractGeminiResponseUseCase, application: Application
) : AndroidViewModel(application) {

    val allPictures = pictureRepository.allPictures

    //selected Uri
    private var _selectedUri by mutableStateOf<Uri?>(null)
    val selectedUri: Uri?
        get() = _selectedUri

    //model selection
    private var _selectedAiModelOption by mutableStateOf(AiModelOptions.MODEL_ONE)
    val selectedAiModelOption: AiModelOptions
        get() = _selectedAiModelOption

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

    fun updateSelectedUri(newUri: Uri){
        _selectedUri = newUri
    }

    fun updateSelectedAiModelOption(newAiModelSelection: AiModelOptions) {
        _selectedAiModelOption = newAiModelSelection
    }

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

    private fun updateTextGenerationResult(resultText: String?) {
        _textGenerationResult.value = resultText
    }


    private val _uploadProgress = MutableStateFlow(0L)
    val uploadProgress: StateFlow<Long> = _uploadProgress

    fun fetchAIResponse(
        imageUri: Uri,
        fileName: String,
        aiModelOption: AiModelOptions
    ) {
        fetchGeminiResponse(imageUri, fileName, "")

        //OpenAi solution crashes

        /*when (aiModelOption) {
           AiModelOptions.MODEL_ONE -> fetchOpenAiResponse(imageUri)
            AiModelOptions.MODEL_TWO -> fetchGeminiResponse(
                imageUri, fileName, ""
            )
        }*/
    }

    private fun convertToBase64(selectedUri: Uri): String {
        val bitmap = MediaStore.Images.Media.getBitmap(
            getApplication<SchoolKillerApplication>().contentResolver,
            selectedUri)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        val encodedString: String = Base64.encodeToString(
            byteArray, Base64.DEFAULT)
        return encodedString
    }

    private fun fetchOpenAiResponse(imageUri: Uri) {
        viewModelScope.launch (Dispatchers.IO){
           val model: OpenAiChatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build()

            val userMessage: UserMessage = UserMessage.from(
                TextContent.from("What is in this picture?"),
                ImageContent.from(convertToBase64(imageUri), "image/png",
                    ImageContent.DetailLevel.LOW)
            )
            val response: Response<AiMessage> = model.generate(userMessage)

            updateTextGenerationResult(response.content().text())
        }
    }

    private fun fetchGeminiResponse(
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
                        val content = geminiApiService.generateContent(actualFileUri, prompt)
                        val textResponse = extractGeminiResponseUseCase.invoke(content)
                        updateTextGenerationResult(textResponse)
                    } else {
                        // Handle the case where the URI couldn't be extracted
                        updateTextGenerationResult("Something went wrong!")
                    }
                }

            }
        }
    }
}



