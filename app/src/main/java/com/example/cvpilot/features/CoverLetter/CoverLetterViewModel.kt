package com.example.cvpilot.features.CoverLetter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.cvpilot.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import com.example.cvpilot.models.GroqRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.cvpilot.models.GroqMessage
import com.example.cvpilot.models.GroqResponse

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

    // Initialize Ktor client configuration
    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    fun updateJobDescription(newText: String) {
        jobDescription = newText
    }

    fun generateCoverLetter() {
        if (jobDescription.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val systemInstruction = "You are an expert career writer. Generate a tailored, professional cover letter based on the following job requirements."

                val requestBody = GroqRequest(
                    messages = listOf(
                        GroqMessage(role = "system", content = systemInstruction),
                        GroqMessage(role = "user", content = jobDescription)
                    )
                )

                // Execute securely without local hardcoded keys
                val response: Map<String, String> = httpClient.post(
                    "${BuildConfig.SUPABASE_URL}/functions/v1/generate-cover-letter"
                ) {
                    contentType(ContentType.Application.Json)

                    setBody(
                        mapOf(
                            "jobDescription" to jobDescription
                        )
                    )
                }.body()

                val letterResult = response["result"]


                _uiState.update { it.copy(
                    isLoading = false,
                    generatedLetter = letterResult ?: "No response generated."
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to connect to AI engine."
                )}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        httpClient.close() // Close network connections when ViewModel cleans up
    }
}