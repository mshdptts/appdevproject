package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentaskmanager.ui.theme.*
import com.example.studentaskmanager.viewmodel.LoginViewModel

@Composable
fun ForgotPinScreen(
    username: String,
    viewModel: LoginViewModel = viewModel(),
    onPinResetSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var question by remember { mutableStateOf<String?>(null) }
    var answer by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 = Answer, 2 = New PIN
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // NEW: We need to save the userId once they verify their answer
    var verifiedUserId by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        viewModel.getSecurityQuestion(username) { q ->
            if (q != null) question = q else errorMessage = "User not found"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Cream).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (step == 1) {
            Text(
                text = "Security Check",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = question ?: "Loading...",
                fontSize = 18.sp,
                color = Ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it; errorMessage = null },
                label = { Text("Your Answer") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Navy)
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.verifySecurityAnswer(username, answer) { id ->
                        if (id != -1) {
                            verifiedUserId = id // Save the ID here!
                            step = 2
                        } else {
                            errorMessage = "Incorrect answer"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) {
                Text("Verify", color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(
                text = "Set New PIN",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = newPin,
                onValueChange = {
                    // FIXED: Filter out non-digits and limit to 6
                    val filtered = it.filter { char -> char.isDigit() }
                    if (filtered.length <= 6) newPin = filtered
                },
                label = { Text("6-Digit PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Navy)
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (newPin.length == 6) {
                        // FIXED: Pass the verifiedUserId into setPin
                        viewModel.setPin(verifiedUserId, newPin) {
                            onPinResetSuccess()
                        }
                    } else {
                        errorMessage = "PIN must be exactly 6 digits"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6B2E)) // Green
            ) {
                Text("Save PIN", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("Cancel", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}