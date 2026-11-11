package com.example.studentaskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentaskmanager.data.DatabaseHelper
import com.example.studentaskmanager.data.Session
import com.example.studentaskmanager.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TaskViewModel(app: Application) : AndroidViewModel(app) {
    private val db = DatabaseHelper.get(app)

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _completedDates = MutableStateFlow<List<String>>(emptyList())
    val completedDates: StateFlow<List<String>> = _completedDates

    init {
        if (Session.userId != 0) reload()
    }

    fun reload() {
        viewModelScope.launch(Dispatchers.IO) {
            _tasks.value = db.getTasks(Session.userId)
            _completedDates.value = db.getCompletedDates(Session.userId)
        }
    }

    fun markComplete(task: Task) {
        if (task.completed) return // Prevent reversing from here
        viewModelScope.launch(Dispatchers.IO) {
            db.markTaskComplete(task.id, Session.userId, LocalDate.now().toString())
            reload()
        }
    }

    fun addTask(title: String, description: String, dueDate: String, priority: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            db.addTask(Session.userId, title, description, dueDate, priority)
            reload()
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun updateTask(updatedTask: Task, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateTask(updatedTask)
            reload()
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteTask(task.id)
            reload()
        }
    }
}