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
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cvpilot.authentication.AuthService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel


sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Resumes : Screen("resumes", "Files", Icons.Default.Folder)
    object AiTools : Screen("ai_tools", "Tools", Icons.Default.AutoAwesome)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CvPilotTheme {
                // State to track if onboarding is complete
                var isLoggedIn by remember { mutableStateOf(false) } // Track auth state

                val rootNavController = rememberNavController()


                val authViewModel: AuthViewModel = hiltViewModel()

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
                            viewModel = authViewModel,
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
                            viewModel = authViewModel,
                            onBackToLogin = {
                                rootNavController.navigate("login")
                            }
                        )
                    }

                    // 3. Main App Flow
                    composable("main_app") {
                        MainScreen(
                            onNavigateToDeepScreen = { destinationRoute ->
                                rootNavController.navigate(destinationRoute)
                            }
                        )
                    }

                    composable("edit_profile_screen") {
                        // TODO: Implement EditProfileView()
                        Text("Edit Profile Screen")

                    }
                    composable("premium_subscription_screen") {
                        // TODO: Implement PremiumSubscriptionView()
                        Text("Premium Subscription Screen")
                    }
                    composable("ats_analytics_screen") {
                        // TODO: Implement AtsAnalyticsView()
                        Text("ATS Analytics Screen")
                    }
                    composable("contact_support_screen") {
                        // TODO: Implement ContactSupportView()
                        Text("Contact Support Screen")
                    }
                    composable("privacy_policy_screen") {
                        // TODO: Implement PrivacyPolicyView()
                        Text("Privacy Policy Screen")
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(onNavigateToDeepScreen: (String) -> Unit) {
    val items = listOf(
        Screen.Home,
        Screen.Resumes,
        Screen.AiTools,
        Screen.Profile
    )

    // Track the currently selected tab
    var selectedItem by remember { mutableIntStateOf(0) }

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
                        "resumes" -> {
                            // Switches the active bottom bar tab to index 1 (Resumes)
                            selectedItem = 1
                        }
                        "edit_profile" -> onNavigateToDeepScreen("edit_profile_screen")
                        "premium" -> onNavigateToDeepScreen("premium_subscription_screen")
                        "analytics" -> onNavigateToDeepScreen("ats_analytics_screen")
                        "support" -> onNavigateToDeepScreen("contact_support_screen")
                        "privacy" -> onNavigateToDeepScreen("privacy_policy_screen")
                    }
                    println("Navigating to $route")
                },
                modifier = modifier
            )
        }
    }
}