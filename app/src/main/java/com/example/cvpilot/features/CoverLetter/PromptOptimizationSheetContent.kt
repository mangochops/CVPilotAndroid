package com.example.cvpilot.features.CoverLetter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
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
import com.airbnb.lottie.compose.*
import com.example.cvpilot.R

enum class OptimizationType {
    BULLET_REWRITE, SUMMARY_IMPROVE, MATCH_ANALYSIS,ATS_CHECK
}

@Composable
fun PromptOptimizationSheetContent(
    type: OptimizationType,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    // UI States (In production, wire these to a ViewModel or state handler)
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var optimizedResult by remember { mutableStateOf("") }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Dynamic Text Content based on type selection
    val sheetTitle = when (type) {
        OptimizationType.BULLET_REWRITE -> "Rewrite Resume Bullet"
        OptimizationType.SUMMARY_IMPROVE -> "Improve Profile Summary"
        OptimizationType.MATCH_ANALYSIS -> "Job Match Keyword Matrix"
        OptimizationType.ATS_CHECK -> "ATS Check Optimization"
    }

    val placeholderText = when (type) {
        OptimizationType.BULLET_REWRITE -> "e.g., I was responsible for writing code and debugging the mobile app application..."
        OptimizationType.SUMMARY_IMPROVE -> "Paste your current introduction or summary paragraph here to make it sound premium..."
        OptimizationType.MATCH_ANALYSIS -> "Paste your target job advertisement text requirements here..."
        OptimizationType.ATS_CHECK -> "Paste your ATS requirements here..."

    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(sheetTitle, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        if (type != OptimizationType.MATCH_ANALYSIS) {
            // --- INPUT / REWRITE FLOWS ---
            if (!isLoading && optimizedResult.isEmpty()) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text(placeholderText, style = MaterialTheme.typography.bodyMedium) }
                )

                Button(
                    onClick = {
                        isLoading = true
                        // Fake AI Delay for Demo (Simulating your Streaming/Generation API execution)
                        // In production: viewModel.optimizeText(inputText, type)
                        optimizedResult = if (type == OptimizationType.BULLET_REWRITE) {
                            "• Architected and engineered high-performance cross-platform mobile modules using Flutter, resulting in a 35% reduction in screen transition latencies and a 20% growth in daily user engagements."
                        } else {
                            "Results-driven Software Developer with robust expertise in crafting high-availability mobile architectures. Proven track record of leveraging Jetpack Compose and automated AI systems to scale application efficiencies."
                        }
                        isLoading = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Optimize with AI")
                }
            }
        } else {
            // --- MATRIX MATCH RATING VIEW ---
            Text("Keyword Competency Breakdown", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray)

            MatchMetricItem("Technical Framework Overlap", 0.85f, Color(0xFF34D399))
            MatchMetricItem("System Architecture & Scalability", 0.60f, Color(0xFFFFB74D))
            MatchMetricItem("Cloud API Integration Strategy", 0.90f, Color(0xFF38BDF8))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text("Recommendation: Add missing focus patterns around Cloud deployment metrics to boost rating above 90%.", fontSize = 13.sp)
                }
            }
        }

        // --- LOADING RUNTIME DISPLAY ---
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(composition = composition, progress = { progress }, modifier = Modifier.size(160.dp))
                Spacer(Modifier.height(12.dp))
                Text("Analyzing parameters...", color = MaterialTheme.colorScheme.primary)
            }
        }

        // --- OUTPUT GENERATED CARD STATE ---
        if (optimizedResult.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Optimized Result", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    SelectionContainer {
                        Text(text = optimizedResult, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { optimizedResult = ""; inputText = "" },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(8.dp))
                Text("Reset Engine")
            }
        }
    }
}

@Composable
fun MatchMetricItem(label: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp)
            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = Color.Gray.copy(alpha = 0.2f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

