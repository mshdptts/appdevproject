package com.example.studentaskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentaskmanager.ui.screens.*
import com.example.studentaskmanager.ui.theme.StudentTaskManagerTheme
import com.example.studentaskmanager.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StudentTaskManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    var currentScreen by remember { mutableStateOf("login") }
                    var signupUsername by remember { mutableStateOf("") }

                    // Navigation states
                    var selectedTaskId by remember { mutableStateOf<Int?>(null) }
                    var isAddingTask by remember { mutableStateOf(false) }
                    var taskToEditId by remember { mutableStateOf<Int?>(null) } // <-- NEW STATE

                    val taskViewModel: TaskViewModel = viewModel()
                    val tasks by taskViewModel.tasks.collectAsState()

                    when (currentScreen) {
                        "login" -> {
                            LoginScreen(
                                onLoggedIn = { currentScreen = "home" },
                                onCreateAccountClick = { typedName ->
                                    signupUsername = typedName
                                    currentScreen = "signup"
                                }
                            )
                        }
                        "signup" -> {
                            SignUpScreen(
                                username = signupUsername,
                                onAccountCreated = { currentScreen = "login" },
                                onBackToLogin = { currentScreen = "login" }
                            )
                        }
                        "home" -> {
                            // Determine current state
                            val screenState = when {
                                isAddingTask -> "add"
                                taskToEditId != null -> "edit"
                                selectedTaskId != null -> "detail"
                                else -> "main"
                            }

                            AnimatedContent(
                                targetState = screenState,
                                transitionSpec = {
                                    // Slide UP for entering Add or Edit
                                    if (targetState == "add" || targetState == "edit" || targetState == "detail") {
                                        slideInVertically(
                                            initialOffsetY = { fullHeight -> fullHeight },
                                            animationSpec = tween(300)
                                        ) + fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(150))
                                    } else {
                                        // Slide DOWN for exiting
                                        fadeIn(animationSpec = tween(150)) togetherWith
                                                slideOutVertically(
                                                    targetOffsetY = { fullHeight -> fullHeight },
                                                    animationSpec = tween(300)
                                                ) + fadeOut(animationSpec = tween(300))
                                    }
                                },
                                label = "ScreenTransition"
                            ) { state ->
                                when (state) {
                                    "add" -> {
                                        AddTaskScreen(
                                            onBack = { isAddingTask = false },
                                            onSave = { title, desc, date, priority ->
                                                taskViewModel.addTask(title, desc, date, priority) {
                                                    isAddingTask = false
                                                }
                                            }
                                        )
                                    }
                                    "edit" -> {
                                        val taskToEdit = tasks.find { it.id == taskToEditId }
                                        if (taskToEdit != null) {
                                            AddTaskScreen(
                                                taskToEdit = taskToEdit, // Pass the task data
                                                onBack = { taskToEditId = null }, // Go back to detail
                                                onSave = { title, desc, date, priority ->
                                                    // Update the task in the database
                                                    taskViewModel.updateTask(
                                                        taskToEdit.copy(
                                                            title = title,
                                                            description = desc,
                                                            dueDate = date,
                                                            priority = priority
                                                        )
                                                    ) {
                                                        taskToEditId = null // Go back to detail on success
                                                    }
                                                }
                                            )
                                        } else {
                                            LaunchedEffect(Unit) { taskToEditId = null }
                                        }
                                    }
                                    "detail" -> {
                                        val task = tasks.find { it.id == selectedTaskId }
                                        if (task != null) {
                                            TaskDetailScreen(
                                                task = task,
                                                onBack = { selectedTaskId = null },
                                                onToggle = { taskViewModel.toggle(task) },
                                                onDelete = {
                                                    taskViewModel.delete(task)
                                                    selectedTaskId = null
                                                },
                                                onEdit = { taskToEditId = task.id } // <-- TRIGGER EDIT MODE
                                            )
                                        } else {
                                            LaunchedEffect(Unit) { selectedTaskId = null }
                                        }
                                    }
                                    else -> {
                                        HomeScreen(
                                            vm = taskViewModel,
                                            onAddClick = { isAddingTask = true },
                                            onTaskClick = { taskId -> selectedTaskId = taskId }
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
}