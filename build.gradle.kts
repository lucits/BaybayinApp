// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        // Add other repositories as needed
    }
    dependencies {
        // Add the classpath for the Google services Gradle plugin
        classpath("com.android.tools.build:gradle:8.3.2")
        classpath("com.google.gms:google-services:4.4.1")
        // Add other dependencies as needed
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
}