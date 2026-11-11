package com.example.studentaskmanager

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentaskmanager.data.Session
import com.example.studentaskmanager.ui.screens.*
import com.example.studentaskmanager.ui.theme.Cream
import com.example.studentaskmanager.ui.theme.Ink
import com.example.studentaskmanager.ui.theme.Navy
import com.example.studentaskmanager.ui.theme.StudentTaskManagerTheme
import com.example.studentaskmanager.viewmodel.LoginViewModel
import com.example.studentaskmanager.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedUsername = prefs.getString("saved_username", null)
        val savedUserId = prefs.getInt("saved_user_id", -1)

        setContent {
            StudentTaskManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    // START AT SPLASH SCREEN
                    var currentScreen by remember { mutableStateOf("splash") }

                    var signupUsername by remember { mutableStateOf("") }
                    var selectedTaskId by remember { mutableStateOf<Int?>(null) }
                    var isAddingTask by remember { mutableStateOf(false) }
                    var taskToEditId by remember { mutableStateOf<Int?>(null) }

                    val taskViewModel: TaskViewModel = viewModel()
                    val loginViewModel: LoginViewModel = viewModel()
                    val tasks by taskViewModel.tasks.collectAsState()

                    // Helper to fully log out and reset
                    val logoutAndReset = {
                        prefs.edit().clear().apply()
                        Session.userId = 0
                        Session.username = ""
                        currentScreen = "login"
                    }

                    when (currentScreen) {
                        // --- NEW SPLASH SCREEN BLOCK ---
                        "splash" -> {
                            SplashScreen(
                                onTimeout = {
                                    // Route to PIN if they are remembered, otherwise Login
                                    if (savedUsername != null && savedUserId > 0) {
                                        currentScreen = "pin"
                                    } else {
                                        currentScreen = "login"
                                    }
                                }
                            )
                        }

                        "pin" -> {
                            val actualUserId = if (savedUserId > 0) savedUserId else Session.userId
                            PinScreen(
                                username = savedUsername ?: "",
                                userId = actualUserId,
                                viewModel = loginViewModel,
                                onLoginSuccess = { currentScreen = "home" },
                                onForgotPin = { currentScreen = "forgot_pin" },
                                onSwitchAccount = { logoutAndReset() }
                            )
                        }
                        "forgot_pin" -> {
                            ForgotPinScreen(
                                username = savedUsername ?: "",
                                viewModel = loginViewModel,
                                onPinResetSuccess = { currentScreen = "pin" },
                                onBack = { currentScreen = "pin" }
                            )
                        }
                        "login" -> {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoggedIn = {
                                    prefs.edit().putString("saved_username", Session.username).putInt("saved_user_id", Session.userId).apply()
                                    loginViewModel.checkHasPin(Session.userId) { hasPin ->
                                        currentScreen = if (hasPin) "home" else "set_pin"
                                    }
                                },
                                onCreateAccountClick = { typedName ->
                                    signupUsername = typedName
                                    currentScreen = "signup"
                                }
                            )
                        }
                        "signup" -> {
                            SignUpScreen(
                                username = signupUsername,
                                viewModel = loginViewModel,
                                onAccountCreated = { newUserId ->
                                    Session.userId = newUserId.toInt()
                                    Session.username = signupUsername
                                    currentScreen = "set_pin"
                                },
                                onBackToLogin = { currentScreen = "login" }
                            )
                        }
                        "set_pin" -> {
                            var newPin by remember { mutableStateOf("") }
                            Column(
                                modifier = Modifier.fillMaxSize().background(Cream).padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("ARIA", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Navy)
                                Spacer(Modifier.height(32.dp))

                                Text(
                                    text = "Secure your\naccount.",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,
                                    color = Ink,
                                    lineHeight = 42.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Create a 6-digit PIN for quick access next time.",
                                    fontSize = 15.sp,
                                    color = Ink.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(Modifier.height(40.dp))

                                OutlinedTextField(
                                    value = newPin,
                                    onValueChange = {
                                        val filtered = it.filter { char -> char.isDigit() }
                                        if (filtered.length <= 6) newPin = filtered
                                    },
                                    label = { Text("6-Digit PIN") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Navy)
                                )

                                Spacer(Modifier.height(32.dp))

                                Button(
                                    onClick = {
                                        if (newPin.length == 6) {
                                            loginViewModel.setPin(Session.userId, newPin) { success ->
                                                if (success) {
                                                    prefs.edit().putString("saved_username", Session.username).putInt("saved_user_id", Session.userId).apply()
                                                    currentScreen = "home"
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6B2E))
                                ) {
                                    Text("Save PIN & Enter App", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                        "home" -> {
                            val screenState = when {
                                isAddingTask -> "add"
                                taskToEditId != null -> "edit"
                                selectedTaskId != null -> "detail"
                                else -> "main"
                            }

                            AnimatedContent(
                                targetState = screenState,
                                transitionSpec = {
                                    if (targetState == "add" || targetState == "edit" || targetState == "detail") {
                                        slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) + fadeIn(tween(300)) togetherWith fadeOut(tween(150))
                                    } else {
                                        fadeIn(tween(150)) togetherWith slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)) + fadeOut(tween(300))
                                    }
                                },
                                label = "ScreenTransition"
                            ) { state ->
                                when (state) {
                                    "add" -> AddTaskScreen(
                                        onBack = { isAddingTask = false },
                                        onSave = { t, d, date, p -> taskViewModel.addTask(t, d, date, p) { isAddingTask = false } }
                                    )
                                    "edit" -> {
                                        val taskToEdit = tasks.find { it.id == taskToEditId }
                                        if (taskToEdit != null) {
                                            AddTaskScreen(
                                                taskToEdit = taskToEdit,
                                                onBack = { taskToEditId = null },
                                                onSave = { t, d, date, p ->
                                                    taskViewModel.updateTask(taskToEdit.copy(title = t, description = d, dueDate = date, priority = p)) { taskToEditId = null }
                                                }
                                            )
                                        }
                                    }
                                    "detail" -> {
                                        val task = tasks.find { it.id == selectedTaskId }
                                        if (task != null) {
                                            TaskDetailScreen(
                                                task = task,
                                                onBack = { selectedTaskId = null },
                                                onMarkComplete = { taskViewModel.markComplete(task) },
                                                onDelete = { taskViewModel.delete(task); selectedTaskId = null },
                                                onEdit = { taskToEditId = task.id }
                                            )
                                        }
                                    }
                                    else -> HomeScreen(
                                        vm = taskViewModel,
                                        onAddClick = { isAddingTask = true },
                                        onTaskClick = { selectedTaskId = it },
                                        onLockApp = {
                                            currentScreen = if (savedUserId > 0) "pin" else "login"
                                        },
                                        onResetAccount = {
                                            logoutAndReset()
                                        },
                                        onChangePin = {
                                            currentScreen = "set_pin"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}