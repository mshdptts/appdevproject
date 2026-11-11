package com.example.studentaskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.studentaskmanager.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun ProgressContent(
    tasks: List<Task>,
    completedDates: List<String> // <-- Now takes history dates
) {
    val totalCompleted = completedDates.size

    val startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val completedThisWeek = completedDates.count {
        val date = try { LocalDate.parse(it) } catch (e: Exception) { null }
        date != null && !date.isBefore(startOfWeek) && !date.isAfter(LocalDate.now())
    }

    val distinctDates = completedDates.mapNotNull {
        try { LocalDate.parse(it) } catch (e: Exception) { null }
    }.distinct().sortedDescending()

    var currentStreak = 0
    var checkDate = LocalDate.now()

    if (distinctDates.isNotEmpty() && distinctDates.first() == LocalDate.now().minusDays(1)) {
        checkDate = LocalDate.now().minusDays(1)
    }

    for (date in distinctDates) {
        if (date == checkDate) {
            currentStreak++
            checkDate = checkDate.minusDays(1)
        } else if (date.isBefore(checkDate)) {
            break
        }
    }

    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Navy)
                .statusBarsPadding()
                .padding(horizontal = 28.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Progress", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Cream)
                Text("Your achievements", fontSize = 15.sp, color = Cream.copy(alpha = 0.85f))
            }
            Spacer(Modifier.width(16.dp))
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(DustyBlue), contentAlignment = Alignment.Center) {
                Text(Session.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PaleBlue)
        ) {
            Row(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current Streak", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy.copy(alpha = 0.7f))
                    Text("$currentStreak Day${if (currentStreak != 1) "s" else ""}", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Navy)
                }
                Text(text = "🔥", fontSize = 48.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("This Week", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Ink, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach { date ->
                val isCompleted = distinctDates.contains(date)
                val isToday = date == LocalDate.now()

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(date.dayOfWeek.name.take(1), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isToday) Navy else Ink.copy(alpha = 0.5f))
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) Navy else Color.White)
                            .border(width = 1.dp, color = if (isToday) Navy else Ink.copy(alpha = 0.1f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(date.dayOfMonth.toString(), color = if (isCompleted) Color.White else Ink, fontWeight = if (isToday || isCompleted) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("This Week", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Ink.copy(alpha = 0.6f))
                    Text("$completedThisWeek", fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Color(0xFF1E6B2E))
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("All Time", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Ink.copy(alpha = 0.6f))
                    Text("$totalCompleted", fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Navy)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Divider(color = Ink.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(16.dp))

        Text("Task History", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Ink, modifier = Modifier.padding(horizontal = 24.dp))

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous", tint = Navy)
            }
            Text(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next", tint = Navy)
            }
        }

        Spacer(Modifier.height(8.dp))

        CalendarGridSafe(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            tasks = tasks,
            onDateSelected = { selectedDate = it }
        )

        Spacer(Modifier.height(32.dp))
    }
}