package com.example.cvpilot.ui.component

import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue

@Composable
fun animatedGradientBrush(
    colors: List<Color> = listOf(Color(0xFF6200EE), Color(0xFF03DAC6), Color(0xFFBB86FC))
): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    return Brush.linearGradient(
        colors = colors,
        start = Offset(offset, offset),
        end = Offset(offset + 500f, offset + 500f)
    )
}