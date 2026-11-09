package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.studentaskmanager.data.Session
import com.example.studentaskmanager.data.Task
import com.example.studentaskmanager.ui.theme.*
import com.example.studentaskmanager.utils.dueInfo
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TaskDetailScreen(
    task: Task,
    onBack: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val due = dueInfo(task.dueDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Navy Header with Back Button
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
                text = "Tasks",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Cream,
                modifier = Modifier.weight(1f)
            )

            // Avatar
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(DustyBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Session.username.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        // 2. Overlapping White Content Card
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
                // Title
                Text(
                    text = task.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Ink,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.height(24.dp))

                // REAL CALENDAR
                RealTaskCalendar(task = task)

                Spacer(Modifier.height(16.dp))

                // Due Date Label
                if (due != null) {
                    Text(
                        text = if (task.completed) "Completed" else "Due ${due.text}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (due.overdue && !task.completed) Color(0xFFB3261E) else Navy
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Description Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Cream)
                        .padding(16.dp)
                ) {
                    Text(
                        text = task.description.ifBlank { "No description provided." },
                        fontSize = 14.sp,
                        color = Ink,
                        lineHeight = 22.sp
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Edit Button
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MutedIcon)
                    ) {
                        Text("EDIT", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Make Complete Button
                    Button(
                        onClick = onToggle,
                        modifier = Modifier.weight(1.5f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (task.completed) Color(0xFF1E6B2E) else Color(0xFF1E6B2E)
                        )
                    ) {
                        Text(
                            text = if (task.completed) "COMPLETED" else "MAKE COMPLETE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Delete Button
                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
                ) {
                    Text("DELETE", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RealTaskCalendar(task: Task) {
    // Try to parse the task's due date. If it fails or is empty, use today.
    val taskDate = try { LocalDate.parse(task.dueDate) } catch (e: Exception) { null }
    val initialDate = taskDate ?: LocalDate.now()
    val currentMonth = YearMonth.from(initialDate)

    val firstDayOfMonth = currentMonth.atDay(1)
    val emptyDaysBefore = firstDayOfMonth.dayOfWeek.value - 1
    val daysInMonth = currentMonth.lengthOfMonth()

    val calendarCells = mutableListOf<LocalDate?>()
    repeat(emptyDaysBefore) { calendarCells.add(null) }
    for (day in 1..daysInMonth) {
        calendarCells.add(currentMonth.atDay(day))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Cream.copy(alpha = 0.5f)) // Very light grey/cream background
            .padding(16.dp)
    ) {
        // Month Header
        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Ink,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // Days of Week
        val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink.copy(alpha = 0.5f),
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Calendar Grid (Using FlowRow or manual chunking to avoid scroll issues inside a scroll view)
        // Since we are inside a verticalScroll, we cannot use LazyVerticalGrid, so we chunk the list.
        calendarCells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                week.forEach { date ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val isDueDate = date == taskDate
                            val isToday = date == LocalDate.now()

                            // Highlight logic
                            val bgColor = when {
                                isDueDate && task.completed -> Color(0xFF1E6B2E) // Green if completed
                                isDueDate -> Navy // Navy if pending
                                isToday -> PaleBlue // Light blue for today
                                else -> Color.Transparent
                            }

                            val textColor = when {
                                isDueDate -> Color.White
                                isToday -> Navy
                                else -> Ink
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = if (isDueDate || isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                // Pad the last week with empty boxes so alignment stays perfect
                if (week.size < 7) {
                    repeat(7 - week.size) {
                        Spacer(modifier = Modifier.size(32.dp).padding(2.dp))
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}