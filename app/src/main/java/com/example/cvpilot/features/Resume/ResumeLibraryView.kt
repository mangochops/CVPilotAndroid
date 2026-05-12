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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.graphics.Color
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import android.net.Uri
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import com.example.cvpilot.models.Resume
import androidx.hilt.navigation.compose.hiltViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeLibraryView(
    modifier: Modifier = Modifier,
    viewModel: ResumeViewModel = hiltViewModel()
    ) {
    // State from ViewModel
    val resumes by viewModel.resumes.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()

    // File Picker Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadResumeToSupabase(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Resumes") })
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { launcher.launch("application/pdf") },
                containerColor = Color.Blue, // Matching SwiftUI .blue
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Add, contentDescription = "Create")
                }
            }
        }
    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            if (resumes.isEmpty()) {
                // MARK: Empty State (Identical to SwiftUI)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.Gray.copy(alpha = 0.6f)
                    )
                    Text("No Resumes Yet", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text("Create your first AI optimized resume", color = Color.Gray)
                }
            } else {
                // MARK: Resume List
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(resumes) { resume ->
                        ResumeCard(resume = resume) // Create a separate ResumeCard component
                    }
                }
            }
        }
    }
}

@Composable
fun ResumeCard(resume: Resume) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DocumentScanner, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = resume.title, style = MaterialTheme.typography.titleMedium)
                Text(text = resume.name, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}