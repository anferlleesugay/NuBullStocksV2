plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Firebase setup
    kotlin("kapt")
}

android {
    namespace = "com.example.nubullstocksv2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nubullstocksv2"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX dependencies
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Material Components (Keep only the latest version)
    implementation("com.google.android.material:material:1.12.0")

    // Firebase dependencies (Using Firebase BoM for version management)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-database-ktx") // Firebase Database
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication
    implementation("com.google.firebase:firebase-storage-ktx") // Firebase Storage
    implementation("com.google.firebase:firebase-messaging-ktx") // Firebase Messaging

    // Glide for Image Loading (Updated to latest version)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.recyclerview)
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Google Play Services for Authentication
    implementation("com.google.android.gms:play-services-auth:20.2.0")

    // AWS SDK (Updated to latest version)
    implementation("com.amazonaws:aws-android-sdk-core:2.72.0")
    implementation("com.amazonaws:aws-android-sdk-s3:2.72.0")
    implementation("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.72.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    testImplementation("junit:junit:4.13.2")
}

// Firebase plugin to apply the google-services.json file
apply(plugin = "com.google.gms.google-services")
