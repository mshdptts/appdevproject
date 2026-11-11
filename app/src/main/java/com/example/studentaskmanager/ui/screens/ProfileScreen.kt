package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentaskmanager.data.Session
import com.example.studentaskmanager.ui.theme.*

@Composable
fun ProfileContent(
    securityQuestion: String,
    onLockApp: () -> Unit,
    onResetAccount: () -> Unit,
    onChangePin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // 1. Main Header
        Text(
            text = "PROFILE",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Navy,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 2. PROFILE Section
        SectionHeader("PROFILE")
        SettingsCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle profile edit later */ }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Navy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = Session.username.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                }
                Spacer(Modifier.width(16.dp))
                // User Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Session.username,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Ink
                    )
                    Text(
                        text = "           ",
                        fontSize = 14.sp,
                        color = Ink.copy(alpha = 0.6f)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Edit Profile",
                    tint = Ink.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // 3. SECURITY Section
        SectionHeader("SECURITY")
        SettingsCard {
            // Change PIN
            SettingsRow(
                icon = Icons.Default.Lock,
                title = "Change PIN",
                onClick = onChangePin
            )

            HorizontalDivider(color = Ink.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            // Security Question
            SettingsRow(
                icon = Icons.Default.HelpOutline,
                title = "Security question",
                subtitle = securityQuestion,
                onClick = { /* Handle editing question later */ }
            )

            HorizontalDivider(color = Ink.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            // Remove PIN
            SettingsRow(
                icon = Icons.Default.LockOpen,
                title = "Remove PIN",
                isDestructive = true,
                onClick = { /* Handle removing PIN later */ }
            )
        }

        Spacer(Modifier.height(24.dp))

        // 4. ABOUT Section
        SectionHeader("ABOUT")
        SettingsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ARIA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Ink
                )
                Text(
                    text = "Version 1.5",
                    fontSize = 14.sp,
                    color = Ink.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Ink.copy(alpha = 0.1f), thickness = 1.dp)
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Developer",
                        tint = Ink.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Developer", fontSize = 16.sp, color = Ink)
                    Spacer(Modifier.weight(1f))
                    Text("MSHDPTTS", fontSize = 16.sp, color = Ink.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // 5. Action Buttons
        OutlinedButton(
            onClick = onLockApp,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Navy),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Navy)
        ) {
            Text("Lock app", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onResetAccount,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFB3261E)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB3261E))
        ) {
            Text("Reset account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(48.dp)) // Bottom padding
    }
}

// --- Helper Composables for a clean layout ---

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Ink.copy(alpha = 0.5f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Ink.copy(alpha = 0.08f))
    ) {
        content()
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val color = if (isDestructive) Color(0xFFB3261E) else Ink

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = color)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 14.sp, color = Ink.copy(alpha = 0.5f))
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = color.copy(alpha = 0.5f)
        )
    }
}