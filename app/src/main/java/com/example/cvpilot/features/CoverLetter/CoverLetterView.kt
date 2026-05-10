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
import com.example.cvpilot.paywall.RevenueCatPaywall
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cvpilot.ui.component.StreamingText
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cvpilot.R
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import androidx.compose.foundation.text.selection.SelectionContainer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterView(
    modifier: Modifier = Modifier,
    viewModel: CoverLetterViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val jobDescription by viewModel.jobDescription.collectAsStateWithLifecycle() // Unwraps StateFlow<String>
    val showPaywall by viewModel.showPaywall.collectAsStateWithLifecycle()     // Unwraps StateFlow<Boolean>

    // Lottie Animation Setup
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

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
            // --- 1. INPUT STATE ---
            if (!uiState.isLoading && uiState.generatedLetter.isEmpty()) {
                JobDescriptionCard(
                    text = jobDescription,
                    onValueChange = { viewModel.updateJobDescription(it) }
                )

                Button(
                    onClick = { viewModel.onGenerateClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled =jobDescription.isNotBlank(),
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(240.dp)
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
                    onClick = { viewModel.updateJobDescription("") },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Start Over")
                }
            }

            // --- 4. PERSISTENT TOOLS ---
            if (!uiState.isLoading) {
                QuickToolsSection()
            }
        } // End of Column

        // Paywall Logic
        if (showPaywall) {
            RevenueCatPaywall(
                onDismiss = { viewModel.dismissPaywall() },
                onSuccess = { viewModel.onPaywallSuccess() }
            )
        }
    } // End of Scaffold
} // End of CoverLetterView function

            // Live Output Card
//            if (uiState.generatedLetter.isNotEmpty() || uiState.isLoading) {
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
//                    )
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text(
//                            text = "Generated Cover Letter",
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        StreamingText(uiState.generatedLetter, uiState.isLoading)
//                    }
//                }
//            }

            // Quick Tools Section






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

