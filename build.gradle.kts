// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    repositories {
        google()  // Asegúrate de tener este repositorio
    }
    dependencies {
        classpath ("com.google.gms:google-services:4.3.15")  // La última versión disponible
    }
}