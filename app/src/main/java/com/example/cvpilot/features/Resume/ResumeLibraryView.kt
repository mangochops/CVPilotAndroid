package com.example.cvpilot.features.Resume

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Star
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
import com.example.cvpilot.models.Resume
import com.example.cvpilot.ui.component.animatedGradientBrush
import androidx.compose.foundation.isSystemInDarkTheme

enum class LibraryTab { RESUMES, COVER_LETTERS }

@Composable
fun ResumeLibraryView(
    modifier: Modifier = Modifier,
    viewModel: ResumeViewModel = hiltViewModel(),
    onResumeClick: (resumeId: String) -> Unit = {},
    onCoverLetterClick: (letterId: String) -> Unit = {}
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

    // Use MaterialTheme colors instead of hardcoded dark colors
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor) // Match deep dark premium theme
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Premium Header Layout
            LibraryHeader(animatedBrush, onSurfaceColor = onSurfaceColor)

            PrimaryResumeSection(
                resumes = resumes,
                onUploadClick = {
                    val primaryId = resumes.firstOrNull()?.id
                    if (primaryId != null) onResumeClick(primaryId) else launcher.launch("application/pdf")
                },
                surfaceColor = surfaceColor,
                onSurfaceColor = onSurfaceColor,
                onSurfaceVariant = onSurfaceVariant
            )



            // Custom Segmented Control/Toggle Group
            PremiumSegmentedPicker(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                surfaceColor = surfaceColor,
                onSurfaceColor = onSurfaceColor,
                onSurfaceVariant = onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Tab Content Listing
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (selectedTab) {
                    LibraryTab.RESUMES -> {
                        if (resumes.isEmpty()) {
                            EmptyLibraryState("No Resumes Yet", "Create your first AI optimized resume", onSurfaceVariant)
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
                                        onClick = { resume.id?.let { onResumeClick(it) } },
                                        onSurfaceColor = onSurfaceColor,
                                        surfaceColor = surfaceColor,
                                        onSurfaceVariant = onSurfaceVariant,
                                        primaryColor = primaryColor
                                    )
                                }
                            }
                        }
                    }
                    LibraryTab.COVER_LETTERS -> {
                        if (coverLetters.isEmpty()) {
                            EmptyLibraryState("No Cover Letters", "Tailor a letter for your dream application", onSurfaceVariant)
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
                                        onClick = { letter.id?.let { onCoverLetterClick(it) } },
                                        onSurfaceColor = onSurfaceColor,
                                        surfaceColor = surfaceColor,
                                        onSurfaceVariant = onSurfaceVariant,
                                        primaryColor = primaryColor
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
fun LibraryHeader(brush: Brush, onSurfaceColor: Color) {
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
fun PrimaryResumeSection(resumes: List<Resume>, onUploadClick: () -> Unit,
                         surfaceColor: Color,
                         onSurfaceColor: Color,
                         onSurfaceVariant: Color) {
    // Finds the first resume or treats it as the default base file
    val primaryResume = resumes.firstOrNull()
    val isDark = isSystemInDarkTheme()

    // Smooth adaptive border configuration for dark/light variations
    val borderColor = if (isDark) Color(0xFF2E2E2E) else onSurfaceColor.copy(alpha = 0.12f)
    val iconBgColor = if (isDark) Color(0xFF22252A) else onSurfaceColor.copy(alpha = 0.06f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "PRIMARY CV",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = onSurfaceVariant,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUploadClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF22252A),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(44.dp),
//                    contentAlignment = Alignment.Center
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = primaryResume?.title ?: "No Master CV Set",
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor,
                        fontSize = 16.sp
                    )
                    Text(
                        text = primaryResume?.name ?: "Upload your master copy base file",
                        color = onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }

                if (primaryResume == null) {
                    Button(
                        onClick = onUploadClick,
                        colors = ButtonDefaults.buttonColors(containerColor = onSurfaceColor.copy(alpha = 0.1f)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Setup", color = onSurfaceColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumSegmentedPicker(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit,
    surfaceColor: Color,
    onSurfaceColor: Color,
    onSurfaceVariant: Color
) {
    val isDark = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(54.dp)
            .background(surfaceColor.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LibraryTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            val activeBg = if (isDark) Color(0xFF262626) else onSurfaceColor.copy(alpha = 0.12f)
            val backgroundAlpha by animateColorAsState(
                targetValue = if (isSelected) activeBg else Color.Transparent,
                animationSpec = tween(250)
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) onSurfaceColor else onSurfaceVariant,
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
    onClick: () -> Unit,
    surfaceColor: Color,
    onSurfaceColor: Color,
    onSurfaceVariant: Color,
    primaryColor: Color
) {
    val isDark = isSystemInDarkTheme()
    val borderColor = if (isDark) Color(0xFF222222) else onSurfaceColor.copy(alpha = 0.08f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, borderColor) // Subdued, luxury looking border accent
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = onSurfaceColor)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.bindDp()))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// Inline helper to quickly scale spacer offsets
private fun Int.bindDp() = this.dp

@Composable
fun EmptyLibraryState(title: String, desc: String, onSurfaceVariant: Color) {
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DocumentScanner,
            contentDescription = null,
            modifier = Modifier.size(70.dp),
            tint = onSurfaceVariant.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
        Spacer(modifier = Modifier.height(6.dp))
        Text(desc, color = onSurfaceVariant, fontSize = 14.sp)
    }
}