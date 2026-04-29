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
            MaterialTheme {
                // State to track if onboarding is complete
                var showOnboarding by remember { mutableStateOf(true) }

                if (showOnboarding) {
                    OnboardingScreen(onFinished = {
                        showOnboarding = false
                    })
                } else {
                    MainScreen()
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
            3 -> ProfileView(modifier)
        }
    }
}