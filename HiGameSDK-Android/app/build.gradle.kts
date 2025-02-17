plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("/Users/neillwu/Documents/HiGameSDKKey")
            storePassword = "123456"
            keyAlias = "higamesdk"
            keyPassword = "123456"
        }
        create("release") {
            storeFile = file("/Users/neillwu/Documents/HiGameSDKKey")
            storePassword = "123456"
            keyAlias = "higamesdk"
            keyPassword = "123456"
        }
    }
    namespace = "com.neil.gamesdk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.neil.gamesdk"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":HiGameSDK-Core"))
    implementation(project(":HiGameSDK-Account"))
    implementation(project(":HiGameSDK-Ad"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}