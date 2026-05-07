package com.example.cvpilot.authentication

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import com.example.cvpilot.network.SupabaseManager
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthService @Inject constructor(){
    private val client = SupabaseManager.client

    suspend fun signIn(email: String, pass: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}