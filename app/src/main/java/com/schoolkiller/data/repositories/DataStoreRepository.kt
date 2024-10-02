package com.schoolkiller.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.schoolkiller.data.Constants
import com.schoolkiller.domain.ExplanationLevelOption
import com.schoolkiller.domain.GradeOption
import com.schoolkiller.domain.SolutionLanguageOption
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFERENCE_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferenceKeys {
        val gradeOptionState = stringPreferencesKey(name = Constants.GRADE_OPTION)
        val languageOptionState = stringPreferencesKey(name = Constants.LANGUAGE_OPTION)
        val explanationLevelOptionState = stringPreferencesKey(name = Constants.EXPLANATION_LEVEL_OPTION)
        val descriptionState = stringPreferencesKey(name = Constants.DESCRIPTION)
    }

    private val dataStore = context.dataStore


    suspend fun persistGradeOptionState(gradeOption: GradeOption) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.gradeOptionState] = gradeOption.name
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

    suspend fun persistDescriptionState(description : String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.descriptionState] = description
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
            val gradeOptionState = preferences[PreferenceKeys.gradeOptionState] ?: GradeOption.NONE.name
            gradeOptionState
        }

    val readLanguageOptionState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val languageOptionState = preferences[PreferenceKeys.languageOptionState] ?: SolutionLanguageOption.ORIGINAL_TASK_LANGUAGE.name
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
            val explanationLevelOptionState = preferences[PreferenceKeys.explanationLevelOptionState] ?: ExplanationLevelOption.SHORT_EXPLANATION.name
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

}