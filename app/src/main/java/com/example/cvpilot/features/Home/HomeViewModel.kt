package com.example.cvpilot.features.Home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvpilot.network.SupabaseManager
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var resumes by mutableStateOf<List<ResumeData>>(emptyList())
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    // This holds the full text of the resume currently being "Targeted"
    var selectedResumeFullText by mutableStateOf("")
    var isExtractingFullText by mutableStateOf(false)

    init {
        PDFBoxResourceLoader.init(getApplication())
        fetchAndParseResumes()
    }

    private val _showPaywall = MutableStateFlow(false)
    val showPaywall = _showPaywall.asStateFlow()

    fun triggerPaywall() {
        _showPaywall.value = true
    }

    fun dismissPaywall() {
        _showPaywall.value = false
    }

    // Optional: Logic to refresh credits after a successful purchase
    fun onPaywallSuccess() {
        _showPaywall.value = false
        // refreshUserCredits()
    }
    fun fetchAndParseResumes() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val bucket = SupabaseManager.client.storage.from("resumes")
                val files = bucket.list()

                resumes = files.map { file ->
                    val summary = if (file.name.lowercase().endsWith(".pdf")) {
                        extractSummary(file.name)
                    } else {
                        "Preview unavailable"
                    }

                    ResumeData(
                        name = file.name,
                        date = file.createdAt.toString().split("T")[0], // Cleaner date
                        content = summary
                    )
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load resumes: ${e.localizedMessage}"
                Log.e("HOME_VM", "Error fetching resumes", e)
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun extractSummary(fileName: String): String = withContext(Dispatchers.IO) {
        try {
            val bytes = SupabaseManager.client.storage.from("resumes").downloadPublic(fileName)
            PDDocument.load(bytes).use { document ->
                val stripper = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                    sortByPosition = true
                }
                val text = stripper.getText(document)
                text.take(150).replace("\n", " ").trim() + "..."
            }
        } catch (e: Exception) {
            "No preview available"
        }
    }

    /**
     * Call this when the user clicks on a resume card to prepare it for tweaking.
     */
    fun selectResumeAndExtractText(fileName: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            isExtractingFullText = true
            try {
                val text = withContext(Dispatchers.IO) {
                    val bytes = SupabaseManager.client.storage.from("resumes").downloadPublic(fileName)
                    PDDocument.load(bytes).use { document ->
                        PDFTextStripper().getText(document)
                    }
                }
                selectedResumeFullText = text
                onComplete() // Callback to navigate to UploadJobAdView
            } catch (e: Exception) {
                errorMessage = "Could not read resume content."
                Log.e("HOME_VM", "Full text extraction failed", e)
            } finally {
                isExtractingFullText = false
            }
        }
    }
}

data class ResumeData(
    val name: String,
    val date: String,
    val content: String
)