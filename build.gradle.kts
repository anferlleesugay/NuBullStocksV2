plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://aws-sdk-ios.s3.amazonaws.com/android/release/") } // AWS SDK repository
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.0") // Updated Firebase services plugin
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9") // Updated Firebase Crashlytics plugin
        classpath("com.android.tools.build:gradle:8.1.2") // Updated Android Gradle plugin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // Kotlin Gradle plugin
    }
}