package com.example.cvpilot.features.CoverLetter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvpilot.paywall.RevenueCatManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

data class CoverLetterUiState(
    val isLoading: Boolean = false,
    val generatedLetter: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class CoverLetterViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val httpClient: HttpClient
) : ViewModel() {

    // Use StateFlow instead of mutableStateOf to avoid Binder Transaction issues
    private val _jobDescription = MutableStateFlow("")
    val jobDescription = _jobDescription.asStateFlow()

    private val _uiState = MutableStateFlow(CoverLetterUiState())
    val uiState = _uiState.asStateFlow()

    private var _showPaywall = MutableStateFlow(false)
    val showPaywall = _showPaywall.asStateFlow()

    fun updateJobDescription(text: String) { _jobDescription.value = text }
    fun dismissPaywall() { _showPaywall.value = false }

    fun onGenerateClicked() {
        val currentDesc = _jobDescription.value
        Log.d("CV_DEBUG", "Generate Clicked. Length: ${currentDesc.length}")

        if (currentDesc.isBlank()) return

        // CRITICAL: Ensure we are handling the SDK callback safely
        try {
            RevenueCatManager.isProUser { isPro ->
                // RevenueCat often returns on a background thread.
                // We MUST use viewModelScope to get back to the Main thread for StateFlow updates.
                viewModelScope.launch {
                    Log.d("CV_DEBUG", "RevenueCat callback: isPro = $isPro")
                    if (isPro) {
                        executeAiGeneration()
                    } else {
                        _showPaywall.value = true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CV_DEBUG", "RevenueCat Crash: ${e.message}")
            // Fallback: If the billing SDK fails, let the user try the generation anyway
            // or show an error rather than crashing.
            executeAiGeneration()
        }
    }

    fun onPaywallSuccess() {
        _showPaywall.value = false
        executeAiGeneration()
    }

    private fun executeAiGeneration() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generatedLetter = "", errorMessage = null) }
            val currentDesc = _jobDescription.value

            try {
                withContext(Dispatchers.IO) {
                    Log.d("CV_DEBUG", "Switching to IO Thread")
                    val session = supabaseClient.auth.currentSessionOrNull()
                    val token = session?.accessToken

                    if (token == null) {
                        Log.e("CV_DEBUG", "No Auth Token")
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Auth session lost.") }
                        return@withContext
                    }

                    val url = "https://eocldmwhgovgdhuttwgs.supabase.co/functions/v1/generate-cover-letter"
                    Log.d("CV_DEBUG", "Preparing Request to: $url")

                    httpClient.preparePost(url) {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        header(HttpHeaders.Authorization, "Bearer $token")

                        // Stream the body to avoid OOM
                        setBody(buildString {
                            append("{\"jobDescription\":")
                            append(Json.encodeToString(currentDesc))
                            append("}")
                        })

                        timeout {
                            requestTimeoutMillis = 60000
                            connectTimeoutMillis = 60000
                        }
                    }.execute { response ->
                        Log.d("CV_DEBUG", "Response Received: ${response.status}")

                        if (!response.status.isSuccess()) {
                            _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${response.status.value}") }
                            return@execute
                        }

                        val channel = response.bodyAsChannel()
                        val resultBuilder = StringBuilder()

                        while (!channel.isClosedForRead) {
                            val line = channel.readUTF8Line() ?: break
                            if (line.startsWith("data:")) {
                                val data = line.removePrefix("data:").trim()
                                if (data == "[DONE]") break
                                try {
                                    val content = Json.parseToJsonElement(data)
                                        .jsonObject["choices"]?.jsonArray?.get(0)
                                        ?.jsonObject?.get("delta")
                                        ?.jsonObject?.get("content")
                                        ?.jsonPrimitive?.content ?: ""
                                    resultBuilder.append(content)
                                } catch (e: Exception) { }
                            }
                        }

                        Log.d("CV_DEBUG", "Streaming Complete")
                        val finalOut = resultBuilder.toString()
                        _uiState.update { it.copy(isLoading = false, generatedLetter = finalOut) }
                    }
                }
            } catch (e: Exception) {
                Log.e("CV_DEBUG", "Exception: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}