package com.example.cvpilot.features.Home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvpilot.models.Resume // Use your standard Resume model
import com.example.cvpilot.network.SupabaseManager
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Now uses the database model 'Resume' instead of 'ResumeData'
    var recentResumes by mutableStateOf<List<Resume>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    var selectedResumeFullText by mutableStateOf("")

    private val _showPaywall = MutableStateFlow(false)
    val showPaywall = _showPaywall.asStateFlow()

    init {
        fetchRecentGenerations()
    }

    fun fetchRecentGenerations() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Query DB table directly - much faster than storage listing
                recentResumes = SupabaseManager.client.from("resumes")
                    .select {
                        order("created_at", order = Order.DESCENDING)
                        limit(5)
                    }
                    .decodeList<Resume>()
            } catch (e: Exception) {
                Log.e("HOME_VM", "Error fetching from DB: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun triggerPaywall() = viewModelScope.launch { _showPaywall.value = true }
    fun dismissPaywall() = viewModelScope.launch { _showPaywall.value = false }
}