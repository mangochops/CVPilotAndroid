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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import com.example.cvpilot.models.CoverLetter
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.cvpilot.ui.util.shimmerLoadingAnimation

@Composable
fun CompactCoverLetterCard(letter: CoverLetter, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()

    // Dynamic container and content colors for dark/light premium consistency
    val cardBgColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color(0xFFFFF9F0)
    val iconSurfaceColor = if (isDark) Color(0xFF3E2C00) else Color(0xFFFFECB3)
    val textColor = MaterialTheme.colorScheme.onSurface
    val labelColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .width(220.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardBgColor // Warm premium tone for cover letters
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = iconSurfaceColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Email,
                        null,
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text("Cover Letter", style = MaterialTheme.typography.labelMedium, color = labelColor)
            }

            Text(
                text = letter.companyName ?: "New Application",
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                color = textColor,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = letter.jobTitle ?: "New Job",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CompactCoverLetterSkeletonCard() {
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
                        .width(70.dp)
                        .height(14.dp)
                        .shimmerLoadingAnimation(),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Company Name Skeleton Line
                Surface(
                    modifier = Modifier
                        .width(130.dp)
                        .height(18.dp)
                        .shimmerLoadingAnimation(),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
                // Job Title Skeleton Line
                Surface(
                    modifier = Modifier
                        .width(90.dp)
                        .height(12.dp)
                        .shimmerLoadingAnimation(),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
            }
        }
    }
}