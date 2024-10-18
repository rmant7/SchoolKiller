plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)

    // Room
    id("androidx.room")
    // KSP
    id("com.google.devtools.ksp")
    // Dagger-Hilt
    id("com.google.dagger.hilt.android")
    // Secrets Gradle Plugin
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.schoolkiller"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.schoolkiller"
        minSdk = 29
        targetSdk = 34
        versionCode = 9
        versionName = "1.9"

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
            excludes += "/META-INF/{AL2.0,LGPL2.1,DEPENDENCIES}"
        }
    }

}

dependencies {

    // Google's "fix" of Guava duplicate issue.
    implementation(libs.listenablefuture)
    // Google cloud vision
    implementation("com.google.apis:google-api-services-vision:v1-rev369-1.23.0"){
        exclude(group="com.google.guava", module="guava-jdk5")
    }
    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude(module = "httpclient")
        exclude (group="com.google.guava", module="guava-jdk5")
    }
    implementation("com.google.http-client:google-http-client-gson:1.23.0"){
        exclude(module="httpclient")
        exclude (group="com.google.guava", module="guava-jdk5")
    }
    implementation("net.sourceforge.tess4j:tess4j:5.4.0")
    implementation("org.apache.pdfbox:pdfbox:2.0.26")

    //Scroll bars
    implementation(libs.composescrollbars)

    // AppMetrica SDK.
    implementation(libs.analytics)
    // AppMetrica Push SDK.
    implementation(libs.push)
    implementation(libs.androidx.legacy.support.v4)
    // Firebase, inimum support version 20.3.0
    implementation(libs.firebase.messaging)
    // Google services
    implementation(libs.play.services.base)

    // AdMob advertisement
    implementation(libs.play.services.ads)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // Logging
    implementation(libs.timber)

    // LangChain4j
    implementation(libs.dev.langchain4j.langchain4j.open.ai)
    implementation(libs.langchain4j)


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
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.android.compiler)

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