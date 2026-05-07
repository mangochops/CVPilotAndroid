// Top-level build file
plugins {
    alias(deps.plugins.android.application) apply false
    alias(deps.plugins.kotlin.compose) apply false
    alias(deps.plugins.hilt.android) apply false
    alias(deps.plugins.google.devtools.ksp) apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}