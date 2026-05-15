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
                val content = readPdfContent(uri) // Implement this
                supabaseClient.from("resumes").upsert(
                    UserResume(
                        id = generateId(),
                        user_id = getCurrentUserId(),
                        title = "Primary Resume",
                        name = userProfile.value?.firstName ?: "User",
                        content = content,
                        template_id = null,  // ADD THIS (was missing)
                        created_at = System.currentTimeMillis().toString()
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
            Log.d("HOME_VM", "Starting fetchUserData...")
            try {
                val user = supabaseClient.auth.currentUserOrNull() ?: return@launch

                // 1. SET INITIAL DATA FROM METADATA IMMEDIATELY
                // This ensures the UI updates even if the database is slow or fails
                val metaName = user.userMetadata?.get("full_name")?.jsonPrimitive?.content
                    ?: user.userMetadata?.get("name")?.jsonPrimitive?.content
                    ?: "User"

                Log.d("HOME_VM", "Metadata name found: $metaName")

                val initialFirstName = metaName.split(" ").firstOrNull() ?: "User"
                _userProfile.value = UserProfile(firstName = initialFirstName)
                Log.d("HOME_VM", "State updated with initial name: $initialFirstName")

                // 2. FETCH FROM PROFILES TABLE (WRAP IN A NESTED TRY)
                try {
                    Log.d("HOME_VM", "Querying 'profiles' table for ID: ${user.id}")
                    val profileData = supabaseClient.from("profiles")
                        .select { filter { eq("id", user.id) } }
                        .decodeSingleOrNull<Map<String, JsonElement>>()

                    if (profileData != null) {
                        Log.d("HOME_VM", "Profile data received: $profileData")

                        // Update Name from DB if it exists
                        val dbName = profileData["full_name"]?.jsonPrimitive?.content
                        if (!dbName.isNullOrBlank()) {
                            val dbFirstName = dbName.split(" ").firstOrNull() ?: "User"
                            _userProfile.value = UserProfile(firstName = dbFirstName)
                        }

                        // Update Credits
                        val credits = profileData["credits"]?.jsonPrimitive?.intOrNull ?: 0
                        _userCredits.value = credits
                        Log.d("HOME_VM", "Credits State updated: $credits")
                    }
                } catch (e: Exception) {
                    Log.e("HOME_VM", "Database Profile fetch failed: ${e.message}")
                }

            } catch (e: Exception) {
                Log.e("HOME_VM", "General Profile Error: ${e.message}")
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