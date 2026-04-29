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

@Composable
fun HomeView(modifier: Modifier = Modifier) {
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
            ActionCard("Edit Primary CV", "Store your master resume", Icons.Default.ContactPage, Color(0xFF2196F3))
            ActionCard("Tailor CV for Job", "Paste job link or upload PDF", Icons.Default.AutoAwesome, Color(0xFF9C27B0))
            ActionCard("Import from LinkedIn", "Upload LinkedIn profile PDF", Icons.Default.Link, Color(0xFF4CAF50))
        }

        // Recent Resumes Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Generated Resumes",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            // Replace with your actual list logic
            Text("No resumes yet", color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun ActionCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Handle Click */ }
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

