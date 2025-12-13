java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.restro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.restro"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            isShrinkResources = false
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

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    defaultConfig {
        multiDexEnabled = true
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.ads.identifier)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.work.runtime.ktx)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //EasyPermissions-ktx
    implementation(libs.easypermissions.ktx)
    //Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.scalars)

    // Hilt dependencies
    implementation(libs.hilt.android.v2562)
    ksp(libs.hilt.android.compiler)

    //Reflection
    implementation(libs.kotlin.reflect)
    //SwipeRefreshlayout
    implementation(libs.androidx.swiperefreshlayout)
    //chuckerInterceptor
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)

    //dataStore
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.datastore.preferences)
    // Timber
    implementation(libs.timber)
    //Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation("androidx.room:room-paging:2.8.4")

    //CustomActivityOnCrash
    implementation(libs.customactivityoncrash)

    // joda time
    implementation(libs.android.joda)
    //sdp
    implementation(libs.sdp.android)
    //ssp
    implementation(libs.ssp.android)

    // library for Play In-App Update:
    implementation(libs.app.update.ktx)

    //lottie animation
    implementation(libs.lottie)

    // socket.io
    implementation("io.socket:socket.io-client:2.0.1") {
        exclude(group = "org.json", module = "json")
    }
    // paging 3
    implementation(libs.androidx.paging.runtime)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}