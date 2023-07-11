plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("dagger.hilt.android.plugin")
    id("realm-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 33
    namespace = "org.zotero.android"

    defaultConfig {
        applicationId = "org.zotero.android"
        minSdk = 23
        targetSdk = 33
        versionCode = 8
        versionName = "1.10"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_API_URL", "\"https://api.zotero.org\"")
        buildConfigField("boolean", "EVENT_AND_CRASH_LOGGING_ENABLED", "false")
        manifestPlaceholders["enableCrashReporting"] = false
    }
    signingConfigs {
        create("release") {
            storeFile = rootProject.file("zotero.release.keystore")

            if (rootProject.file("keystore-secrets.txt").exists()) {
                val secrets: List<String> = rootProject
                    .file("keystore-secrets.txt")
                    .readLines()
                keyAlias = secrets[0]
                storePassword = secrets[1]
                keyPassword = secrets[2]
            }
        }
    }
    androidResources {
        noCompress ("ttf", "mov", "avi", "json", "html", "csv", "obb")
    }
    buildTypes {
        getByName("debug") {
            resValue("string", "app_name", "Zotero Debug")
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            signingConfigs
                .findByName("debug")
                ?.storeFile = rootProject.file("debug.keystore")

            buildConfigField("boolean", "EVENT_AND_CRASH_LOGGING_ENABLED", "false")
            manifestPlaceholders["enableCrashReporting"] = false
            extra.set("enableCrashlytics", false)
        }
        create("eBeta") {//prefix 'e' added for ordering in Android Studio Build Variant's menu
            resValue("string", "app_name", "Zotero Beta")
            isDebuggable = true
            isMinifyEnabled = false
            signingConfig = signingConfigs.getAt("release")

            buildConfigField("boolean", "EVENT_AND_CRASH_LOGGING_ENABLED", "true")
            manifestPlaceholders["enableCrashReporting"] = true
        }
        getByName("release") {
            resValue("string", "app_name", "Zotero")
            isDebuggable = true
            isMinifyEnabled = false
            signingConfig = signingConfigs.getAt("release")

            buildConfigField("boolean", "EVENT_AND_CRASH_LOGGING_ENABLED", "true")
            manifestPlaceholders["enableCrashReporting"] = true
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
}

dependencies {

    //AndroidX
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")
    implementation("androidx.lifecycle:lifecycle-process:2.6.1")
    implementation("androidx.fragment:fragment-ktx:1.5.7")
    implementation("androidx.activity:activity-ktx:1.7.1")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.constraintlayout:constraintlayout-solver:2.0.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    //Material design
    implementation("com.google.android.material:material:1.9.0")

    //Compose
    implementation("androidx.compose.ui:ui-viewbinding:1.4.3")
    implementation("androidx.compose.ui:ui-util:1.4.3")
    implementation("androidx.compose.ui:ui-tooling:1.4.3")
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.navigation:navigation-compose:2.6.0-rc01")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.compose.foundation:foundation:1.4.3")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.0")

    //Accompanist
    implementation("com.google.accompanist:accompanist-insets:0.23.1")
    implementation("com.google.accompanist:accompanist-placeholder:0.23.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.23.1")
    implementation("com.google.accompanist:accompanist-pager:0.23.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.23.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.23.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.11.0")
    kapt ("com.github.bumptech.glide:compiler:4.11.0")

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.8.21")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    //Dagger + Hilt
    implementation("com.google.dagger:hilt-android:2.43.2")
    implementation("com.google.dagger:dagger:2.43.2")
    kapt("com.google.dagger:dagger-compiler:2.43.2")
    kapt("com.google.dagger:hilt-compiler:2.43.2")

    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    //Crash & Analytics
    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.3.7")
    implementation("com.google.firebase:firebase-analytics-ktx:21.2.2")

    //PSPDFKIT
    implementation("com.pspdfkit:pspdfkit:8.7.2")

    //Retrofit 2
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    //Ok HTTP
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    //GSON
    implementation("com.google.code.gson:gson:2.8.9")

    //ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.1.0-alpha01")
    implementation("androidx.media3:media3-ui:1.1.0-alpha01")

    //Coil
    implementation("io.coil-kt:coil-compose:2.2.2")

    //Other
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("joda-time:joda-time:2.10.9")
    implementation("org.greenrobot:eventbus:3.2.0")

    implementation("commons-io:commons-io:2.4")
    implementation("commons-codec:commons-codec:1.13")
    implementation("commons-validator:commons-validator:1.7")
    implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC3")

    //Unit Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    testImplementation("org.mockito:mockito-core:3.11.0")
    testImplementation("org.mockito:mockito-inline:3.11.0")

    testImplementation("io.mockk:mockk:1.11.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.amshove.kluent:kluent-android:1.72")

    //Instrumented Tests
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}
