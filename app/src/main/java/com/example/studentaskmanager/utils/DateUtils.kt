package com.example.studentaskmanager.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun greeting(): String = when (LocalTime.now().hour) {
    in 5..11 -> "Good morning"
    in 12..17 -> "Good afternoon"
    else -> "Good evening"
}

fun todayLabel(): String = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))

class DueInfo(val text: String, val overdue: Boolean)

fun dueInfo(dueDate: String): DueInfo? {
    if (dueDate.isBlank()) return null
    return try {
        val date = LocalDate.parse(dueDate)
        val days = ChronoUnit.DAYS.between(LocalDate.now(), date)
        when {
            days < 0L -> DueInfo("Overdue by ${-days}d", true)
            days == 0L -> DueInfo("TODAY", false)
            days == 1L -> DueInfo("Tomorrow", false)
            else -> DueInfo(date.format(DateTimeFormatter.ofPattern("MMM d")), false)
        }
    } catch (e: Exception) { null }
}