plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.projek"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projek"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

 implementation(libs.appcompat.v161)
 implementation(libs.material)
 implementation(libs.constraintlayout.v214)
 implementation(libs.recyclerview)
 implementation(libs.cardview)
 implementation(libs.glide) // Untuk memuat gambar
 annotationProcessor(libs.compiler)

 // Retrofit & GSON for Networking
 implementation(libs.retrofit)
 implementation(libs.converter.gson)
 implementation(libs.okhttp)
 implementation(libs.logging.interceptor)

 // Room for Local Database
 implementation(libs.room.runtime)
 annotationProcessor(libs.room.compiler)

 // Lifecycle components (ViewModel, LiveData)
 implementation(libs.lifecycle.extensions)
 implementation(libs.lifecycle.viewmodel.ktx)
 implementation(libs.lifecycle.livedata.ktx)
 implementation(libs.lifecycle.common.java8)

 // Navigation Component
 implementation(libs.navigation.fragment.v277)
 implementation(libs.navigation.ui.v277)

 testImplementation(libs.junit)
 androidTestImplementation(libs.junit.v115)
 androidTestImplementation(libs.espresso.core.v351)
}