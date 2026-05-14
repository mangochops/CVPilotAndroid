package com.example.cvpilot.authentication

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// Lottie Imports
import com.airbnb.lottie.compose.*
import com.example.cvpilot.R
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithApple
import io.github.jan.supabase.compose.auth.composeAuth
import androidx.compose.ui.graphics.Color

@Composable
fun SignUpView(
    viewModel: AuthViewModel = hiltViewModel(), // Connect your real ViewModel
               onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isSuccess = viewModel.isSuccess

    // Prepare Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pilot))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Or 1 if you want it to stop
    )

    AnimatedContent(
        targetState = isSuccess,
        transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() },
        label = "AuthTransition"
    ) { success ->
        if (success) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Lottie Animation replaces the static Icon
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(200.dp) // Adjust size as needed
                )

                Text(
                    text = "Check your email",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "We've sent a confirmation link to \n${viewModel.email}",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(onClick = onBackToLogin) {
                    Text("Back to Login")
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = viewModel.fullName,
                    onValueChange = { viewModel.fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (viewModel.errorMessage != null) {
                    Text(
                        text = viewModel.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.onSignUp() },
                    enabled = !viewModel.isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Sign Up")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("OR", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Google Button
                    OutlinedButton(
                        onClick = { viewModel.onSignInWithGoogle() }, // Call your VM function
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        // You can add a Google Icon here
                        Text("Google")
                    }

                    // Apple Button
                    OutlinedButton(
                        onClick = { viewModel.onSignInWithApple() }, // Call your VM function
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Apple")
                    }
                }

                TextButton(onClick = onBackToLogin) {
                    Text("Already have an account? Log In")
                }
            }
        }
    }
}