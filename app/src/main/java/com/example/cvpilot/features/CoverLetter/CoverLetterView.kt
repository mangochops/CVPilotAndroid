package com.example.cvpilot.features.CoverLetter

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CoverLetterView(modifier: Modifier = Modifier) {
    var jobDescription by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Smart tools for job applications", color = MaterialTheme.colorScheme.secondary)

        OutlinedTextField(
            value = jobDescription,
            onValueChange = { jobDescription = it },
            modifier = Modifier.fillMaxWidth().height(160.dp),
            label = { Text("Paste Job Description") }
        )

        Button(
            onClick = { /* Generate Logic */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Generate AI Cover Letter")
        }
    }
}

