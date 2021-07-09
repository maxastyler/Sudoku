plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
//    kotlin("jvm") version "1.5.20" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "1.5.10"
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    packagingOptions {
        // for JNA and JNA-platform
//        jniLibs.excludes.add("META-INF/AL2.0")
//        jniLibs.excludes.add("META-INF/LGPL2.1")
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }

    defaultConfig {
        applicationId = "com.maxtyler.sudoku"
        minSdk = 26
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["compose_version"] as String
        kotlinCompilerVersion = "1.4.32"
    }
}

dependencies {

    // hilt stuff
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hilt_version"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hilt_version"]}")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha03")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha04")

    // room stuff
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    annotationProcessor("androidx.room:room-compiler:${rootProject.extra["room_version"]}")

    // serialisation
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    // paging
    implementation("androidx.paging:paging-runtime:${rootProject.extra["paging_version"]}")
    implementation("androidx.paging:paging-compose:1.0.0-alpha11")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")
    testImplementation("androidx.room:room-testing:${rootProject.extra["room_version"]}")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${rootProject.extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${rootProject.extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${rootProject.extra["lifecycle_version"]}")
    kapt("androidx.lifecycle:lifecycle-compiler:${rootProject.extra["lifecycle_version"]}")

    implementation("androidx.activity:activity-compose:1.3.0-rc01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_version"]}")
}