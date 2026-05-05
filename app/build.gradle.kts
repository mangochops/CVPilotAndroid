import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "com.example.cvpilot"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cvpilot"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        val supabaseUrl = localProperties.getProperty("SUPABASE_URL") ?: ""
        val supabaseKey = localProperties.getProperty("SUPABASE_ANON_KEY") ?: ""

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseKey\"")

        // This injects a safe string property directly into a custom field
        buildConfigField("String", "GROQ_API_KEY", "\"${project.properties["GROQ_API_KEY"] ?: ""}\"")
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

    buildFeatures {
        // Enforce generation of the BuildConfig class
        buildConfig = true
        compose = true
    }

    // No more kotlinOptions block here
}

dependencies {

    implementation("com.revenuecat.purchases:purchases:8.4.0")
    implementation("com.revenuecat.purchases:purchases-ui:8.4.0")

    implementation("io.ktor:ktor-client-android:3.0.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.1")



    implementation("com.google.dagger:hilt-android:2.59")
    annotationProcessor("com.google.dagger:hilt-android-compiler:2.59")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Supabase & others
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.1")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.0.1")
    implementation("io.github.jan-tennert.supabase:realtime-kt:3.0.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:3.0.1")
    implementation("io.ktor:ktor-client-android:3.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

