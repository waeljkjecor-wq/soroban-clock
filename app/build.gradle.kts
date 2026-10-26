plugins {
    id("com.android.application")
}

android {
    namespace = "com.soroban.clock"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.soroban.clock"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
