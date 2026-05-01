package com.example.cvpilot.features.CoverLetter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class CoverLetterUiState(
    val isLoading: Boolean = false,
    val generatedLetter: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CoverLetterViewModel @Inject constructor() : ViewModel() {

    var jobDescription by mutableStateOf("")
        private set

    private val _uiState = MutableStateFlow(CoverLetterUiState())
    val uiState = _uiState.asStateFlow()

    fun updateJobDescription(newText: String) {
        jobDescription = newText
    }

    fun generateCoverLetter() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // TODO: Wire up your LLM API call here
                // val result = llmRepository.generate(jobDescription)

                _uiState.update { it.copy(
                    isLoading = false,
                    generatedLetter = "Generated content would go here"
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}