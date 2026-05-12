package com.example.cvpilot.features.Resume

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvpilot.models.Resume
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

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient,
    @ApplicationContext private val context: Context // Hilt provides this automatically
) : ViewModel() {

    private val _resumes = MutableStateFlow<List<Resume>>(emptyList())
    val resumes: StateFlow<List<Resume>> = _resumes.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    init {
        fetchResumes()
    }

    fun fetchResumes() {
        viewModelScope.launch {
            try {
                val result = supabaseClient.from("resumes")
                    .select {
                        order("created_at", order = Order.DESCENDING)
                    }
                    .decodeList<Resume>()

                _resumes.value = result
            } catch (e: Exception) {
                Log.e("RESUME_DEBUG", "Error fetching: ${e.message}")
            }
        }
    }

    fun uploadResumeToSupabase(uri: Uri) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                // Access contentResolver via the injected context
                val bytes = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                } ?: throw Exception("Failed to read file")

                val fileName = "resume_${System.currentTimeMillis()}.pdf"
                val bucket = supabaseClient.storage.from("resumes")

                bucket.upload(path = fileName, data = bytes)

                val publicUrl = bucket.publicUrl(fileName)

                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id
                    ?: throw Exception("User not authenticated")

                // 4. Create the Database Record
                // Note: Ensure your Resume model matches the DB columns (user_id, file_url, etc.)
                val newResume = Resume(
                    userId = currentUserId,
                    title = "My Uploaded Resume",
                    fileUrl = publicUrl,
                    name = fileName, // Fixed: Added missing 'name' parameter
                    content = ""     // Fixed: Use empty string instead of null if needed
                )

                supabaseClient.from("resumes").insert(newResume)

                fetchResumes()
            } catch (e: Exception) {
                Log.e("Upload", "Error: ${e.message}")
            } finally {
                _isUploading.value = false
            }
        }
    }
}