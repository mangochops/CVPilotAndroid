package com.example.cvpilot.ui.component

import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier

@Composable
fun StreamingText(text: String, isLoading: Boolean) {
    val displayedText = remember { mutableStateOf("") }

    LaunchedEffect(text) {
        if (text.isNotEmpty()) {
            displayedText.value = ""

            text.forEach { char ->
                displayedText.value += char
                delay(5) // typing speed
            }
        }
    }

    val scrollState = rememberScrollState()

    LaunchedEffect(displayedText.value) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Text(
        text = displayedText.value + if (isLoading) "▍" else "",
        modifier = Modifier.verticalScroll(scrollState),
        style = MaterialTheme.typography.bodyMedium
    )
}