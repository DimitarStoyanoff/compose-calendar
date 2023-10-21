// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.gradle.nexus)
    alias(libs.plugins.dokka) apply false
}

apply("${rootDir}/scripts/publish-root.gradle")