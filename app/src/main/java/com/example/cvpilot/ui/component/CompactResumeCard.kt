package com.example.cvpilot.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.material3.ElevatedCard
import com.example.cvpilot.models.Resume
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.cvpilot.ui.util.shimmerLoadingAnimation

@Composable
fun CompactResumeCard(resume: Resume, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()

    val iconSurfaceColor = if (isDark) Color(0xFF0D233A) else Color(0xFFE3F2FD)
    val labelColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
    val cardBgColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color(0xFFFFF9F0)


    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .width(220.dp) // Fixed width for horizontal scrolling
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardBgColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Icon and Type
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = iconSurfaceColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Description,
                        null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Resume",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            // Bottom Column: Name and Title
            Column {
                Text(
                    text = resume.name.ifEmpty { "Untitled" },
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = resume.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CompactResumeSkeletonCard() {
    val isDark = isSystemInDarkTheme()
    val cardBgColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color(0xFFFFF9F0)

    ElevatedCard(
        modifier = Modifier
            .width(220.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cardBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Box Placeholder
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .shimmerLoadingAnimation(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Transparent
                ) {}
                Spacer(Modifier.width(10.dp))
                // Label Category Bar
                Surface(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .shimmerLoadingAnimation(),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Resume Name Line
                Surface(
                    modifier = Modifier
                        .width(140.dp)
                        .height(18.dp)
                        .shimmerLoadingAnimation(),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
                // Sub-headline Title Line
                Surface(
                    modifier = Modifier
                        .width(100.dp)
                        .height(12.dp)
                        .shimmerLoadingAnimation(),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
            }
        }
    }
}