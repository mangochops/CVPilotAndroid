package com.example.cvpilot.features.CoverLetter

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvpilot.paywall.RevenueCatManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

data class CoverLetterUiState(
    val isLoading: Boolean = false,
    val generatedLetter: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class CoverLetterViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient // Inject your initialized Supabase Client wrapper
) : ViewModel() {

    var jobDescription by mutableStateOf("")
        private set

    private var _showPaywall = mutableStateOf(false)
    val showPaywall: Boolean get() = _showPaywall.value

    fun dismissPaywall() {
        _showPaywall.value = false
    }

    private val _uiState = MutableStateFlow(CoverLetterUiState())
    val uiState = _uiState.asStateFlow()

    // Light footprint client setup (No JSON content negotiation plugin needed because we parse lines raw)
    private val httpClient = HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            socketTimeoutMillis = 60000
        }
    }

    fun updateJobDescription(text: String) {
        jobDescription = text
    }

    fun onGenerateClicked() {
        if (jobDescription.isBlank()) return

        RevenueCatManager.isProUser { isPro ->
            if (isPro) {
                executeAiGeneration()
            } else {
                _showPaywall.value = true
            }
        }
    }

    fun onPaywallSuccess() {
        _showPaywall.value = false
        executeAiGeneration()
    }

    fun executeAiGeneration() {
        if (jobDescription.isBlank()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, generatedLetter = "", errorMessage = null)
            }

            try {
                // Get the current user session token from Supabase Auth
                val currentSessionToken = supabaseClient.auth.currentAccessTokenOrNull()
                if (currentSessionToken == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.") }
                    return@launch
                }

                val payload = mapOf("jobDescription" to jobDescription)
                val jsonPayload = Json.encodeToString(kotlinx.serialization.serializer(), payload)

                // Point this directly to your deployed Supabase edge function URL
                val edgeFunctionUrl = "https://your-project-id.supabase.co/functions/v1/generate-cover-letter"

                httpClient.preparePost(edgeFunctionUrl) {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    // Pass user authentication down to let Supabase verify the request
                    header(HttpHeaders.Authorization, "Bearer $currentSessionToken")
                    setBody(jsonPayload)
                }.execute { httpResponse ->

                    if (httpResponse.status.value == 402) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "No credits remaining.") }
                        _showPaywall.value = true
                        return@execute
                    }

                    if (!httpResponse.status.isSuccess()) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Server error: ${httpResponse.status.value}") }
                        return@execute
                    }

                    // Read the response pipeline channel stream byte-by-byte
                    val responseChannel: ByteReadChannel = httpResponse.bodyAsChannel()

                    _uiState.update { it.copy(isLoading = false) } // Turn off parent loading indicator as streaming begins

                    while (!responseChannel.isClosedForRead) {
                        val line = responseChannel.readUTF8Line() ?: break

                        // Parse standard Server-Sent Events formatting (data: { ... })
                        if (line.startsWith("data:")) {
                            val dataChunk = line.removePrefix("data:").trim()

                            if (dataChunk == "[DONE]") break // Groq finishes sending events

                            try {
                                val jsonElement = Json.parseToJsonElement(dataChunk)
                                val textContent = jsonElement.jsonObject["choices"]
                                    ?.jsonArray?.firstOrNull()
                                    ?.jsonObject["delta"]
                                    ?.jsonObject["content"]
                                    ?.jsonPrimitive?.content ?: ""

                                // Push single token characters safely down to the UI stream
                                if (textContent.isNotEmpty()) {
                                    _uiState.update {
                                        it.copy(generatedLetter = it.generatedLetter + textContent)
                                    }
                                }
                            } catch (parseException: Exception) {
                                // Gracefully skip structural noise or empty heartbeats
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to stream cover letter."
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }
}