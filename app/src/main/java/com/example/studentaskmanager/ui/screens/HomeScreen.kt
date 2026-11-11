package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentaskmanager.data.DatabaseHelper
import com.example.studentaskmanager.data.Session
import com.example.studentaskmanager.data.Task
import com.example.studentaskmanager.ui.components.*
import com.example.studentaskmanager.ui.theme.Cream
import com.example.studentaskmanager.ui.theme.Ink
import com.example.studentaskmanager.viewmodel.TaskViewModel

@Composable
fun HomeScreen(
    vm: TaskViewModel = viewModel(),
    onAddClick: () -> Unit = {},
    onTaskClick: (Int) -> Unit = {},
    onLockApp: () -> Unit = {},
    onResetAccount: () -> Unit = {},
    onChangePin: () -> Unit = {}
) {
    LaunchedEffect(Unit) { vm.reload() }

    val tasks by vm.tasks.collectAsState()
    val completedDates by vm.completedDates.collectAsState()
    var currentTab by rememberSaveable { mutableStateOf("home") }

    // Fetch security question for the profile page
    val db = DatabaseHelper.get(androidx.compose.ui.platform.LocalContext.current)
    val securityQuestion = remember { db.getSecurityQuestionById(Session.userId) ?: "Not set" }

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            AppBottomBar(
                selected = currentTab,
                onSelect = { currentTab = it },
                onAdd = onAddClick
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
            when (currentTab) {
                "home" -> HomeContent(
                    userName = Session.username,
                    tasks = tasks,
                    onMarkComplete = { vm.markComplete(it) },
                    onTaskClick = onTaskClick
                )
                "tasks" -> TasksContent(
                    tasks = tasks,
                    onMarkComplete = { vm.markComplete(it) },
                    onTaskClick = onTaskClick
                )
                "progress" -> ProgressContent(
                    tasks = tasks,
                    completedDates = completedDates
                )
                "profile" -> {
                    // CONNECTED THE NEW PROFILE SCREEN HERE
                    ProfileContent(
                        securityQuestion = securityQuestion,
                        onLockApp = onLockApp,
                        onResetAccount = onResetAccount,
                        onChangePin = onChangePin
                    )
                }
            }
        }
    }
}

// ... (HomeContent stays the same) ...

@Composable
fun HomeContent(
    userName: String,
    tasks: List<Task>,
    onMarkComplete: (Task) -> Unit,
    onTaskClick: (Int) -> Unit
) {
    val pending = tasks.count { !it.completed }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { HomeHeader(name = userName) }

        item {
            MotivationCard(
                pending = pending,
                total = tasks.size,
                name = userName,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }

        item {
            Text(
                text = "Tasks for Today",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Ink,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
            )
        }

        if (tasks.isEmpty()) {
            item {
                Text(
                    text = "Nothing here yet. Tap + to add your first task.",
                    color = Ink.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        items(tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onMarkComplete = { onMarkComplete(task) },
                onClick = { onTaskClick(task.id) },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }
    }
}