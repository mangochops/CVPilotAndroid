package com.example.cvpilot.features.Profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.large
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(60.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("User", style = MaterialTheme.typography.titleLarge)
                    Text("0 Credits Remaining", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Simple List-like items for Account/Tools
        Text("ACCOUNT", style = MaterialTheme.typography.labelLarge)
        ListItem(
            headlineContent = { Text("Edit Profile") },
            leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
        )

        ListItem(
            headlineContent = { Text("Upgrade to Premium") },
            leadingContent = { Icon(Icons.Default.Star, contentDescription = null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
        )

        Button(
            onClick = { /* Logout */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Sign Out")
        }
    }
}

