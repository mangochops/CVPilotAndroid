package com.example.cvpilot.features.CoverLetter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cvpilot.ui.component.animatedGradientBrush
import com.example.cvpilot.features.Home.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cvpilot.ui.component.UploadJobAdView
import com.example.cvpilot.models.AtsMatrixResponse
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterView(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    var atsResult by remember { mutableStateOf<AtsMatrixResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showOptimizationSheet by remember { mutableStateOf(false) }
    var activeOptimizationType by remember { mutableStateOf(OptimizationType.BULLET_REWRITE) }

    val scrollState = rememberScrollState()
    val animatedBrush = animatedGradientBrush()

    var showCoverLetterEntry by remember { mutableStateOf(false) }
    var showJobAdEntry by remember { mutableStateOf(false) }

    // Consistent material colors mapping
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant


    // Local state calculation for Primary CV parsing
    val resumes = homeViewModel.recentResumes
    val primaryResume = resumes.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Workspace",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = onSurfaceColor
                )
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Subtitle status line
            Text(
                text = "Select an optimized agent to improve your professional application documents.",
                style = MaterialTheme.typography.bodyMedium,
                color = onSurfaceVariant
            )

            // --- SECTION 1: CORE ENGINE POWER GRID ---
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "CORE ENGINES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceVariant,
                    letterSpacing = 1.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GridToolCard(
                        title = "Tailor Resume",
                        description = "Align experiences with an active job description text copy.",
                        icon = Icons.Default.AutoAwesome,
                        iconTint = Color(0xFF9C27B0),
                        backgroundColor = Color(0xFF9C27B0).copy(alpha = 0.08f),
                        outlineVariant = outlineVariant,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (homeViewModel.recentResumes.isNotEmpty() && homeViewModel.selectedResumeFullText.isEmpty()) {
                                // 🚀 Extract clean text for the workspace view engine
                                val firstResume = homeViewModel.recentResumes.first()
                                val cleanText = try {
                                    firstResume.content?.jsonObject?.get("text")?.jsonPrimitive?.content ?: ""
                                } catch (e: Exception) {
                                    ""
                                }
                                homeViewModel.selectedResumeFullText = cleanText
                            }
                            showJobAdEntry = true
                        }
                    )

                    GridToolCard(
                        title = "Cover Letter",
                        description = "Draft contextual application statements from standard requirements.",
                        icon = Icons.Default.Link,
                        iconTint = Color(0xFF4CAF50),
                        backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.08f),
                        outlineVariant = outlineVariant,
                        modifier = Modifier.weight(1f),
                        onClick = { showCoverLetterEntry = true }
                    )
                }
            }

            // --- SECTION 2: INDIVIDUAL OPTIMIZERS ---
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "RESUME OPTIMIZATION UTILITIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceVariant,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = BorderStroke(1.dp, outlineVariant)
                ) {
                    Column {
                        ListToolItem(
                            title = "Rewrite Resume Bullet",
                            subtitle = "Enhance responsibility syntax using strong impact verbs.",
                            icon = Icons.Default.Edit,
                            iconContainerColor = Color(0xFF1E293B),
                            iconTint = Color(0xFF38BDF8),
                            onSurfaceColor = onSurfaceColor,
                            onSurfaceVariant = onSurfaceVariant,
                            onClick = {
                                activeOptimizationType = OptimizationType.BULLET_REWRITE
                                showOptimizationSheet = true
                            }
                        )
                        HorizontalDivider(color = outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))
                        ListToolItem(
                            title = "Improve Professional Summary",
                            subtitle = "Structure high-level personal value declarations.",
                            icon = Icons.Default.Notes,
                            iconContainerColor = Color(0xFF2E1065),
                            iconTint = Color(0xFFC084FC),
                            onSurfaceColor = onSurfaceColor,
                            onSurfaceVariant = onSurfaceVariant,
                            onClick = {
                                activeOptimizationType = OptimizationType.SUMMARY_IMPROVE
                                showOptimizationSheet = true
                            }
                        )
                        HorizontalDivider(color = outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))
                        ListToolItem(
                            title = "Analyze Job Match Rating",
                            subtitle = "Benchmark resume keyword data matrices against active adverts.",
                            icon = Icons.Default.BarChart,
                            iconContainerColor = Color(0xFF064E3B),
                            iconTint = Color(0xFF34D399),
                            onSurfaceColor = onSurfaceColor,
                            onSurfaceVariant = onSurfaceVariant,
                            onClick = {
                                activeOptimizationType = OptimizationType.MATCH_ANALYSIS
                                showOptimizationSheet = true
                            }
                        )
                        HorizontalDivider(color = outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))
                        ListToolItem(
                            title = "ATS Check Optimization",
                            subtitle = "Run deep structural compliance scans to eliminate parsing barriers.",
                            icon = Icons.Default.FactCheck, // Premium validation tick icon
                            iconContainerColor = Color(0xFF451A03), // Sleek deep amber/brown container
                            iconTint = Color(0xFFFB923C), // Eye-catching premium orange accent
                            onSurfaceColor = onSurfaceColor,
                            onSurfaceVariant = onSurfaceVariant,
                            onClick = {
                                // This launches the custom edge-function matrix evaluation panel
                                activeOptimizationType = OptimizationType.ATS_CHECK
                                showOptimizationSheet = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showOptimizationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showOptimizationSheet = false },
            containerColor = surfaceColor,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            PromptOptimizationSheetContent(
                type = activeOptimizationType,

                onDismiss = { showOptimizationSheet = false }
            )
        }
    }

    if (showCoverLetterEntry) {
        ModalBottomSheet(
            onDismissRequest = { showCoverLetterEntry = false },
            containerColor = surfaceColor,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            CoverLetterSheetContent(
                onDismiss = { showCoverLetterEntry = false }
            )
        }
    }

    // 2. Interactive Sheet Engine for Resume Tailoring
    if (showJobAdEntry) {
        UploadJobAdView(
            selectedResumeContent = homeViewModel.selectedResumeFullText,
            onDismiss = { showJobAdEntry = false },
            onSelectResume = { showJobAdEntry = false },
            onShowPaywall = {
                showJobAdEntry = false
                homeViewModel.triggerPaywall()
            }
        )
    }
}

@Composable
fun GridToolCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    backgroundColor: Color,
    outlineVariant: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp),
//                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
fun ListToolItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconTint: Color,
    onSurfaceColor: Color,
    onSurfaceVariant: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = iconContainerColor,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(44.dp),
//            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = onSurfaceColor
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = onSurfaceVariant,
                lineHeight = 17.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}