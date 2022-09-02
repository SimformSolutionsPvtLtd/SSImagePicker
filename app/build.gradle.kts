plugins {
    id(Plugins.ANDROID_APPLICATION)
    kotlin(Plugins.Kotlin.ANDROID)
    kotlin(Plugins.Kotlin.KAPT)
}

android {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = App.ID
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = App.Version.CODE
        versionName = App.Version.NAME
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
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["enableCrashReporting"] = true
        }

        getByName(App.BuildType.DEBUG) {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
    implementation(Dependencies.CORE_KTX)

    // UI
    implementation(Dependencies.APPCOMPAT)
    implementation(Dependencies.CONSTRAINT_LAYOUT)
    implementation(Dependencies.MATERIAL)

    // Jetpack
    implementation(Dependencies.ACTIVITY_KTX)
    implementation(Dependencies.FRAGMENT_KTX)
    implementation(Dependencies.RECYCLER_VIEW)

    // LiveData
    implementation(Dependencies.LIFECYCLE_LIVEDATA)
    implementation(Dependencies.LIFECYCLE_LIVEDATA_KTX)

    // Material
    implementation(Dependencies.MATERIAL)

    // INTUIT DIMEN SSP and SDP
    implementation(Dependencies.INTUIT_SDP)
    implementation(Dependencies.INTUIT_SSP)

    // Unit testing
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.JUNIT_EXT)
    testImplementation(Dependencies.ARCH_CORE_TESTING)

    // UI testing
    androidTestImplementation(Dependencies.TEST_RUNNER)
    androidTestImplementation(Dependencies.JUNIT_EXT)
    androidTestImplementation(Dependencies.TEST_RULES)
    androidTestImplementation(Dependencies.ESPRESSO_CORE)
    androidTestImplementation(Dependencies.ESPRESSO_CONTRIB)

    // Glide
    implementation(Dependencies.GLIDE)
    kapt(Dependencies.GLIDE_COMPILER)

    implementation(project(":imagepickerlibrary"))
}