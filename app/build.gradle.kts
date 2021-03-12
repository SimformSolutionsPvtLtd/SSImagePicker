plugins {
    id(Plugins.ANDROID_APPLICATION)
    kotlin(Plugins.Kotlin.ANDROID)
    kotlin(Plugins.Kotlin.ANDROID_EXTENSIONS)
    kotlin(Plugins.Kotlin.KAPT)
    id(Plugins.KOTLIN_ANDROID)
}

android {
    compileSdkVersion(Versions.COMPILE_SDK)
    buildToolsVersion(Versions.BUILD_TOOLS)

    defaultConfig {
        applicationId = App.ID
        minSdkVersion(Versions.MIN_SDK)
        targetSdkVersion(Versions.TARGET_SDK)
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

    flavorDimensions(App.Dimension.DEFAULT)

    productFlavors {
        create(App.Flavor.DEV) {
            applicationId = App.ID
        }

        create(App.Flavor.QA) {

        }

        create(App.Flavor.PRODUCTION) {

        }
    }

    variantFilter {
        val name = this.flavors[0].name

        @SuppressWarnings("ComplexCondition")
        if ((this.buildType.name == App.BuildType.RELEASE && name.contains(App.Flavor.DEV)) || (this.buildType.name == App.BuildType.DEBUG && name.contains(App.Flavor.QA))) {
            ignore = true
        }
    }

    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
}