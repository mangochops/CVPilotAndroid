package com.example.cvpilot.features.CoverLetter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.AutoAwesome // If using standard
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterView(
    modifier: Modifier = Modifier,
    viewModel: CoverLetterViewModel = hiltViewModel()
) {
    var jobDescription by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("AI Tools") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            Text(
                text = "Smart tools for job applications",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            // Job Description Card
            JobDescriptionCard(
                text = jobDescription,
                onValueChange = { jobDescription = it }
            )

            // Generate Button
            Button(
                onClick = { viewModel.generateCoverLetter() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading && jobDescription.isNotBlank(),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ){
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Generate AI Cover Letter", fontWeight = FontWeight.SemiBold)
            }
            }

            // Quick Tools Section
            QuickToolsSection()
        }
    }
}

@Composable
fun JobDescriptionCard(text: String, onValueChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.TextSnippet, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Paste Job Description", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedTextField(
                value = text,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp), // Adjusted height for better screen balance
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Enter the job requirements here...", style = MaterialTheme.typography.bodyMedium) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun QuickToolsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Quick AI Tools",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        AIQuickToolItem(title = "Rewrite Resume Bullet", icon = Icons.Default.Edit)
        AIQuickToolItem(title = "Improve Resume Summary", icon = Icons.Default.Notes)
        AIQuickToolItem(title = "Analyze Job Match", icon = Icons.Default.BarChart)
    }
}

@Composable
fun AIQuickToolItem(title: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Tool Logic */ },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}