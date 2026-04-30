package com.example.cvpilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cvpilot.features.Home.HomeView
import com.example.cvpilot.features.Resume.ResumeLibraryView
import com.example.cvpilot.features.CoverLetter.CoverLetterView
import com.example.cvpilot.features.Profile.ProfileView
import com.example.cvpilot.ui.onboarding.OnboardingScreen
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cvpilot.ui.theme.CvPilotTheme
import com.example.cvpilot.authentication.SignUpView
import com.example.cvpilot.authentication.LoginView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cvpilot.authentication.AuthViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Resumes : Screen("resumes", "Resumes", Icons.Default.Folder)
    object AiTools : Screen("ai_tools", "AI Tools", Icons.Default.AutoAwesome)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CvPilotTheme {
                // State to track if onboarding is complete
                var isLoggedIn by remember { mutableStateOf(false) } // Track auth state

                val rootNavController = rememberNavController()

                NavHost(
                    navController = rootNavController,
                    // Start at onboarding for new users
                    startDestination = "onboarding"
                ) {
                    // 1. Onboarding Screen
                    composable("onboarding") {
                        OnboardingScreen(onFinished = {
                            // Navigate to signup and clear onboarding from backstack
                            rootNavController.navigate("signup") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        })
                    }

                    // 2. Auth Flow
                    composable("login") {
                        LoginView(
                            viewModel = viewModel(factory = AuthViewModel.Factory),
                            onNavigateToSignUp = { rootNavController.navigate("signup") },
                            onLoginSuccess = {
                                isLoggedIn = true
                                rootNavController.navigate("main_app") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("signup") {
                        SignUpView(
                            // THIS IS THE CRITICAL ADDITION
                            viewModel = viewModel(factory = AuthViewModel.Factory),
                            onBackToLogin = {
                                rootNavController.navigate("login")
                            }
                        )
                    }

                    // 3. Main App Flow
                    composable("main_app") {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val items = listOf(
        Screen.Home,
        Screen.Resumes,
        Screen.AiTools,
        Screen.Profile
    )

    // Track the currently selected tab
    var selectedItem by remember { mutableIntStateOf(0) }
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                items.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        // This modifier applies the padding from the bottom bar so content isn't hidden
        val modifier = Modifier.padding(innerPadding)

        when (selectedItem) {
            0 -> HomeView(modifier)
            1 -> ResumeLibraryView(modifier)
            2 -> CoverLetterView(modifier)
            3 -> ProfileView(
                onNavigate = { route ->
                    // This mirrors your iOS NavigationStack logic
                    when (route) {
                        "edit_profile" -> navController.navigate("edit_profile_screen")
                        "premium" -> navController.navigate("premium_subscription_screen")
                        "resumes" -> {
                            // You could also jump to a different bottom tab
                            selectedItem = 1
                        }
                        "analytics" -> navController.navigate("ats_analytics_screen")
                        "support" -> navController.navigate("contact_support_screen")
                        "privacy" -> navController.navigate("privacy_policy_screen")
                    }
                    println("Navigating to $route")
                },
                modifier = modifier
            )
        }
    }
}