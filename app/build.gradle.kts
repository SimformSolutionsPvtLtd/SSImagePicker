plugins {
    id(Plugins.ANDROID_APPLICATION)
    kotlin(Plugins.Kotlin.ANDROID)
    kotlin(Plugins.Kotlin.KAPT)
    id(Plugins.Kotlin.PARCELIZE)
}

android {
    compileSdk = libs.versions.compile.sdk.get().toInt()
    namespace = App.ID
    defaultConfig {
        applicationId = App.ID
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = libs.versions.code.get().toInt()
        versionName = libs.versions.name.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = App.MULTI_DEX
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.incremental"] = "true"
            }
        }
    }

    buildTypes {
        getByName(App.BuildType.RELEASE) {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            manifestPlaceholders["enableCrashReporting"] = true
        }

        getByName(App.BuildType.DEBUG) {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            manifestPlaceholders["enableCrashReporting"] = false
        }
    }

    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}


dependencies {
    implementation(defaultFileTree())

    // Core
    implementation(libs.androidx.core.ktx)

    // UI
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)

    // Jetpack
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.lifecycle.livedata.ktx)

    // Material
    implementation(libs.material)

    // INTUIT DIMEN SSP and SDP
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.junit)

    // UI testing
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit.v111)
    androidTestImplementation(libs.rules)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.contrib)

    // Glide
    implementation(libs.glide)
    kapt(libs.compiler)

    implementation(project(":imagepickerlibrary"))
}