package com.example.cvpilot.features.Profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    var userName by mutableStateOf("User")
        private set

    var userEmail by mutableStateOf("")
        private set

    var isPremiumUser by mutableStateOf(false)
        private set

    var creditsRemaining by mutableStateOf(0)
        private set

    init {
        loadUserProfile()
        checkSubscriptionStatus()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val user = supabaseClient.auth.currentUserOrNull()
                user?.let {
                    userEmail = it.email ?: ""
                    // Fallback to metadata full name or email prefix
                    userName = it.userMetadata?.get("full_name")?.jsonPrimitive?.content
                        ?: it.userMetadata?.get("name")?.jsonPrimitive?.content
                                ?: userEmail.substringBefore("@")
                }
            } catch (e: Exception) {
                // Handle logging errors cleanly silently
            }
        }
    }

    private fun checkSubscriptionStatus() {
        viewModelScope.launch {
            try {
                // TODO: Replace with your actual RevenueCat / database entitlement check engine
                // Purchases.sharedInstance.getCustomerInfo(onSuccess = { customerInfo ->
                //     isPremiumUser = customerInfo.entitlements["premium"]?.isActive == true
                // }, onError = {})

                // Placeholder balance logic:
                isPremiumUser = false
                creditsRemaining = if (isPremiumUser) 999 else 3
            } catch (e: Exception) {
                isPremiumUser = false
            }
        }
    }

    fun signOut(onSignOutSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                supabaseClient.auth.signOut()
                onSignOutSuccess()
            } catch (e: Exception) {
                // Graceful error logging logic goes here
            }
        }
    }
}

