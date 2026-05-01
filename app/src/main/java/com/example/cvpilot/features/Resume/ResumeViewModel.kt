package com.example.cvpilot.features.Resume

import kotlinx.coroutines.flow.MutableStateFlow
import android.net.Uri
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.emptyList
import kotlinx.coroutines.launch
import com.example.cvpilot.models.Resume
import io.github.jan.supabase.storage.storage
import com.example.cvpilot.network.SupabaseManager
import androidx.lifecycle.AndroidViewModel
import android.app.Application

class ResumeViewModel(application: Application) : AndroidViewModel(application) {
    private val _resumes = MutableStateFlow<List<Resume>>(emptyList())
    val resumes: StateFlow<List<Resume>> = _resumes

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    fun uploadResumeToSupabase(uri: Uri) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                // 1. Get ByteData from Uri
                val bytes = getApplication<Application>().contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                } ?: throw Exception("Failed to read file")

                val fileName = "resume_${System.currentTimeMillis()}.pdf"

                // 2. Upload to Supabase Storage
                // Assumes you have a bucket named "resumes"
                val bucket = SupabaseManager.client.storage.from("resumes")
                bucket.upload(path = fileName, data = bytes) {
                    upsert = false
                }

                // 3. Save reference to local database (SwiftData equivalent)
                val publicUrl = bucket.publicUrl(fileName)
                val newResume = Resume(
                    title = "Uploaded Resume",
                    name = fileName,
                    publicUrl = publicUrl
                )
                _resumes.value = _resumes.value + newResume

            } catch (e: Exception) {
                Log.e("Upload", "Error: ${e.message}")
            } finally {
                _isUploading.value = false
            }
        }
    }
}