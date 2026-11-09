package com.example.studentaskmanager.data

data class Task(
    val id: Int = 0,
    val ownerId: Int = 0,
    val title: String,
    val description: String = "",
    val dueDate: String = "",
    val completed: Boolean = false,
    val completedAt: String? = null,
    val priority: String = "LOW"
)