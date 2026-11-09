package com.example.studentaskmanager.utils

fun summaryText(pending: Int, total: Int, name: String): String {
    return when {
        total == 0 -> "Fresh start! Add your first task."
        pending == 0 -> "All caught up. You earned a break!"
        pending == 1 -> "Just one more to go!"
        pending <= 4 -> "You've got this, $name. $pending to go!"
        else -> "Big list today. Start with the easiest one!"
    }
}