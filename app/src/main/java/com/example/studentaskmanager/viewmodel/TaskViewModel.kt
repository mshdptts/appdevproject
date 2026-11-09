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

    init { reload() }

    fun reload() {
        viewModelScope.launch(Dispatchers.IO) {
            _tasks.value = db.getTasks(Session.userId)
        }
    }

    fun toggle(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val nowDone = !task.completed
            db.updateTask(
                task.copy(
                    completed = nowDone,
                    completedAt = if (nowDone) LocalDate.now().toString() else null
                )
            )
            _tasks.value = db.getTasks(Session.userId)
        }
    }

    // ADDED DELETE FUNCTION
    fun delete(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteTask(task.id)
            _tasks.value = db.getTasks(Session.userId)
        }
    }
    // Add this function inside your TaskViewModel class

    fun addTask(title: String, description: String, dueDate: String, priority: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            db.addTask(Session.userId, title, description, dueDate, priority)
            _tasks.value = db.getTasks(Session.userId)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}