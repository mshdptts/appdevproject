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

    // Handles logging in
    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        val cleanName = username.trim()
        if (cleanName.isEmpty() || password.isEmpty()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val userId = withContext(Dispatchers.IO) {
                db.authenticateUser(cleanName, password)
            }

            if (userId != -1) {
                Session.userId = userId
                Session.username = cleanName
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    // Handles creating a new account
    fun signUp(username: String, password: String, onResult: (Boolean) -> Unit) {
        val cleanName = username.trim()
        if (cleanName.isEmpty() || password.isEmpty()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                db.addUser(cleanName, password)
            }
            onResult(success)
        }
    }
}