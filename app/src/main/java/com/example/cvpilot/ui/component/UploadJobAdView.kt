package com.example.cvpilot.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.material3.Scaffold
import io.ktor.websocket.Frame.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadJobAdView(onDismiss: () -> Unit) {
    var jobLink by remember { mutableStateOf("") }
    var jobDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Job Advert") },
                navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF9C27B0), modifier = Modifier.size(40.dp))
                Text("Target Your Resume", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Provide job details so AI can tailor your experience.",
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
            }

            Card(shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = jobLink,
                        onValueChange = { jobLink = it },
                        label = { Text("Job Advert Link") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Link, null) }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(Modifier.weight(1f))
                        Text(" OR ", style = MaterialTheme.typography.labelSmall)
                        HorizontalDivider(Modifier.weight(1f))
                    }

                    OutlinedTextField(
                        value = jobDescription,
                        onValueChange = { jobDescription = it },
                        label = { Text("Paste Job Description") },
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        leadingIcon = { Icon(Icons.Default.Description, null) }
                    )
                }
            }

            Button(
                onClick = { /* Analyze logic */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = jobLink.isNotEmpty() || jobDescription.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
            ) {
                Text("Analyze & Tailor CV")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.AutoAwesome, null)
            }
        }
    }
}