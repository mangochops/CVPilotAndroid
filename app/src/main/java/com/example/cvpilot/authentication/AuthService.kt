package com.example.cvpilot.authentication

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import com.example.cvpilot.network.SupabaseManager
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.LogInCallback
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.CustomerInfo
import android.util.Log
import io.github.jan.supabase.auth.providers.AuthProvider

class AuthService @Inject constructor(){
    private val client = SupabaseManager.client

    suspend fun signIn(email: String, pass: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            syncRevenueCatUser()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, pass: String, name: String): Result<Unit> {
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = pass
                // Store fullName in user_metadata so it's accessible later
                data = buildJsonObject {
                    put("full_name", name)
                }
            }

            syncRevenueCatUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithSocial(provider: AuthProvider<*, *>): Result<Unit> {
        return try {
            // This triggers the external browser flow
            client.auth.signInWith(provider)
            syncRevenueCatUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun syncRevenueCatUser() {
        val supabaseUserId = client.auth.currentUserOrNull()?.id

        if (supabaseUserId != null) {
            Purchases.sharedInstance.logIn(
                newAppUserID = supabaseUserId,
                callback = object : LogInCallback {
                    override fun onReceived(customerInfo: CustomerInfo, created: Boolean) {
                        Log.d("CV_DEBUG", "RevenueCat successfully linked to: $supabaseUserId")
                    }
                    override fun onError(error: PurchasesError) {
                        Log.e("CV_DEBUG", "RevenueCat Login Error: ${error.message}")
                    }
                }
            )
        }
    }
}