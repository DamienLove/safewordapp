// settings.gradle.kts (top-level in your project root)
pluginManagement {
    repositories {
        // Add Google and MavenCentral so we can resolve Android Gradle Plugin
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // Declare the plugin IDs and versions here
        id("com.android.application") version "8.1.0"
        kotlin("android") version "1.8.22"
        // Add more plugins as needed
    }
}

// Name of the project
rootProject.name = "SafewordApp"
// The modules you want to include
include(":app")
