package com.example.cvpilot.features.Resume

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResumeLibraryView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // Empty State (Matches your logic)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.outline)
            Text("No Resumes Yet", style = MaterialTheme.typography.titleLarge)
            Text("Create your first AI optimized resume", color = MaterialTheme.colorScheme.secondary)
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { /* Show Picker */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create")
        }
    }
}

