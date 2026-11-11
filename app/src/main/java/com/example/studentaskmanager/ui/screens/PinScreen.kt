package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentaskmanager.ui.theme.*
import com.example.studentaskmanager.viewmodel.LoginViewModel

@Composable
fun PinScreen(
    username: String,
    userId: Int, // <-- NEW: Passed directly from MainActivity
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPin: () -> Unit,
    onSwitchAccount: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pin) {
        if (pin.length == 6) {
            val currentPin = pin // Save the pin we are checking
            viewModel.verifyPin(userId, currentPin) { success ->
                if (pin == currentPin) { // Only process if user hasn't typed something else
                    if (success) {
                        onLoginSuccess()
                    } else {
                        errorMessage = "Incorrect PIN. Try again."
                        pin = ""
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Cream).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Text("ARIA", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Navy)
        Spacer(Modifier.height(32.dp))

        Text(
            text = "Welcome back,\n$username.",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            color = Ink,
            lineHeight = 42.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Enter your 6-digit PIN to unlock your tasks.",
            fontSize = 15.sp,
            color = Ink.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            for (i in 0 until 6) {
                val isFilled = i < pin.length
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) Navy else Color.Transparent)
                        .border(2.dp, Navy, CircleShape)
                )
            }
        }

        if (errorMessage != null) {
            Spacer(Modifier.height(24.dp))
            Text(errorMessage!!, color = Color(0xFFB3261E), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.weight(1f))

        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "x")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        if (key.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (key == "x") {
                                            if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                            errorMessage = null
                                        } else {
                                            if (pin.length < 6) {
                                                pin += key
                                                errorMessage = null
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (key == "x") {
                                    Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = Navy)
                                } else {
                                    Text(key, fontSize = 32.sp, fontFamily = FontFamily.Serif, color = Navy)
                                }
                            }
                        } else {
                            Spacer(Modifier.size(72.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onForgotPin) {
                Text("Forgot PIN?", color = Navy, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onSwitchAccount) {
                Text("Switch Account", color = Ink.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}