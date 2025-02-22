plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.safeword"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.safeword"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
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
    // Kotlin standard library
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.25"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.25")

    // AndroidX core and AppCompat
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Material Design components
    implementation("com.google.android.material:material:1.12.0")

    // Jetpack Compose dependencies
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.3")

    // Compose Tooling
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation(libs.firebase.crashlytics)
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")
    implementation("androidx.databinding:databinding-runtime:8.6.0")
    // ✅ Google Generative AI Client
}
