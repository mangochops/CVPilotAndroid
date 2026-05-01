package com.example.cvpilot.features.Home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvpilot.network.SupabaseManager
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // List to hold file metadata from Supabase
    var resumes by mutableStateOf<List<ResumeData>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        PDFBoxResourceLoader.init(getApplication())
        fetchAndParseResumes()
    }

    fun fetchAndParseResumes() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Fetching files from the 'resumes' bucket
                val bucket = SupabaseManager.client.storage.from("resumes")
                val files = bucket.list()

                resumes = files.map { file ->
                    val summary = if (file.name.endsWith(".pdf")) {
                        extractSummary(file.name)
                    } else {
                        "Non-PDF file"
                    }

                    ResumeData(
                        name = file.name,
                        date = file.createdAt.toString(),
                        content = summary
                    )
                }
            } catch (e: Exception) {
                // Handle error (e.g., logging for Mango Chops Studio dev)
            } finally {
                isLoading = false
            }
        }
    }
    private suspend fun extractSummary(fileName: String): String = withContext(Dispatchers.IO) {
        try {
            // 1. Download bytes from Supabase
            val bytes = SupabaseManager.client.storage.from("resumes").downloadPublic(fileName)

            // 2. Load PDF and strip text
            val document = PDDocument.load(bytes)
            val stripper = PDFTextStripper()
            stripper.sortByPosition = true
            stripper.startPage = 1
            stripper.endPage = 1 // Only parse the first page for the summary

            val fullText = stripper.getText(document)
            document.close()

            // 3. Clean and shorten the text for the card preview
            fullText.take(150).replace("\n", " ").trim() + "..."
        } catch (e: Exception) {
            "Error parsing PDF content"
        }
    }
}

data class ResumeData(val name: String, val date: String, val content: String)