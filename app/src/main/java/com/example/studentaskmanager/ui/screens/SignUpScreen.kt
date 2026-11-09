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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentaskmanager.ui.theme.Cream
import com.example.studentaskmanager.ui.theme.Ink
import com.example.studentaskmanager.ui.theme.Navy
import com.example.studentaskmanager.viewmodel.LoginViewModel

@Composable
fun SignUpScreen(
    username: String,
    viewModel: LoginViewModel = viewModel(),
    onAccountCreated: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You're here.\nLet's secure\nthis account.",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = Ink,
            lineHeight = 46.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Create a password for $username to keep your tasks safe.",
            fontSize = 16.sp,
            color = Ink.copy(alpha = 0.6f),
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Create Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; errorMessage = null },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (password.isBlank()) {
                    errorMessage = "Password cannot be empty"
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    viewModel.signUp(username, password) { success ->
                        if (success) {
                            onAccountCreated()
                        } else {
                            errorMessage = "Error creating account. Try a different name."
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Navy)
        ) {
            Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Cancel & Return to Login", color = Navy, fontWeight = FontWeight.Bold)
        }
    }
}