package com.example.cvpilot.features.Resume

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cvpilot.models.CoverLetter
import com.example.cvpilot.models.Resume
import com.example.cvpilot.ui.component.animatedGradientBrush

enum class LibraryTab { RESUMES, COVER_LETTERS }

@Composable
fun ResumeLibraryView(
    modifier: Modifier = Modifier,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    // Collect states from ViewModel
    val resumes by viewModel.resumes.collectAsState()
    val coverLetters by viewModel.coverLetters.collectAsState() // Ensure this is added to your ResumeViewModel
    val isUploading by viewModel.isUploading.collectAsState()

    var selectedTab by remember { mutableStateOf(LibraryTab.RESUMES) }
    val animatedBrush = animatedGradientBrush()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadResumeToSupabase(it) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D)) // Match deep dark premium theme
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Premium Header Layout
            LibraryHeader(animatedBrush)

            // Custom Segmented Control/Toggle Group
            PremiumSegmentedPicker(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Tab Content Listing
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (selectedTab) {
                    LibraryTab.RESUMES -> {
                        if (resumes.isEmpty()) {
                            EmptyLibraryState("No Resumes Yet", "Create your first AI optimized resume")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(resumes) { resume ->
                                    PremiumLibraryCard(
                                        title = resume.title,
                                        subtitle = resume.name,
                                        dateText = "Updated recently", // Replace with raw date string if available
                                        isResume = true,
                                        onClick = { /* Open Resume Details */ }
                                    )
                                }
                            }
                        }
                    }
                    LibraryTab.COVER_LETTERS -> {
                        if (coverLetters.isEmpty()) {
                            EmptyLibraryState("No Cover Letters", "Tailor a letter for your dream application")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(coverLetters) { letter ->
                                    PremiumLibraryCard(
                                        title = letter.companyName ?: "Cover Letter",
                                        subtitle = letter.jobTitle ?: "Job Application",
                                        dateText = "Created recently",
                                        isResume = false,
                                        onClick = { /* Open Letter Details */ }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sleek Premium Action Button floating lower right
//        FloatingActionButton(
//            onClick = { launcher.launch("application/pdf") },
//            containerColor = Color(0xFF1A1A1A),
//            contentColor = Color.White,
//            shape = CircleShape,
//            border = BorderStroke(1.dp, Color(0xFF333333)),
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(end = 20.dp, bottom = 40.dp)
//                .size(60.dp)
//        ) {
//            if (isUploading) {
//                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
//            } else {
//                Icon(Icons.Default.Add, contentDescription = "Upload Document", modifier = Modifier.size(28.dp))
//            }
//        }
    }
}

@Composable
fun LibraryHeader(brush: Brush) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text("Your Content", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                brush = brush,
                fontSize = 40.sp
            )
        )
    }
}

@Composable
fun PremiumSegmentedPicker(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(54.dp)
            .background(Color(0xFF161616), RoundedCornerShape(28.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LibraryTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            val backgroundAlpha by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF262626) else Color.Transparent,
                animationSpec = tween(250)
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.Gray,
                animationSpec = tween(250)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(backgroundAlpha, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onTabSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (tab == LibraryTab.RESUMES) "Resumes" else "Cover Letters",
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun PremiumLibraryCard(
    title: String,
    subtitle: String,
    dateText: String,
    isResume: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
        border = BorderStroke(1.dp, Color(0xFF222222)) // Subdued, luxury looking border accent
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (isResume) Color(0xFF1E293B) else Color(0xFF2E1065),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp),
//                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isResume) Icons.Default.DocumentScanner else Icons.Default.Description,
                    contentDescription = null,
                    tint = if (isResume) Color(0xFF38BDF8) else Color(0xFFC084FC),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.bindDp()))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

// Inline helper to quickly scale spacer offsets
private fun Int.bindDp() = this.dp

@Composable
fun EmptyLibraryState(title: String, desc: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DocumentScanner,
            contentDescription = null,
            modifier = Modifier.size(70.dp),
            tint = Color(0xFF222222)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
        Spacer(modifier = Modifier.height(6.dp))
        Text(desc, color = Color.Gray, fontSize = 14.sp)
    }
}