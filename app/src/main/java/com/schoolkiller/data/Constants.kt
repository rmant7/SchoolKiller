package com.schoolkiller.data

object Constants {

    // Datastore Preference
    const val PREFERENCE_NAME = "school_killer_preferences"
    const val GRADE_OPTION = "grade_option"
    const val SOLUTION_GRADE_OPTION = "solution_grade_option"
    const val LANGUAGE_OPTION = "language_option"
    const val EXPLANATION_LEVEL_OPTION = "explanation_level_option"
    const val DESCRIPTION = "description"
    const val SOLVE_PROMPT = "original_solve_prompt"
    const val SOLUTION_PROMPT = "original_solution_prompt"
    const val IMAGE_LIST = "image_list"
    const val SELECTED_IMAGE_URI = "selected_image_uri"


    // Database
    const val DATABASE_NAME = "school_killer_database"

    // Entities
    const val PICTURE_TABLE_NAME = "Picture"

    // Gemini Models
    const val GEMINI_FLASH_1_5 = "gemini-1.5-flash:generateContent"
    const val GEMINI_FLASH_1_5_002 = "gemini-1.5-flash-002:generateContent"
    const val GEMINI_FLASH_LATEST="gemini-1.5-flash-latest:generateContent"

    // Adds IDs
    const val OPEN_AD_SAMPLE_ID = "ca-app-pub-3940256099942544/9257395921"
    const val INTERSTITIAL_AD_SAMPLE_ID = "ca-app-pub-3940256099942544/1033173712"
    const val BANNER_AD_SAMPLE_ID = "ca-app-pub-3940256099942544/9214589741"
    const val INTERSTITIAL_AD_ID = "ca-app-pub-7574006463043131/1940332933"
    const val BANNER_AD_ID = "ca-app-pub-7574006463043131/3537319788"
    const val OPEN_AD_ID = "ca-app-pub-7574006463043131/8938590785"
    const val OPEN_AD_COOLDOWN = 60 * 1000 // 1 minute


    // Deleting device storage images
    const val DELETE_REQUEST_CODE = 123

}