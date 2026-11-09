package com.example.studentaskmanager.data

object Session {
    var userId: Int = -1
    var username: String = ""

    fun logout() {
        userId = -1
        username = ""
    }
}