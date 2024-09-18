

// Top-level build file where you can add configuration options common to all sub-projects/modules.

// Secrets Gradle Plugin
//buildscript {
//    dependencies {
//        classpath(libs.secrets.gradle.plugin)
//    }
//}
buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
//    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false

    // Room
    id ("androidx.room") version "2.6.1" apply false
    // Dagger-Hilt
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    // KSP
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    // Serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"

//    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false

}


