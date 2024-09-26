package com.schoolkiller.presentation.view_model

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.schoolkiller.data.Constants
import com.schoolkiller.data.entities.Picture
import com.schoolkiller.data.network.api.GeminiApiService
import com.schoolkiller.data.network.response.GeminiResponse
import com.schoolkiller.data.repositories.PictureRepository
import com.schoolkiller.domain.ExplanationLevelOptions
import com.schoolkiller.domain.GradeOptions
import com.schoolkiller.domain.SolutionLanguageOptions
import com.schoolkiller.domain.UploadFileMethodOptions
import com.schoolkiller.domain.usecases.adds.AdUseCases
import com.schoolkiller.domain.usecases.api.ExtractGeminiResponseUseCase
import com.schoolkiller.domain.usecases.api.GetImageByteArrayUseCase
import com.schoolkiller.domain.usecases.database.AddPictureUseCase
import com.schoolkiller.domain.usecases.database.DeletePictureUseCase
import com.schoolkiller.domain.usecases.prompt.ConvertPromptUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val extractGeminiResponseUseCase: ExtractGeminiResponseUseCase,
    private val convertPromptUseCases: ConvertPromptUseCases,
    private val adUseCases: AdUseCases,
) : ViewModel() {


    val allPictures = pictureRepository.allPictures

    // selected Uri
    private var _selectedUri = MutableStateFlow<Uri?>(null)
    val selectedUri: StateFlow<Uri?> = _selectedUri

    // converted prompt by Additional Information Screen
    private var _originalPrompt = MutableStateFlow<String>("")
    val originalPrompt: StateFlow<String> = _originalPrompt

    // Additional Information Text addition
    private var _additionalInfoText = MutableStateFlow<String>("")
    val additionalInfoText: StateFlow<String> = _additionalInfoText

    //model selection
//    private var _selectedAiModelOption by mutableStateOf(AiModelOptions.MODEL_ONE)
//    val selectedAiModelOption: AiModelOptions
//        get() = _selectedAiModelOption

    //selected rating max
    private var _selectedRateMax by mutableIntStateOf(100)
    val selectedRateMax: Int
        get() = _selectedRateMax

    private var _selectedGradeOption by mutableStateOf(GradeOptions.NONE)
    val selectedGradeOption: GradeOptions
        get() = _selectedGradeOption

    private var _selectedLanguageOption by mutableStateOf(SolutionLanguageOptions.ORIGINAL_TASK_LANGUAGE)
    val selectedSolutionLanguageOption: SolutionLanguageOptions
        get() = _selectedLanguageOption

    private var _selectedExplanationLevelOption by mutableStateOf(ExplanationLevelOptions.SHORT_EXPLANATION)
    val selectedExplanationLevelOption: ExplanationLevelOptions
        get() = _selectedExplanationLevelOption


    private val _listOfImages = MutableStateFlow(mutableStateListOf<Uri>())
    val listOfImages: StateFlow<SnapshotStateList<Uri>> = _listOfImages

    private var _selectedUploadMethodOption by mutableStateOf(UploadFileMethodOptions.NO_OPTION)
    val selectedUploadMethodOption: UploadFileMethodOptions
        get() = _selectedUploadMethodOption

    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?>
        get() = _error.asStateFlow()

    // For requesting Gemini response again
    private var _requestGeminiResponse = MutableStateFlow<Boolean>(true)
    val requestGeminiResponse: StateFlow<Boolean> = _requestGeminiResponse

    // OpenAd State
    private var _appOpenAd = MutableStateFlow<AppOpenAd?>(null)
    val appOpenAd: StateFlow<AppOpenAd?> = _appOpenAd
    private var _isOpenAdLoading = MutableStateFlow<Boolean>(false)
    val isOpenAdLoading: StateFlow<Boolean> = _isOpenAdLoading
    private var _openAdLoadTime = MutableStateFlow<Long>(0L)
    val openAdLoadTime: StateFlow<Long> = _openAdLoadTime
    private var _openAdLastAdShownTime = MutableStateFlow<Long>(0L)
    val openAdLastAdShownTime: StateFlow<Long> = _openAdLastAdShownTime
    private val _triggerAdLoadAfterCooldown = MutableStateFlow(false)
    val triggerAdLoadAfterCooldown: StateFlow<Boolean> = _triggerAdLoadAfterCooldown.asStateFlow()
    fun updateAppOpenAd(newAd: AppOpenAd?) { _appOpenAd.update { newAd } }
    fun updateIsOpenAdLoading(isLoading: Boolean) { _isOpenAdLoading.update { isLoading } }
    fun updateOpenAdLoadTime(newAdLoadTime: Long) { _openAdLoadTime.update { newAdLoadTime } }
    fun updateOpenAdLastAdShownTime(newLastAdShowTime: Long) { _openAdLastAdShownTime.update { newLastAdShowTime } }
    fun updateTriggerAdLoadAfterCooldown(isTriggered: Boolean) { _triggerAdLoadAfterCooldown.update { isTriggered } }


    // BannerAd State
    private var _adview = MutableStateFlow<AdView?>(null)
    val adview: StateFlow<AdView?> = _adview
    fun updateAdview(newAd: AdView?) { _adview.update { newAd } }

    // InterstitialAd State
    private var _interstitialAd = MutableStateFlow<InterstitialAd?>(null)
    val interstitialAd: StateFlow<InterstitialAd?> = _interstitialAd
    fun updateInterstitialAd(newAd: InterstitialAd?) { _interstitialAd.update { newAd } }

    // Ads calling functions
    fun loadOpenAd() = viewModelScope.launch {
        adUseCases.openAdUseCase.loadOpenAd(
            adUnitId = Constants.OPEN_AD_ID,
            viewModel = this@SchoolKillerViewModel
        )
    }

    fun loadBannerAd() {
        viewModelScope.launch {
            adUseCases.bannerAdUseCase.loadAd(
                adUnitId = Constants.BANNER_AD_ID,
                viewModel = this@SchoolKillerViewModel,
                adSize = AdSize.BANNER)
        }
    }

    fun loadInterstitialAd() {
        adUseCases.interstitialAdUseCase.loadAd(
            adUnitId = Constants.INTERSTITIAL_AD_ID,
            viewModel = this@SchoolKillerViewModel
        )
    }



    fun updateSelectedRateMax(newRateMax: Int) {
        _selectedRateMax = newRateMax
    }

    fun updateSelectedUri(newUri: Uri?) {
        _selectedUri.value = newUri
    }

    fun updatePrompt(convertedPrompt: String) {
        _originalPrompt.value = convertedPrompt
    }

    fun updateAdditionalInfoText(addedText: String) {
        _additionalInfoText.value = addedText
    }

//    fun updateSelectedAiModelOption(newAiModelSelection: AiModelOptions) {
//        _selectedAiModelOption = newAiModelSelection
//    }

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

    fun insertImagesOnTheList(newImages: List<Uri>) {
        _listOfImages.update { it.apply { addAll(newImages) } }
    }

    fun deleteImageFromTheList(imageToDelete: Uri) {
        _listOfImages.update { it.apply { remove(imageToDelete) } }
    }

    fun updateRequestGeminiResponse(requestResponse: Boolean) {
        _requestGeminiResponse.update { requestResponse }
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


    private val _textGenerationResult = MutableStateFlow("")
    val textGenerationResult = _textGenerationResult.asStateFlow()

    fun updateTextGenerationResult(resultText: String?, error: Throwable? = null) {
        resultText?.let { text -> _textGenerationResult.update { text } }
        error?.let { err -> _error.update { err } }
    }

    //Don't remove, for future development
    /*
       fun fetchAIResponse(
           imageUri: Uri,
           fileName: String,
           context: Context
           aiModelOption: AiModelOptions
       ) {

           when (aiModelOption) {
             AiModelOptions.MODEL_ONE -> fetchOpenAiResponse(imageUri)
               AiModelOptions.MODEL_TWO -> fetchGeminiResponse(
                   imageUri, fileName, ""
               )
           }
       }
   */

    //Don't remove, for future development
    /*
       private fun convertToBase64(selectedUri: Uri, context: Context): String {
           val bitmap = MediaStore.Images.Media.getBitmap(
               context.contentResolver,
               selectedUri
           )
           val outputStream = ByteArrayOutputStream()
           bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
           val byteArray = outputStream.toByteArray()

           val encodedString: String = Base64.encodeToString(
               byteArray, Base64.DEFAULT
           )
           return encodedString
       }
   */

    //Don't remove, for future development
    /*
    fun fetchOpenAiResponse(imageUri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val key = "API_KEY"

            val model: OpenAiChatModel = OpenAiChatModel.builder()
                .apiKey(key)
                .modelName("gpt-4o")
                .build()

            val userMessage: UserMessage = UserMessage.from(
                TextContent.from("What is in this picture?"),
                ImageContent.from(
                    convertToBase64(imageUri, context), "image/png",
                    ImageContent.DetailLevel.LOW
                )
            )
            val response: Response<AiMessage> = model.generate(userMessage)

            updateTextGenerationResult(response.content().text())
        }
    }
*/

    fun fetchGeminiResponse(
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
                        val textResponse = if (content is GeminiResponse.Success) {
                            extractGeminiResponseUseCase.invoke(content.data ?: "{}")
                        } else {
                            content.message
                        }
                        updateTextGenerationResult(textResponse)
                    } else {
                        // Handle the case where the URI couldn't be extracted
                        updateTextGenerationResult(null, RuntimeException(" URI couldn't be extracted"))
                    }
                }

                fileUriResult.onFailure { throwable ->
                    _error.update { throwable }
                }
            }
            uploadResult.onFailure { throwable ->
                _error.update { throwable }
            }
        }
    }

    fun importGradeToOriginalPrompt() {
        updatePrompt(
            convertPromptUseCases.importGradeToPromptUseCase.invoke(
                gradeOption = selectedGradeOption,
                originalPrompt = originalPrompt.value
            )
        )
    }

    fun importLanguageToOriginalPrompt() {
        updatePrompt(
            convertPromptUseCases.importLanguageToPromptUseCase.invoke(
                languageOption = selectedSolutionLanguageOption,
                originalPrompt = originalPrompt.value
            )
        )
    }

    fun importExplanationToOriginalPrompt() {
        updatePrompt(
            convertPromptUseCases.importExplanationToPromptUseCase.invoke(
                explanationOption = selectedExplanationLevelOption,
                originalPrompt = originalPrompt.value
            )
        )
    }

    fun importAdditionalInfoToOriginalPrompt() {
        updatePrompt(
            convertPromptUseCases.importAdditionalInfoToPromptUseCase.invoke(
                originalPrompt = originalPrompt.value,
                additionalInformationText = additionalInfoText.value
            )
        )
    }

    fun clearError() {
        _error.value = null;
    }

    // initialize ads as soon as the app starts
    init {
            loadOpenAd()
            loadBannerAd()
            loadInterstitialAd()
    }

}



