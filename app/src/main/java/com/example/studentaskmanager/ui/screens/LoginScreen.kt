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
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoggedIn: () -> Unit,
    onCreateAccountClick: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            text = "Welcome back.\nLet's conquer\nyour day.",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = Ink,
            lineHeight = 46.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Log in to see your tasks and keep the momentum going.",
            fontSize = 16.sp,
            color = Ink.copy(alpha = 0.6f),
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it; errorMessage = null },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password") },
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
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all fields"
                } else {
                    viewModel.login(username, password) { success ->
                        if (success) {
                            onLoggedIn()
                        } else {
                            errorMessage = "Incorrect username or password."
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
            Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (username.isBlank()) {
                    errorMessage = "Type a username above first to create an account."
                } else {
                    onCreateAccountClick(username)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Create New Account", color = Navy, fontWeight = FontWeight.Bold)
        }
    }
}