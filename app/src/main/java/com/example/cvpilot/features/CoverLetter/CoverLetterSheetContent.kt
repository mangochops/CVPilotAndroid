package com.example.cvpilot.features.CoverLetter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.*
import com.example.cvpilot.R
import com.example.cvpilot.paywall.RevenueCatPaywall

@Composable
fun CoverLetterSheetContent(
    modifier: Modifier = Modifier,
    viewModel: CoverLetterViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val jobDescription by viewModel.jobDescription.collectAsStateWithLifecycle()
    val showPaywall by viewModel.showPaywall.collectAsStateWithLifecycle()

    val companyName by viewModel.companyName.collectAsStateWithLifecycle()
    val jobTitle by viewModel.jobTitle.collectAsStateWithLifecycle()

    // Lottie Setup
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Sheet Header Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI Cover Letter Engine",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close Sheet")
            }
        }

        // --- 1. INPUT STATE ---
        if (!uiState.isLoading && uiState.generatedLetter.isEmpty()) {
            JobDescriptionCard(
                text = jobDescription,
                companyName = companyName,
                jobTitle = jobTitle,
                onValueChange = { viewModel.updateJobDescription(it) },
                onCompanyChange = { viewModel.updateCompanyName(it) },
                onTitleChange = { viewModel.updateJobTitle(it) }
            )

            Button(
                onClick = { viewModel.onGenerateClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = jobDescription.isNotBlank() && companyName.isNotBlank() && jobTitle.isNotBlank(),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Generate with AI")
            }
        }

        // --- 2. LOADING STATE ---
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(200.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "AI is crafting your letter...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // --- 3. RESULT STATE ---
        if (uiState.generatedLetter.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Generated Cover Letter", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    SelectionContainer {
                        Text(
                            text = uiState.generatedLetter,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.updateJobDescription("")
                    viewModel.updateCompanyName("")
                    viewModel.updateJobTitle("")
                    viewModel.resetForm() // Explicitly call this to clear uiState.generatedLetter
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Start Over")
            }
        }

        // --- 4. PERSISTENT QUICK TOOLS ---
//        if (!uiState.isLoading) {
//            QuickToolsSection()
//        }
    }

    // Sheet-Isolated Paywall Logic
    if (showPaywall) {
        RevenueCatPaywall(
            onDismiss = { viewModel.dismissPaywall() },
            onSuccess = { viewModel.onPaywallSuccess() }
        )
    }
}

@Composable
fun JobDescriptionCard(
    text: String,
    companyName: String,
    jobTitle: String,
    onValueChange : (String) -> Unit,
    onCompanyChange : (String) -> Unit,
    onTitleChange : (String) -> Unit
) {
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
                Text("Application Details", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedTextField(
                value = companyName,
                onValueChange = onCompanyChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text("Company Name", style = MaterialTheme.typography.bodyMedium) },
                placeholder = { Text("e.g. Google, Safaricom") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = jobTitle,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text("Job Position / Title", style = MaterialTheme.typography.bodyMedium) },
                placeholder = { Text("e.g. Junior Android Developer") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = text,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                label = { Text("Job Description") },
                placeholder = { Text("Paste the requirements or description details here...", style = MaterialTheme.typography.bodyMedium) },
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

