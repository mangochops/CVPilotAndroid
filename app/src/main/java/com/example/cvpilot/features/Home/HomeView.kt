package com.example.cvpilot.features.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cvpilot.ui.component.ResumeCard
import com.example.cvpilot.ui.component.LinkedInImportView
import com.example.cvpilot.ui.component.UploadJobAdView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cvpilot.ui.component.animatedGradientBrush
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import com.example.cvpilot.ui.component.CompactCoverLetterCard
import com.example.cvpilot.ui.component.CompactResumeCard
import android.util.Log
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cvpilot.features.CoverLetter.CoverLetterSheetContent
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(modifier: Modifier = Modifier,viewModel: HomeViewModel = hiltViewModel() ) {
    var showLinkedInImport by remember { mutableStateOf(false) }
    var showJobAdEntry by remember { mutableStateOf(false) }
    var showCoverLetterEntry by remember { mutableStateOf(false) }

    val showPaywall by viewModel.showPaywall.collectAsStateWithLifecycle()

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val credits by viewModel.userCredits.collectAsStateWithLifecycle()
    val isUploading by viewModel.isUploading.collectAsStateWithLifecycle()

    val animatedBrush = animatedGradientBrush()

    // File picker launcher for PDF upload
    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadResumeToSupabase(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // Header
        HeaderSection(userProfile?.firstName ?: "User", credits, animatedBrush)

        // Action Cards (Logic for clicking can be added via lambdas)
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ActionCard("Upload your primary CV", "Store your master resume", Icons.Default.ContactPage, Color(0xFF2196F3),onClick = { pdfLauncher.launch("application/pdf") })
            ActionCard("Tailor CV for Job", "Paste job description", Icons.Default.AutoAwesome, Color(0xFF9C27B0), onClick = {
                if (viewModel.recentResumes.isNotEmpty() && viewModel.selectedResumeFullText.isEmpty()) {
                    //  Grab the first resume and parse its text property safely
                    val firstResume = viewModel.recentResumes.first()
                    val cleanText = try {
                        firstResume.content?.jsonObject?.get("text")?.jsonPrimitive?.content ?: ""
                    } catch (e: Exception) {
                        ""
                    }
                    viewModel.selectedResumeFullText = cleanText
                }
                showJobAdEntry = true
            })
            ActionCard("Generate cover letter", "Generate a cover letter for an application", Icons.Default.Email, Color(0xFF4CAF50), onClick = { showCoverLetterEntry = true })
        }

        // Recent Resumes Section
        // --- Recent Resumes Section ---
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(title = "Recent Resumes") {
                // TODO: Navigate to full Library view
            }

            if (viewModel.isLoading) {
                // Use a more premium looking indicator or skeleton loaders here
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else if (viewModel.recentResumes.isEmpty()) {
                Text(
                    "No generations yet. Try tailoring a CV!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // This is the fix: Wrap your list in the HorizontalList component
                HorizontalList(items = viewModel.recentResumes) { resume ->
                    key(resume.id) {
                        CompactResumeCard(
                            resume = resume,
                            onClick = {
                                //  Extract the clean text out of the jsonb container safely
                                val cleanText = try {
                                    resume.content?.jsonObject?.get("text")?.jsonPrimitive?.content ?: ""
                                } catch (e: Exception) {
                                    ""
                                }
                                viewModel.selectedResumeFullText = cleanText
                                showJobAdEntry = true
                            }
                        )
                    }
                }
            }
        }

        // --- Recent Cover Letters Section ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Slightly tighter spacing for premium feel
        ) {
            SectionHeader(title = "Recent Cover Letters") {
                // Navigate to full library
            }

            // Logic to handle empty vs loaded states
            if (viewModel.isLoading) {
                // Show a horizontal skeleton or a simple indicator
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            } else if (viewModel.recentCoverLetters.isEmpty()) {
                Text(
                    "Your generated letters will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                // Using the HorizontalList component for swipeable cards
                HorizontalList(items = viewModel.recentCoverLetters) { letter ->
                    key(letter.id) {
                        CompactCoverLetterCard(
                            letter = letter,
                            onClick = {
                                // Click logic: Open the full letter view or copy text
                                Log.d("UI", "Selected letter for ${letter.companyName}")
                            }
                        )
                    }
                }
            }
        }
    }
    // Overlay Sheets (Conditional Rendering)
    if (showLinkedInImport) {
        LinkedInImportView(
            onDismiss = { showLinkedInImport = false },
            onContinue = { text ->
                // Handle the imported text here
                showLinkedInImport = false
            }
        )
    }

    if (showJobAdEntry) {
        UploadJobAdView(
            // 1. Pass the text currently stored in the ViewModel
            selectedResumeContent = viewModel.selectedResumeFullText,
            onDismiss = { showJobAdEntry = false },
            // 2. Provide a lambda for onAnalyze (even if it just closes the view for now)
            onSelectResume = {
                // You can call your download logic or analysis here
                showJobAdEntry = false
            },
            onShowPaywall = {
                showJobAdEntry = false // Close the tailor dialog
                viewModel.triggerPaywall() // Show the RevenueCat overlay
            }

        )
    }
    if (showCoverLetterEntry) {
        ModalBottomSheet(
            onDismissRequest = { showCoverLetterEntry = false },
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            CoverLetterSheetContent(
                onDismiss = { showCoverLetterEntry = false }
            )
        }
    }

}

@Composable
fun ActionCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
        )
        TextButton(onClick = onSeeAll) {
            Text("See All", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HeaderSection(name: String, credits: Int, brush: androidx.compose.ui.graphics.Brush) {
    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good Morning,"
            in 12..16 -> "Good Afternoon,"
            in 17..20 -> "Good Evening,"
            else -> "Good Night,"
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(greeting, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    brush = brush,
                    fontSize = 38.sp
                )
            )
        }

        // Credits Badge
        Surface(
            color = Color(0xFF1A1A1A), // Dark premium feel
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Token, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("$credits", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun <T> HorizontalList(items: List<T>, content: @Composable (T) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            // padding only on the ends of the scrollable area
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { content(it) }
        // Add a spacer at the end so the last card doesn't stick to the edge
        Spacer(modifier = Modifier.width(16.dp))
    }
}

