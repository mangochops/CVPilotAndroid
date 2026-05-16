package com.example.cvpilot.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from // Use . not -
import io.github.jan.supabase.auth.Auth // Use . not -
import io.github.jan.supabase.auth.auth // Use . not -
import kotlinx.serialization.Serializable
import java.util.Date
import com.example.cvpilot.BuildConfig
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.github.jan.supabase.auth.SettingsSessionManager
import kotlinx.serialization.json.JsonElement


// 1. Define your Data Model for Supabase
@Serializable
data class UserResume(
    val id: String? = null, // Or UUID depending on your DB schema
    val user_id: String,
    val title: String = "Primary Resume",
    val name: String,
    val content: JsonElement,
//    val template_id: String?,
    val file_url: String? = null,
    val created_at: String? = null // ISO 8601 string is best for Supabase
)

object SupabaseManager {

    private const val URL = BuildConfig.SUPABASE_URL
    private const val ANON_KEY = BuildConfig.SUPABASE_ANON_KEY // Use your full key here

    @OptIn(SupabaseInternal::class)
    val client = createSupabaseClient(
        supabaseUrl = URL,
        supabaseKey = ANON_KEY
    ) {
        install(Postgrest)
        install(Auth){
            // ADD THIS LINE: This persists the user session in SharedPreferences
            sessionManager = SettingsSessionManager()
        }
        install(io.github.jan.supabase.storage.Storage)
        install(Functions)

        // Configure Ktor HTTP client with ContentNegotiation
        httpConfig {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    encodeDefaults = true
                })
            }
        }

        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            encodeDefaults = true
        })
    }

    /**
     * Syncs a local Resume to Supabase
     * Replaces the SwiftUI syncResume function
     */
    suspend fun syncResume(resume: UserResume) {
        try {
            // 1. Get current user ID from Auth module
            val userId = client.auth.currentUserOrNull()?.id

            if (userId == null) {
                println("❌ No logged-in user found")
                return
            }

            // 2. Perform the Upsert
            // In Kotlin, the model must be @Serializable
            client.from("resumes").upsert(resume)

            println("✅ Successfully synced resume: ${resume.title}")

        } catch (e: Exception) {
            println("❌ Sync failed: ${e.message}")
            throw e
        }
    }
}