plugins {
    id("com.android.application") // version from settings.gradle.kts
    kotlin("android")             // version from settings.gradle.kts
}

android {
    namespace = "com.safeword"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.safeword"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        // This automatically brings in ViewBinding from the AGP, no separate library needed
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Just the standard Kotlin libs. No direct “viewbinding” library needed.
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")

    // Example AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    // ...
}
