package com.example.cvpilot.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.cvpilot.features.Home.downloadTailoredResume
import com.example.cvpilot.R // Ensure you have your lottie JSON in res/raw
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadJobAdView(
    selectedResumeContent: String,
    onDismiss: () -> Unit,
    onSelectResume: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var jobDetails by remember { mutableStateOf("") }
    var isDownloading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Lottie Setup
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    val purplePrimary = Color(0xFF7B1FA2)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI CV Tailor", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) } }
            )
        }
    ) { padding ->
        if (isDownloading) {
            // Lottie Loading State
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(250.dp)
                )
                Text("Tailoring your experience...", style = MaterialTheme.typography.titleMedium)
                Text("This may take a moment", color = Color.Gray)
            }
        } else {
            // Main Input UI
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header & Resume Selection Card (as designed previously)
                ResumeStatusCard(selectedResumeContent, onSelectResume)

                OutlinedTextField(
                    value = jobDetails,
                    onValueChange = { jobDetails = it },
                    placeholder = { Text("Paste job details or requirements...") },
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                )

                Button(
                    onClick = {
                        isDownloading = true
                        scope.launch {
                            try {
                                downloadTailoredResume(context, jobDetails, selectedResumeContent)
                            } finally {
                                isDownloading = false
                                onDismiss()
                            }
                        }
                    },
                    enabled = jobDetails.isNotEmpty() && selectedResumeContent.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = purplePrimary)
                ) {
                    Text("Generate & Download PDF", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ResumeStatusCard(content: String, onClick: () -> Unit) {
    val isReady = content.isNotEmpty()
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isReady) Color(0xFFF3E5F5) else Color(0xFFFFF1F0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isReady) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                null,
                tint = if (isReady) Color(0xFF2E7D32) else Color.Red
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(if (isReady) "Resume Ready" else "No Resume Selected", fontWeight = FontWeight.Bold)
                Text(if (isReady) "Targeting your primary CV" else "Tap to choose a resume first", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}