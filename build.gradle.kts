// Top-level build file where you can add configuration options common to all sub-projects/modules.
// build.gradle.kts (PROJECT — raiz do projeto, não do app)
// Arquivo localizado em: Movie2Night/build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}
