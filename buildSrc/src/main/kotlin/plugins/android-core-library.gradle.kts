package plugins
import AppConfig
import dependencies.*

plugins {
    id("com.android.library")
    id ("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id ("dagger.hilt.android.plugin")
}

android{
    compileSdk = AppConfig.compileSdkVersion
    defaultConfig {
        minSdk = AppConfig.minSdkVersion
        targetSdk = AppConfig.targetSdkVersion
        testInstrumentationRunner = AppConfig.testRunner
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables{
            useSupportLibrary = true
        }
        // Add 16 KB page size alignment for native libraries
        externalNativeBuild {
            cmake {
                arguments(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM_LEVEL=android-21",
                    "-DANDROID_MM_16KB_PAGESIZE_COMPATIBILITY=ON"
                )
            }
        }
    }
    // For libraries included as prebuilt binaries
    packagingOptions {
        jniLibs {
            useLegacyPackaging = false
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("String", "BASE_URL", "\"http://192.168.1.3:8080/api/\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("String", "BASE_URL", "\"https://smpsantoyosefduri.sch.id/api/\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = AppConfig.kotlinCompilerExtensionVersion
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies{
    addDataStoreDependencies()
    addAndroLifeCycleDependencies()
    addCoroutinesAndroidDependencies()
    addHiltDependencies()
    addNetworkDependencies()
    addAndroidTestsDependencies()
    addRoomDependencies()
    addChuckerDebugDependencies()
    addChuckerReleaseDependencies()
}