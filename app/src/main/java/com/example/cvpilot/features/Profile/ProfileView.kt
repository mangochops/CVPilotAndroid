package com.example.cvpilot.features.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.automirrored.filled.Logout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {},
    onLogoutNavigate: () -> Unit = {},
    modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // MARK: - PROFILE HEADER CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Initials Avatar Circle Profile Fallback
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = viewModel.userName.take(2).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = viewModel.userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = viewModel.userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        // Dynamic Subscription Badge Engine Status
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (viewModel.isPremiumUser) Color(0xFFFF9800).copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = if (viewModel.isPremiumUser) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Text(
                                text = if (viewModel.isPremiumUser) "Premium Member" else "${viewModel.creditsRemaining} Credits Remaining",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // MARK: - ACCOUNT SECTION
            ProfileSection(title = "ACCOUNT") {
                SettingsRow(
                    icon = Icons.Default.AccountCircle,
                    title = "Edit Profile",
                    iconColor = Color(0xFF2196F3),
                    onClick = { onNavigate("edit_profile") }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp)
                SettingsRow(
                    icon = Icons.Default.Stars,
                    title = "Upgrade to Premium",
                    iconColor = Color(0xFFFF9800),
                    onClick = { onNavigate("premium") }
                )
            }

            // MARK: - TOOLS SECTION
//            ProfileSection(title = "RESUME TOOLS") {
//                SettingsRow(
//                    icon = Icons.Default.Description,
//                    title = "My Resumes",
//                    iconColor = Color(0xFF9C27B0),
//                    onClick = { onNavigate("resumes") }
//                )
//                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp)
//                SettingsRow(
//                    icon = Icons.Default.BarChart,
//                    title = "ATS Analytics",
//                    iconColor = Color(0xFF4CAF50),
//                    onClick = { onNavigate("analytics") }
//                )
//            }

            // MARK: - SUPPORT & LEGAL
            ProfileSection(title = "SUPPORT") {
                SettingsRow(
                    icon = Icons.Default.Settings,
                    title = "Contact Support",
                    iconColor = Color(0xFF00BCD4),
                    onClick = { onNavigate("support") }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp)
                SettingsRow(
                    icon = Icons.Default.Shield,
                    title = "Privacy Policy",
                    iconColor = Color.Gray,
                    onClick = { onNavigate("privacy") }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp)
                SettingsRow(
                    icon = Icons.Default.Shield,
                    title = "Terms & Conditions",
                    iconColor = Color.Gray,
                    onClick = { onNavigate("privacy") }
                )
            }

            // MARK: - LOGOUT BUTTON
            Button(
                onClick = { viewModel.signOut(onSignOutSuccess = onLogoutNavigate) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Text(
                text = "CV Pilot v1.0.4",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(title, fontSize = 16.sp) },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}