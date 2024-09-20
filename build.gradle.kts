

// Top-level build file where you can add configuration options common to all sub-projects/modules.


buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false


    // Room
    id ("androidx.room") version "2.6.1" apply false
    // Dagger-Hilt
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    // KSP
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    // Serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"


}


