package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentaskmanager.data.Session
import com.example.studentaskmanager.data.Task
import com.example.studentaskmanager.ui.components.TaskCard
import com.example.studentaskmanager.ui.theme.Cream
import com.example.studentaskmanager.ui.theme.DustyBlue
import com.example.studentaskmanager.ui.theme.Ink
import com.example.studentaskmanager.ui.theme.Navy

@Composable
fun TasksContent(
    tasks: List<Task>,
    onMarkComplete: (Task) -> Unit, // <-- CHANGED HERE
    onTaskClick: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Task") }

    // Filter logic
    val filteredTasks = tasks.filter { task ->
        val matchesSearch = task.title.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Pending Task" -> !task.completed
            "Completed Task" -> task.completed
            else -> true // All Task
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        // 1. Navy Header (MATCHED TO HOME SCREEN)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Navy)
                .statusBarsPadding()
                .padding(horizontal = 28.dp, vertical = 28.dp), // Matched padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Tasks",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp, // Large, elegant font
                    color = Cream
                )
                Text(
                    text = "Manage your day",
                    fontSize = 15.sp,
                    color = Cream.copy(alpha = 0.85f)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Avatar (Matched to Home Screen)
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(DustyBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Session.username.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        // 2. Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Enter title of task to search", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Ink.copy(alpha = 0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        // 3. Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("All Task", "Pending Task", "Completed Task")
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) Navy else Color.White,
                    modifier = Modifier.clickable { selectedFilter = filter }
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else Ink,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 4. Task List
        if (filteredTasks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tasks found.", color = Ink.copy(alpha = 0.5f), fontFamily = FontFamily.Serif)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onMarkComplete = { onMarkComplete(task) }, // <-- CHANGED HERE
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}