// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}

// Define versions in a central place (optional, can also use version catalog)
// For this example, we'll use a version catalog (libs.versions.toml) which is implicitly created/used.
// If you prefer defining here:
// ext {
//     kotlinVersion = "1.9.22"
//     agpVersion = "8.3.0" // Or your preferred AGP version
//     composeCompilerVersion = "1.5.8" // Aligns with Kotlin 1.9.22
//     // ... other versions
// }

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
} 