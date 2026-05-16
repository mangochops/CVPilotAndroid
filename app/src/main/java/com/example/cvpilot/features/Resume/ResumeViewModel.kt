package com.example.cvpilot.features.Resume

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvpilot.models.Resume
import com.example.cvpilot.models.CoverLetter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _resumes = MutableStateFlow<List<Resume>>(emptyList())
    val resumes: StateFlow<List<Resume>> = _resumes.asStateFlow()

    // Added state flow tracking for the premium cover letters view toggle logic
    private val _coverLetters = MutableStateFlow<List<CoverLetter>>(emptyList())
    val coverLetters: StateFlow<List<CoverLetter>> = _coverLetters.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    init {
        fetchLibraryContent()
    }

    // Unified fetch strategy to sync both tables simultaneously on initialization
    fun fetchLibraryContent() {
        fetchResumes()
        fetchCoverLetters()
    }

    fun fetchResumes() {
        viewModelScope.launch {
            try {
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id
                if (currentUserId == null) {
                    Log.e("RESUME_DEBUG", "Cannot fetch resumes: User not authenticated")
                    return@launch
                }

                val result = supabaseClient.from("resumes")
                    .select {
                        filter { eq("user_id", currentUserId) } // Filter matches current account row entries
                        order("created_at", order = Order.DESCENDING)
                    }
                    .decodeList<Resume>()

                _resumes.value = result
                Log.d("RESUME_DEBUG", "Successfully loaded ${result.size} resumes")
            } catch (e: Exception) {
                Log.e("RESUME_DEBUG", "Error fetching resumes: ${e.message}")
            }
        }
    }

    fun fetchCoverLetters() {
        viewModelScope.launch {
            try {
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id
                if (currentUserId == null) {
                    Log.e("RESUME_DEBUG", "Cannot fetch cover letters: User not authenticated")
                    return@launch
                }

                val result = supabaseClient.from("cover_letters")
                    .select {
                        filter { eq("user_id", currentUserId) }
                        order("created_at", order = Order.DESCENDING)
                    }
                    .decodeList<CoverLetter>()

                _coverLetters.value = result
                Log.d("RESUME_DEBUG", "Successfully loaded ${result.size} cover letters")
            } catch (e: Exception) {
                Log.e("RESUME_DEBUG", "Error fetching cover letters: ${e.message}")
            }
        }
    }

    fun uploadResumeToSupabase(uri: Uri) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id
                    ?: throw Exception("User not authenticated")

                val bytes = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                } ?: throw Exception("Failed to read file")

                val fileName = "resume_${System.currentTimeMillis()}.pdf"
                val bucket = supabaseClient.storage.from("resumes")

                // Upload raw binary data file to storage buckets
                bucket.upload(path = fileName, data = bytes)
                val publicUrl = bucket.publicUrl(fileName)

                // Optional: If you want to parse text here too, you can call your readPdfContent helper.
                // Otherwise, we pass an empty JSON structure for now to keep jsonb happy.
                val emptyContentJson = buildJsonObject { put("text", "") }

                // 🚀 Create the database record explicitly using the standard schema properties
                val newResume = Resume(
                    userId = currentUserId,
                    title = "Uploaded Resume",
                    fileUrl = publicUrl,
                    name = fileName,
                    content = emptyContentJson
                )

                supabaseClient.from("resumes").insert(newResume)

                // Refresh list instantly to display changes on screen!
                fetchResumes()
            } catch (e: Exception) {
                Log.e("Upload", "Error during library asset push: ${e.message}")
            } finally {
                _isUploading.value = false
            }
        }
    }
}