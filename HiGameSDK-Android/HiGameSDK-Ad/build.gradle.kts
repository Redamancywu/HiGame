plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.neil.higamesdk.ad"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation ("com.android.support:appcompat-v7:28.0.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(files("libs/mediation_admob_adapter_17.2.0.65.aar"))
    implementation(files("libs/mediation_baidu_adapter_9.37.3.aar"))
    implementation(files("libs/mediation_gdt_adapter_4.610.1480.0.aar"))
    implementation(files("libs/mediation_ks_adapter_3.3.69.3.aar"))
    implementation(files("libs/mediation_mintegral_adapter_16.6.57.8.aar"))
    implementation(files("libs/mediation_sigmob_adapter_4.19.5.0.aar"))
    implementation(files("libs/mediation_unity_adapter_4.3.0.32.aar"))
    implementation(files("libs/open_ad_sdk_6.6.0.7.aar"))
    implementation(project(":HiGameSDK-Core"))
    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
}