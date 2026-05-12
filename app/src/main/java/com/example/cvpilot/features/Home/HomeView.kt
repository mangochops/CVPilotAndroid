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

@Composable
fun HomeView(modifier: Modifier = Modifier,viewModel: HomeViewModel = viewModel() ) {
    var showLinkedInImport by remember { mutableStateOf(false) }
    var showJobAdEntry by remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Get More Interviews",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            )
            Text(
                text = "Tailor your resume to every job using AI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Action Cards (Logic for clicking can be added via lambdas)
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ActionCard("Edit Primary CV", "Store your master resume", Icons.Default.ContactPage, Color(0xFF2196F3),onClick = { TODO() })
            ActionCard("Tailor CV for Job", "Paste job link or upload PDF", Icons.Default.AutoAwesome, Color(0xFF9C27B0), onClick = {
                // Optional: Default to the first resume if one exists
                if (viewModel.resumes.isNotEmpty() && viewModel.selectedResumeFullText.isEmpty()) {
                    viewModel.selectedResumeFullText = viewModel.resumes.first().content
                }
                showJobAdEntry = true
            })
            ActionCard("Import from LinkedIn", "Upload LinkedIn profile PDF", Icons.Default.Link, Color(0xFF4CAF50), onClick = { showLinkedInImport = true })
        }

        // Recent Resumes Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Generated Resumes",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            // Replace with your actual list logic
            // Inside HomeView.kt
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                viewModel.resumes.forEach { resume ->
                    ResumeCard(
                        name = resume.name,
                        title = "Tailored CV",
                        date = resume.date,
                        content = resume.content, // This now shows the actual PDF text
                        onClick = {
                            // 1. Store the selected resume content in the ViewModel
                            viewModel.selectedResumeFullText = resume.content

                            // 2. Open the Tailor UI
                            showJobAdEntry = true
                        }
                    )
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
            }
        )
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

