package com.example.cvpilot.ui.util

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing

fun Modifier.shimmerLoadingAnimation(): Modifier = composed {
    val isDark = isSystemInDarkTheme()

    // Choose base shimmer color tokens matching CV Pilot's themes
    val baseColor = if (isDark) Color(0xFF2D2D2D) else Color(0xFFE0E0E0)
    val highlightColor = if (isDark) Color(0xFF3D3D3D) else Color(0xFFF5F5F5)

    val transition = rememberInfiniteTransition(label = "Shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    this.background(brush = shimmerBrush)
}

