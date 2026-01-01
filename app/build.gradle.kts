import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use(localProperties::load)
}

fun getProp(key: String): String {
    return (project.findProperty(key) as String?)
        ?: localProperties.getProperty(key)
        ?: ""
}

val supabaseUrl = getProp("SUPABASE_URL")
val supabaseAnonKey = getProp("SUPABASE_ANON_KEY")
val supabaseBucket = getProp("SUPABASE_STORAGE_BUCKET").ifBlank { "images" }

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    //id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.app.beloz"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.beloz"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        fun configString(value: String): String = "\"${value.replace("\"", "\\\"")}\""
        buildConfigField("String", "SUPABASE_URL", configString(supabaseUrl))
        buildConfigField("String", "SUPABASE_ANON_KEY", configString(supabaseAnonKey))
        buildConfigField("String", "SUPABASE_STORAGE_BUCKET", configString(supabaseBucket))

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
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // 1. Para la IA de escaneo de ingredientes (ML Kit - Gratis y Local)
    implementation ("com.google.mlkit:object-detection:17.0.0")

    // 2. Para detectar si el usuario camina o va en coche (Awareness API)
    implementation ("com.google.android.gms:play-services-awareness:19.0.1")

    implementation ("androidx.compose.runtime:runtime-livedata:1.7.4")



    implementation(platform("androidx.compose:compose-bom:2023.08.00"))

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")


    implementation ("com.google.dagger:hilt-android:2.44")
    //kapt("com.google.dagger:hilt-compiler:2.44")
    //implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    implementation("androidx.navigation:navigation-compose:2.8.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")


    implementation ("org.osmdroid:osmdroid-android:6.1.10")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}
