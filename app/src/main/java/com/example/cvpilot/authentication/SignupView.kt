package com.example.cvpilot.authentication

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.*
import com.example.cvpilot.R
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.foundation.Image
import com.example.cvpilot.ui.component.animatedGradientBrush

@Composable
fun SignUpView(
    viewModel: AuthViewModel = hiltViewModel(),
    onBackToLogin: () -> Unit
) {
    val isSuccess = viewModel.isSuccess
    var passwordVisible by remember { mutableStateOf(false) }
    val animatedBrush = animatedGradientBrush()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pilot))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = isSuccess,
            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() },
            label = "AuthTransition"
        ) { success ->
            if (success) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(220.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Check your email",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "We've sent a magic confirmation link to\n${viewModel.email}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedButton(
                        onClick = onBackToLogin,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Back to Login", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 28.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            brush = animatedBrush,
                            fontSize = 44.sp,
                            letterSpacing = (-1).sp
                        )
                    )

                    Text(
                        text = "Join CV Pilot and unlock AI-guided optimization",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
                    )

                    // Full Name Field
                    OutlinedTextField(
                        value = viewModel.fullName,
                        onValueChange = { viewModel.fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (viewModel.errorMessage != null) {
                        Text(
                            text = viewModel.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Main Action Sign Up Button
                    Button(
                        onClick = { viewModel.onSignUp() },
                        enabled = !viewModel.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Sign Up", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Premium Custom Separator Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                        Text(
                            text = "OR CONTINUE WITH",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            letterSpacing = 1.sp
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // OAuth SSO Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onSignInWithGoogle() },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            GoogleIcon()
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google", fontWeight = FontWeight.Medium)
                        }

                        OutlinedButton(
                            onClick = { viewModel.onSignInWithApple() },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            AppleIcon()
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Apple", fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = onBackToLogin) {
                        Text(
                            text = "Already have an account? Log In",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun GoogleIcon() {
    val googleIconVector = remember {
        ImageVector.Builder(
            name = "GoogleIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFFEA4335))) {
                moveTo(12.24f, 5.04f)
                // 🚀 FIXED: Changed cubicTo to curveTo
                curveTo(14.12f, 5.04f, 15.8f, 5.68f, 17.13f, 6.95f)
                lineTo(20.8f, 3.28f)
                curveTo(18.57f, 1.25f, 15.68f, 0f, 12.24f, 0f)
                curveTo(7.55f, 0f, 3.48f, 2.69f, 1.51f, 6.62f)
                lineTo(5.58f, 9.77f)
                curveTo(6.55f, 6.95f, 9.17f, 5.04f, 12.24f, 5.04f)
                close()
            }
            path(fill = SolidColor(Color(0xFF4285F4))) {
                moveTo(23.45f, 12.3f)
                curveTo(23.45f, 11.48f, 23.38f, 10.66f, 23.24f, 9.86f)
                lineTo(12.24f, 9.86f)
                lineTo(12.24f, 14.48f)
                lineTo(18.53f, 14.48f)
                curveTo(18.26f, 15.95f, 17.43f, 17.22f, 16.19f, 18.05f)
                lineTo(20.19f, 21.14f)
                curveTo(22.53f, 18.98f, 23.45f, 15.83f, 23.45f, 12.3f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBBC05))) {
                moveTo(5.58f, 14.23f)
                curveTo(5.34f, 13.51f, 5.21f, 12.75f, 5.21f, 11.96f)
                curveTo(5.21f, 11.17f, 5.34f, 10.41f, 5.58f, 9.69f)
                lineTo(1.51f, 6.54f)
                curveTo(0.54f, 8.48f, 0f, 10.66f, 0f, 11.96f)
                curveTo(0f, 13.26f, 0.54f, 15.44f, 1.51f, 17.38f)
                lineTo(5.58f, 14.23f)
                close()
            }
            path(fill = SolidColor(Color(0xFF34A853))) {
                moveTo(12.24f, 18.88f)
                curveTo(9.17f, 18.88f, 6.55f, 16.97f, 5.58f, 14.15f)
                lineTo(1.51f, 17.3f)
                curveTo(3.48f, 21.23f, 7.55f, 23.92f, 12.24f, 23.92f)
                curveTo(15.53f, 23.92f, 18.32f, 22.83f, 20.19f, 20.97f)
                lineTo(16.19f, 17.88f)
                curveTo(15.11f, 18.6f, 13.78f, 18.88f, 12.24f, 18.88f)
                close()
            }
        }.build()
    }
    Image(imageVector = googleIconVector, contentDescription = "Google Logo", modifier = Modifier.size(20.dp))
}

@Composable
fun AppleIcon() {
    val isDark = isSystemInDarkTheme()
    val tintColor = if (isDark) Color.White else Color.Black

    val appleIconVector = remember {
        ImageVector.Builder(
            name = "AppleIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(tintColor)) {
                moveTo(18.71f, 19.5f)
                // 🚀 FIXED: Changed cubicTo to curveTo
                curveTo(17.88f, 20.74f, 17f, 21.95f, 15.66f, 21.97f)
                curveTo(14.32f, 22f, 13.89f, 21.18f, 12.37f, 21.18f)
                curveTo(10.84f, 21.18f, 10.37f, 21.95f, 9.1f, 22f)
                curveTo(7.81f, 22.05f, 6.8f, 20.71f, 5.96f, 19.5f)
                curveTo(4.25f, 17.0f, 2.94f, 12.45f, 4.7f, 9.39f)
                curveTo(5.57f, 7.87f, 7.14f, 6.9f, 8.82f, 6.88f)
                curveTo(10.1f, 6.85f, 11.3f, 7.73f, 12.07f, 7.73f)
                curveTo(12.85f, 7.73f, 14.31f, 6.68f, 15.85f, 6.84f)
                curveTo(16.5f, 6.87f, 18.33f, 7.1f, 19.54f, 8.88f)
                curveTo(19.44f, 8.94f, 17.15f, 10.28f, 17.17f, 12.96f)
                curveTo(17.2f, 16.14f, 19.9f, 17.21f, 19.93f, 17.22f)
                curveTo(19.91f, 17.28f, 19.5f, 18.68f, 18.71f, 19.5f)
                close()
                moveTo(15.97f, 4.17f)
                curveTo(16.63f, 3.37f, 17.07f, 2.28f, 16.95f, 1.17f)
                curveTo(16f, 1.21f, 14.9f, 1.77f, 14.25f, 2.54f)
                curveTo(13.7f, 3.17f, 13.21f, 4.28f, 13.36f, 5.37f)
                curveTo(14.41f, 5.45f, 15.43f, 4.84f, 15.97f, 4.17f)
                close()
            }
        }.build()
    }
    Image(imageVector = appleIconVector, contentDescription = "Apple Logo", modifier = Modifier.size(20.dp))
}