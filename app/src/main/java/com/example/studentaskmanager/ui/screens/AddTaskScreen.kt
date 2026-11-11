package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
import com.example.studentaskmanager.data.Task
import com.example.studentaskmanager.ui.theme.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    taskToEdit: Task? = null, // <-- NEW PARAMETER
    onBack: () -> Unit,
    onSave: (title: String, description: String, dueDate: String, priority: String) -> Unit
) {
    // Pre-fill the fields if editing
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var dueDate by remember { mutableStateOf(taskToEdit?.dueDate ?: "") }
    var priority by remember { mutableStateOf(taskToEdit?.priority ?: "LOW") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Calendar Popup State (Pre-select date if editing)
    var showDatePicker by remember { mutableStateOf(false) }
    val initialDateMillis = if (!taskToEdit?.dueDate.isNullOrBlank()) {
        try {
            LocalDate.parse(taskToEdit?.dueDate).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        } catch (e: Exception) { null }
    } else null

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Navy Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Navy)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Cream)
            }

            Text(
                // Change Header Text based on mode
                text = if (taskToEdit == null) "New Task" else "Edit Task",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Cream,
                modifier = Modifier.weight(1f)
            )
        }

        // 2. Overlapping White Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = null },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Navy,
                        unfocusedContainerColor = Cream.copy(alpha = 0.3f)
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Navy,
                        unfocusedContainerColor = Cream.copy(alpha = 0.3f)
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Due Date Field (Clickable to open Calendar)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Cream.copy(alpha = 0.3f))
                        .clickable { showDatePicker = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (dueDate.isEmpty()) "Select Due Date" else dueDate,
                            color = if (dueDate.isEmpty()) Ink.copy(alpha = 0.6f) else Ink,
                            fontSize = 16.sp
                        )
                        Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = Navy)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Priority Selection (Custom Chips)
                Text(
                    text = "Priority Level",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val priorities = listOf("LOW", "MEDIUM", "URGENT")
                    priorities.forEach { p ->
                        val isSelected = priority == p
                        val bgColor = when (p) {
                            "URGENT" -> Color(0xFFFDE0DE)
                            "MEDIUM" -> Color(0xFFFFF0CC)
                            else -> Color(0xFFDDEBDD)
                        }
                        val borderColor = if (isSelected) Navy else Color.Transparent

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = bgColor,
                            border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                            modifier = Modifier.weight(1f).clickable { priority = p }
                        ) {
                            Text(
                                text = p,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Ink,
                                modifier = Modifier.padding(vertical = 12.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(errorMessage!!, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(Modifier.height(32.dp))

                // Save / Update Button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = "Please enter a task title."
                        } else {
                            onSave(title, description, dueDate, priority)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6B2E))
                ) {
                    Text(
                        // Change Button Text based on mode
                        text = if (taskToEdit == null) "SAVE TASK" else "UPDATE TASK",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Cancel Button
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
                ) {
                    Text("CANCEL", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // --- THE REAL CALENDAR POPUP ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            dueDate = formatter.format(calendar.time)
                        }
                    }
                ) {
                    Text("OK", color = Navy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = MutedIcon)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}