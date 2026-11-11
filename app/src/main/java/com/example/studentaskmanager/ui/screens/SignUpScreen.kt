package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    username: String,
    viewModel: LoginViewModel = viewModel(),
    onAccountCreated: (Long) -> Unit, // <-- UPDATED: Now passes the new User ID back
    onBackToLogin: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var securityAnswer by remember { mutableStateOf("") }

    val questions = listOf("What is your mother's maiden name?", "What was the name of your first pet?", "What city were you born in?")
    var expanded by remember { mutableStateOf(false) }
    var selectedQuestion by remember { mutableStateOf(questions[0]) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Cream).verticalScroll(rememberScrollState()).padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome\naboard.",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = Ink,
            lineHeight = 46.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Let's secure your ARIA account, $username.",
            fontSize = 16.sp,
            color = Ink.copy(alpha = 0.6f),
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it; errorMessage = null },
            label = { Text("Create Password") }, visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Ink, unfocusedTextColor = Ink, focusedBorderColor = Navy)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it; errorMessage = null },
            label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Ink, unfocusedTextColor = Ink, focusedBorderColor = Navy)
        )

        Spacer(Modifier.height(24.dp))

        Text("Security Question", fontWeight = FontWeight.Bold, color = Ink)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = selectedQuestion,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                questions.forEach { q ->
                    DropdownMenuItem(text = { Text(q) }, onClick = { selectedQuestion = q; expanded = false })
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = securityAnswer, onValueChange = { securityAnswer = it; errorMessage = null },
            label = { Text("Your Answer") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Ink, unfocusedTextColor = Ink, focusedBorderColor = Navy)
        )

        if (errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (password.isBlank() || securityAnswer.isBlank()) {
                    errorMessage = "Please fill in all fields"
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    // UPDATED: Capturing the Long ID and checking if it's not -1
                    viewModel.signUp(username, password, selectedQuestion, securityAnswer) { newId ->
                        if (newId != -1L) {
                            onAccountCreated(newId)
                        } else {
                            errorMessage = "Error creating account. Try a different username."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Navy)
        ) {
            Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onBackToLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Cancel & Return to Login", color = Navy, fontWeight = FontWeight.Bold)
        }
    }
}