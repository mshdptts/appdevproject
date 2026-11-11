package com.example.studentaskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentaskmanager.data.DatabaseHelper
import com.example.studentaskmanager.data.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(app: Application) : AndroidViewModel(application = app) {
    private val db = DatabaseHelper.get(app)

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        val cleanName = username.trim()
        if (cleanName.isEmpty() || password.isEmpty()) { onResult(false); return }
        viewModelScope.launch {
            val userId = withContext(Dispatchers.IO) { db.authenticateUser(cleanName, password) }
            if (userId != -1) {
                Session.userId = userId
                Session.username = cleanName
                onResult(true)
            } else onResult(false)
        }
    }

    fun signUp(username: String, password: String, secQ: String, secA: String, onResult: (Long) -> Unit) {
        val cleanName = username.trim()
        if (cleanName.isEmpty() || password.isEmpty() || secA.isBlank()) { onResult(-1L); return }
        viewModelScope.launch {
            val id = withContext(Dispatchers.IO) { db.addUser(cleanName, password, secQ, secA) }
            onResult(id)
        }
    }

    fun checkHasPin(userId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val hasPin = withContext(Dispatchers.IO) { db.hasPin(userId) }
            onResult(hasPin)
        }
    }

    fun setPin(userId: Int, pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) { db.updatePin(userId, pin) }
            onResult(success)
        }
    }

    fun verifyPin(userId: Int, pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) { db.authenticatePin(userId, pin) }
            onResult(success)
        }
    }

    fun getSecurityQuestion(username: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val q = withContext(Dispatchers.IO) { db.getSecurityQuestion(username) }
            onResult(q)
        }
    }

    fun verifySecurityAnswer(username: String, answer: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val id = withContext(Dispatchers.IO) { db.verifySecurityAnswer(username, answer) }
            onResult(id)
        }
    }
}