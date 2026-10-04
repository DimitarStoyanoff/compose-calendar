import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.dimitarstoyanoff.calendar"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        // Added as fix to https://github.com/gradle-nexus/publish-plugin/issues/208
        publishing {
            singleVariant("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_3
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    // Androidx
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)

    // Testing
    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // Androidx Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)
    androidTestImplementation(libs.androidx.compose.ui.test)
}

ext {
    set("PUBLISH_GROUP_ID", "io.github.dimitarstoyanoff")
    set("PUBLISH_ARTIFACT_ID", "calendar")
    set("PUBLISH_VERSION", "0.0.4")
}

apply("${rootProject.projectDir}/scripts/publish-module.gradle")