package com.example.cvpilot.authentication

import androidx.compose.runtime.getValue // Required for 'by'
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue // Required for 'by'
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Required for viewModelScope
import kotlinx.coroutines.launch // Required for launch
import androidx.lifecycle.ViewModelProvider

class AuthViewModel(private val authService: AuthService) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var fullName by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(AuthService()) as T
            }
        }
    }

    fun onLogin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            errorMessage = null // Clear old errors
            isLoading = true
            val result = authService.signIn(email, password)
            isLoading = false

            result.onSuccess {
                onSuccess()
            }.onFailure {
                errorMessage = it.localizedMessage ?: "Login failed"
            }
        }
    }

    fun onSignUp() {
        viewModelScope.launch {
            errorMessage = null
            isLoading = true
            val result = authService.signUp(email, password, fullName)
            isLoading = false

            result.onSuccess {
                isSuccess = true
            }.onFailure {
                errorMessage = it.localizedMessage ?: "Signup failed"
            }
        }
    }
}