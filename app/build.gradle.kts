

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)

    // Room
    id ("androidx.room")
    // KSP
    id("com.google.devtools.ksp")
    // Dagger-Hilt
    id("com.google.dagger.hilt.android")
    // Secrets Gradle Plugin
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.schoolkiller"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.schoolkiller"
        minSdk = 29
        targetSdk = 34
        versionCode = 4
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Room Specific
    room {
        schemaDirectory("$projectDir/schemas")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {


    // AdMob advertisement
    implementation(libs.play.services.ads)
    // Added dependency on Guava to avoid conflicts with CameraX and AdMob
    implementation(libs.guava)/** CameraX not used anymore */
    // Process Lifecycle Owner
    //implementation("androidx.lifecycle:lifecycle-process:2.6.1")

    // Accompanist Permissions
    implementation (libs.accompanist.permissions)

    // Logging
    implementation (libs.timber)

    // LangChain4j
    implementation (libs.dev.langchain4j.langchain4j.open.ai)
    implementation (libs.langchain4j)

    // CameraX (not in use anymore)
//    implementation (libs.androidx.camera.core)
//    implementation (libs.androidx.camera.camera2)
//    implementation (libs.androidx.camera.lifecycle)
//    implementation (libs.androidx.camera.view)

    // Coil
    implementation(libs.coil.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runner)
    implementation(libs.androidx.espresso.core)
    implementation(libs.common)
    implementation(libs.play.services.ads.lite)
    ksp(libs.androidx.room.compiler)

    // DataStore Preferences
    implementation(libs.androidx.datastore.preferences)

    // Dagger - Hilt
    implementation(libs.hilt.android)
    ksp (libs.dagger.compiler)
    ksp (libs.hilt.compiler)
    ksp (libs.hilt.android.compiler)

    // Ktor & kotlin Serialization
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Gemini SDK Library, not needed cause we used Http fetch with Ktor
    implementation(libs.generativeai)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // ViewModel utilities for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Lifecycle utilities for Compose
    implementation(libs.androidx.lifecycle.runtime.compose)
    // Saved state module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}