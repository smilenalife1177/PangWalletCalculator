plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "tw.smilenalife.pangwallet"
    compileSdk = 35

    defaultConfig {
        applicationId = "tw.smilenalife.pangwallet"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
    }
}

kotlin {
    jvmToolchain(17)
}
