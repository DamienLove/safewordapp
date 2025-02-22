// Root-level build.gradle.kts

// Plugins
plugins {
    id("com.android.application") version "8.8.1" apply false
    id("com.android.library") version "8.8.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

// Clean Task
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
