package com.schoolkiller.data.repositories

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.schoolkiller.data.Constants
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.PromptText
import com.schoolkiller.domain.SolutionLanguageOption
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFERENCE_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferenceKeys {
        val gradeOptionState = stringPreferencesKey(name = Constants.GRADE_OPTION)
        val solutionGradeOptionState = stringPreferencesKey(name = Constants.SOLUTION_GRADE_OPTION)
        val languageOptionState = stringPreferencesKey(name = Constants.LANGUAGE_OPTION)
        val explanationLevelOptionState =
            stringPreferencesKey(name = Constants.EXPLANATION_LEVEL_OPTION)
        val descriptionState = stringPreferencesKey(name = Constants.DESCRIPTION)
        val solvePromptState = stringPreferencesKey(name = Constants.SOLVE_PROMPT)
        val solutionPromptState = stringPreferencesKey(name = Constants.SOLUTION_PROMPT)
        val selectedImageUri = stringPreferencesKey(name = Constants.SELECTED_IMAGE_URI)
        val imageList = stringPreferencesKey(name = Constants.IMAGE_LIST)
    }

    private val dataStore = context.dataStore


    suspend fun persistGradeOptionState(gradeOption: GradeOption) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.gradeOptionState] = gradeOption.name
        }
    }

    suspend fun persistSolutionGradeOptionState(solutionGradeOption: GradeOption) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.solutionGradeOptionState] = solutionGradeOption.name
        }
    }

    suspend fun persistLanguageOptionState(languageOption: SolutionLanguageOption) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.languageOptionState] = languageOption.name
        }
    }

    suspend fun persistExplanationLevelOptionState(explanationLevelOption: ExplanationLevelOption) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.explanationLevelOptionState] = explanationLevelOption.name
        }
    }

    suspend fun persistDescriptionState(description: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.descriptionState] = description
        }
    }

    suspend fun persistSolvePromptState(solvePrompt: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.solvePromptState] = solvePrompt
        }
    }

    suspend fun persistSolutionPromptState(solutionPrompt: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.solutionPromptState] = solutionPrompt
        }
    }

    suspend fun persistImageListState(imageList: List<Uri>) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preference ->
                val wrapper = UriListWrapper(imageList.map { it.toString() })
                val jsonString = Json.encodeToString(wrapper)
                preference[PreferenceKeys.imageList] = jsonString
            }
        }
    }

    suspend fun persistImageState(imageUri: Uri) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.selectedImageUri] = imageUri.toString()
        }
    }

    val readGradeOptionState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val gradeOptionState =
                preferences[PreferenceKeys.gradeOptionState] ?: GradeOption.NONE.name
            gradeOptionState
        }

    val readSolutionGradeOptionState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val solutionGradeOptionState =
                preferences[PreferenceKeys.solutionGradeOptionState] ?: GradeOption.NONE.name
            solutionGradeOptionState
        }

    val readLanguageOptionState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val languageOptionState = preferences[PreferenceKeys.languageOptionState]
                ?: SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE.name
            languageOptionState
        }

    val readExplanationLevelOptionState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val explanationLevelOptionState =
                preferences[PreferenceKeys.explanationLevelOptionState]
                    ?: ExplanationLevelOption.SHORT_EXPLANATION.name
            explanationLevelOptionState
        }

    val readDescriptionState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val descriptionState = preferences[PreferenceKeys.descriptionState] ?: ""
            descriptionState
        }

    val readSolvePromptState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val solvePromptState =
                preferences[PreferenceKeys.solvePromptState] ?: PromptText.SOLVE_PROMPT.promptText
            solvePromptState
        }


    val readSolutionPromptState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val solutionPromptState = preferences[PreferenceKeys.solutionPromptState]
                ?: PromptText.CHECK_SOLUTION_PROMPT.promptText
            solutionPromptState
        }

    val readImageListState: Flow<List<Uri>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                withContext(Dispatchers.IO) { emit(emptyPreferences()) }
            } else {
                throw exception
            }
        }.map { preferences ->
            val jsonString = preferences[PreferenceKeys.imageList] ?: ""
            try {
                Json.decodeFromString<UriListWrapper>(jsonString).uris.map { Uri.parse(it) }
            } catch (e: Exception) {
                emptyList()
            }
        }


    val readImageState: Flow<Uri?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val uriString = preferences[PreferenceKeys.selectedImageUri]
            uriString?.let { Uri.parse(it) }
        }

}

@Serializable
data class UriListWrapper(val uris: List<String>)