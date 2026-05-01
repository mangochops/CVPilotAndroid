package com.example.cvpilot.ui.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.setValue
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkedInImportView(onDismiss: () -> Unit, onContinue: (String) -> Unit) {
    var extractedText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    // File Picker Launcher
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isProcessing = true
            // In a real app, use a Coroutine + PDFBox or similar to extract text
            // extractedText = extractTextFromPdf(it)
            isProcessing = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Import", fontSize = 18.sp) },
                navigationIcon = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                },
                actions = {
                    if (extractedText.isNotEmpty()) {
                        TextButton(onClick = { onContinue(extractedText) }) {
                            Text("Continue", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Instruction Card
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFE8F5E9), CircleShape)
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null,
                            tint = Color(0xFF4CAF50), modifier = Modifier.size(40.dp))
                    }

                    Text("LinkedIn to Resume", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "Export your LinkedIn profile as a PDF and we'll handle the rest.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Button(
                        onClick = { filePicker.launch("application/pdf") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Upload LinkedIn PDF")
                    }
                }
            }

            if (isProcessing) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
            }

            if (extractedText.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, "", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Text(" Extracted Content", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                    ) {
                        Text(extractedText, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, "", tint = Color.Blue, modifier = Modifier.size(16.dp))
                Text(" Go to LinkedIn > More > Save to PDF", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}