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

@Composable
fun CompactCoverLetterCard(letter: CoverLetter, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .width(220.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFFFF9F0) // Warm premium tone for cover letters
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
                    color = Color(0xFFFFECB3),
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
                Text("Cover Letter", style = MaterialTheme.typography.labelMedium)
            }

            Text(
                text = letter.companyName ?: "New Application",
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}