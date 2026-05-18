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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError

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
                // Convert RevenueCat interface callbacks cleanly into a coroutine suspension point
                val customerInfo = suspendCancellableCoroutine<CustomerInfo?> { continuation ->
                    Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
                        override fun onReceived(customerInfo: CustomerInfo) {
                            if (continuation.isActive) continuation.resume(customerInfo)
                        }

                        override fun onError(error: PurchasesError) {
                            android.util.Log.e("REVENUECAT_DEBUG", "Error fetching customer info: ${error.message}")
                            if (continuation.isActive) continuation.resume(null)
                        }
                    })
                }

                // Verify if the premium entitlement identifier is active
                isPremiumUser = customerInfo?.entitlements?.get("premium")?.isActive == true

                // Assign credits balance based on subscription tier state
                creditsRemaining = if (isPremiumUser) 999 else 3

            } catch (e: Exception) {
                android.util.Log.e("PROFILE_DEBUG", "Subscription check crash: ${e.message}")
                isPremiumUser = false
                creditsRemaining = 0
            }
        }
    }

    fun updateProfileName(newName: String) {
        viewModelScope.launch {
            try {
                // Update the user metadata attributes inside Supabase Auth management
                supabaseClient.auth.updateUser {
                    data = buildJsonObject {
                        put("full_name", newName)
                    }
                }
                // Update local state immediately so UI refreshes cleanly
                userName = newName
            } catch (e: Exception) {
                android.util.Log.e("PROFILE_DEBUG", "Failed to update profile name: ${e.message}")
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

