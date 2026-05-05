package com.example.cvpilot.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from // Use . not -
import io.github.jan.supabase.auth.Auth // Use . not -
import io.github.jan.supabase.auth.auth // Use . not -
import kotlinx.serialization.Serializable
import java.util.Date
import com.example.cvpilot.BuildConfig

val client = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
){
    install(Postgrest)
    install(Auth)
    install(io.github.jan.supabase.storage.Storage)
}

// 1. Define your Data Model for Supabase
@Serializable
data class UserResume(
    val id: String, // Or UUID depending on your DB schema
    val user_id: String,
    val title: String,
    val name: String,
    val content: String,
    val template_id: String?,
    val file_url: String? = null,
    val created_at: String // ISO 8601 string is best for Supabase
)

object SupabaseManager {

    private const val URL = "https://eocldmwhgovgdhuttwgs.supabase.co"
    private const val ANON_KEY = "sb_publishable_25DHcb2BpSRJWVNtNWWETg_LzHFVppL" // Use your full key here

    val client = createSupabaseClient(
        supabaseUrl = URL,
        supabaseKey = ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(io.github.jan.supabase.storage.Storage)
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