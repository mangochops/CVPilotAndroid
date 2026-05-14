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

data class UserProfile(val firstName: String)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
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

    init {
        refreshAll()
    }

    fun refreshAll() {
        fetchUserData()
        fetchRecentGenerations()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            try {
                val user = supabaseClient.auth.currentUserOrNull() ?: return@launch

                val fullName = user.userMetadata?.get("full_name")?.toString()
                    ?: user.userMetadata?.get("name")?.toString()
                    ?: "User"
                val firstName = fullName.split(" ").firstOrNull() ?: "User"
                _userProfile.value = UserProfile(firstName = firstName)

                // 1. Fetch from Profiles
                val profileData = supabaseClient.from("profiles")
                    .select {
                        filter { eq("id", user.id) }
                    }.decodeSingle<Map<String, JsonElement>>()

                // 2. Safely parse credits
                val credits = profileData["credits"]?.jsonPrimitive?.intOrNull ?: 0
                _userCredits.value = credits

            } catch (e: Exception) {
                Log.e("HOME_VM", "Profile Error: ${e.message}")
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