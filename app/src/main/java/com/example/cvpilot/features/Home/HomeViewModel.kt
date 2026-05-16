package com.example.cvpilot.features.Home

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvpilot.models.Resume
import com.example.cvpilot.models.CoverLetter
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.buildJsonObject // Ensure this is imported
import kotlinx.serialization.json.put // CRITICAL: This is needed for buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.example.cvpilot.network.UserResume
import kotlinx.coroutines.flow.StateFlow
import android.net.Uri
import kotlin.time.Instant
import java.io.InputStream
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.util.UUID
import com.tom_roush.pdfbox.text.PDFTextStripper
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.collectLatest


data class UserProfile(val firstName: String)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var recentResumes by mutableStateOf<List<Resume>>(emptyList())
    var recentCoverLetters by mutableStateOf<List<CoverLetter>>(emptyList())
    var isLoading by mutableStateOf(false)
        private set

    var selectedResumeFullText by mutableStateOf("")
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _userCredits = MutableStateFlow(0)
    val userCredits = _userCredits.asStateFlow()

    private val _showPaywall = MutableStateFlow(false)
    val showPaywall = _showPaywall.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    init {
        refreshAll()
    }

    fun refreshAll() {
        fetchUserData()
        fetchRecentGenerations()
    }

    fun uploadResumeToSupabase(uri: Uri) {
        viewModelScope.launch {
            _isUploading.value = true
            try {

                // Your upload logic here (copy from ResumeViewModel)
                // e.g., read PDF bytes, extract text, save to Supabase
                val rawPdfText = readPdfContent(uri) // Implement this

                val jsonbContent = buildJsonObject {
                    put("text", rawPdfText)
                }

                val fallbackName = userProfile.value?.firstName ?: "User"

                supabaseClient.from("resumes").insert(
                    UserResume(
                        user_id = getCurrentUserId(),
                        title = "Primary Resume",
                        name = fallbackName,
                        content = jsonbContent, // Passes verified jsonb data
                        id = null,              // Supabase will automatically assign gen_random_uuid()
                        created_at = null,      // Supabase will automatically assign now()
                        file_url = null
                    )
                )
                fetchRecentGenerations() // Refresh the list
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Upload failed", e)
            } finally {
                _isUploading.value = false
            }
        }
    }
    // ADD THIS: Read PDF text from Uri
    private fun readPdfContent(uri: Uri): String {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                PDDocument.load(stream).use { document ->
                    PDFTextStripper().getText(document)
                }
            } ?: ""
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to read PDF", e)
            ""
        }
    }

    // ADD THIS: Generate unique ID
    private fun generateId(): String = UUID.randomUUID().toString()

    // ADD THIS: Get current user ID from Supabase auth
    private suspend fun getCurrentUserId(): String {
        return supabaseClient.auth.currentUserOrNull()?.id ?: ""
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            Log.d("HOME_VM", "Starting active session monitoring...")

            // 🚀 FIX: Instead of checking a snapshot once, reactively listen to session changes
            supabaseClient.auth.sessionStatus.collectLatest { status ->
                if (status is SessionStatus.Authenticated) {
                    val user = status.session.user

                    // 1. SET INITIAL DATA FROM METADATA IMMEDIATELY
                    val metaName = user?.userMetadata?.get("full_name")?.jsonPrimitive?.content
                        ?: user?.userMetadata?.get("name")?.jsonPrimitive?.content
                        ?: "User"

                    Log.d("HOME_VM", "Metadata name found: $metaName")
                    val initialFirstName = metaName.split(" ").firstOrNull() ?: "User"

                    // Strip out any accidental residual JSON string artifacts
                    val cleanFirstName = initialFirstName.replace("\"", "")
                    _userProfile.value = UserProfile(firstName = cleanFirstName)

                    // 2. FETCH FROM PROFILES TABLE CLEANLY
                    try {
                        Log.d("HOME_VM", "Querying 'profiles' table for ID: ${user?.id}")
                        val profileData = supabaseClient.from("profiles")
                            .select {
                                filter {
                                    eq("id", user?.id ?: "" )
                                }
                            }.decodeSingleOrNull<Map<String, JsonElement>>()

                        if (profileData != null) {
                            Log.d("HOME_VM", "Profile data received: $profileData")

                            val dbName = profileData["full_name"]?.jsonPrimitive?.content
                            if (!dbName.isNullOrBlank()) {
                                val dbFirstName = dbName.replace("\"", "").split(" ").firstOrNull() ?: "User"
                                _userProfile.value = UserProfile(firstName = dbFirstName)
                            }

                            val credits = profileData["credits"]?.jsonPrimitive?.intOrNull ?: 0
                            _userCredits.value = credits
                        }
                    } catch (e: Exception) {
                        Log.e("HOME_VM", "Database Profile fetch failed: ${e.message}")
                    }
                } else if (status is SessionStatus.NotAuthenticated) {
                    Log.d("HOME_VM", "User is not logged in.")
                    _userProfile.value = UserProfile(firstName = "User")
                }
            }
        }
    }

    fun fetchRecentGenerations() {
        viewModelScope.launch {
            isLoading = true
            try {
                recentResumes = supabaseClient.from("resumes").select {
                    order("created_at", order = Order.DESCENDING)
                    limit(5)
                }.decodeList<Resume>()

                recentCoverLetters = supabaseClient.from("cover_letters").select {
                    order("created_at", order = Order.DESCENDING)
                    limit(5)
                }.decodeList<CoverLetter>()
            } catch (e: Exception) {
                Log.e("HOME_VM", "Fetch Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }



    fun triggerPaywall() { _showPaywall.value = true }
    fun dismissPaywall() { _showPaywall.value = false }
}