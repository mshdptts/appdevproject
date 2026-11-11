package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.example.studentaskmanager.ui.components.TaskCard
import com.example.studentaskmanager.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarContent(
    tasks: List<Task>,
    onMarkComplete: (Task) -> Unit, // <-- UPDATED
    onTaskClick: (Int) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val tasksForSelectedDate = tasks.filter { it.dueDate == selectedDate.toString() }

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
                .padding(horizontal = 28.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Calendar", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Cream)
                Text("Track your progress", fontSize = 15.sp, color = Cream.copy(alpha = 0.85f))
            }
            Spacer(Modifier.width(16.dp))
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(DustyBlue), contentAlignment = Alignment.Center) {
                Text(Session.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // 2. Month Selector
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous", tint = Navy)
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Ink
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next", tint = Navy)
            }
        }

        Spacer(Modifier.height(16.dp))

        // 3. Days Header
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceAround) {
            daysOfWeek.forEach { day ->
                Text(day, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Ink.copy(alpha = 0.5f), modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(12.dp))

        // 4. Safe Calendar Grid
        CalendarGridSafe(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            tasks = tasks,
            onDateSelected = { selectedDate = it }
        )

        Spacer(Modifier.height(32.dp))
        Divider(color = Ink.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(16.dp))

        // 5. Tasks for Selected Date
        Text(
            text = "Tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d"))}",
            fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Ink,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(12.dp))

        if (tasksForSelectedDate.isEmpty()) {
            Text("No tasks due on this day.", color = Ink.copy(alpha = 0.5f), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
        } else {
            Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                tasksForSelectedDate.forEach { task ->
                    TaskCard(
                        task = task,
                        onMarkComplete = { onMarkComplete(task) }, // <-- UPDATED
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun CalendarGridSafe(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    tasks: List<Task>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val emptyDaysBefore = firstDayOfMonth.dayOfWeek.value - 1
    val daysInMonth = currentMonth.lengthOfMonth()

    val calendarCells = mutableListOf<LocalDate?>()
    repeat(emptyDaysBefore) { calendarCells.add(null) }
    for (day in 1..daysInMonth) { calendarCells.add(currentMonth.atDay(day)) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        calendarCells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                week.forEach { date ->
                    if (date == null) {
                        Spacer(modifier = Modifier.size(40.dp))
                    } else {
                        val isSelected = date == selectedDate
                        val isToday = date == LocalDate.now()
                        val tasksForDate = tasks.filter { it.dueDate == date.toString() && !it.completed }
                        val hasTasks = tasksForDate.isNotEmpty()
                        val markerColor = when {
                            tasksForDate.any { it.priority == "URGENT" } -> Color(0xFFB3261E)
                            tasksForDate.any { it.priority == "MEDIUM" } -> Amber
                            tasksForDate.isNotEmpty() -> Color(0xFF1E6B2E)
                            else -> Color.Transparent
                        }

                        val bgColor = when {
                            isSelected -> Navy
                            isToday -> PaleBlue
                            else -> Color.Transparent
                        }
                        val textColor = when {
                            isSelected -> Color.White
                            isToday -> Navy
                            else -> Ink
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clip(CircleShape).clickable { onDateSelected(date) }.padding(4.dp)
                        ) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
                                Text(date.dayOfMonth.toString(), color = textColor, fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                            }
                            if (hasTasks) {
                                Box(modifier = Modifier.padding(top = 4.dp).size(6.dp).clip(CircleShape).background(markerColor))
                            } else {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
                if (week.size < 7) {
                    repeat(7 - week.size) { Spacer(modifier = Modifier.size(40.dp)) }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}