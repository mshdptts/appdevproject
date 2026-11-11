package com.example.studentaskmanager.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, "StudentTasks.db", null, 4) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                    "password TEXT NOT NULL, " +
                    "pin TEXT, " +
                    "security_question TEXT, " +
                    "security_answer TEXT)"
        )
        db.execSQL(
            "CREATE TABLE tasks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "owner_id INTEGER NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "description TEXT, " +
                    "due_date TEXT, " +
                    "priority TEXT NOT NULL DEFAULT 'LOW', " +
                    "completed INTEGER NOT NULL DEFAULT 0, " +
                    "completed_at TEXT, " +
                    "FOREIGN KEY(owner_id) REFERENCES users(id) ON DELETE CASCADE)"
        )
        db.execSQL(
            "CREATE TABLE task_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "task_id INTEGER, " +
                    "completed_at TEXT NOT NULL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS task_history")
        db.execSQL("DROP TABLE IF EXISTS tasks")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // UPDATED: Returns Long (User ID) instead of Boolean
    fun addUser(username: String, password: String, securityQuestion: String, securityAnswer: String): Long {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("security_question", securityQuestion)
            put("security_answer", securityAnswer.lowercase().trim())
        }
        return writableDatabase.insert("users", null, values)
    }

    fun authenticateUser(username: String, password: String): Int {
        readableDatabase.rawQuery(
            "SELECT id FROM users WHERE username = ? AND password = ?",
            arrayOf(username, password)
        ).use { c ->
            if (c.moveToFirst()) return c.getInt(0)
        }
        return -1
    }

    fun updatePin(userId: Int, pin: String): Boolean {
        val values = ContentValues().apply { put("pin", pin) }
        return writableDatabase.update("users", values, "id = ?", arrayOf(userId.toString())) > 0
    }

    fun authenticatePin(userId: Int, pin: String): Boolean {
        readableDatabase.rawQuery(
            "SELECT id FROM users WHERE id = ? AND pin = ?",
            arrayOf(userId.toString(), pin)
        ).use { c ->
            return c.moveToFirst()
        }
    }

    // NEW: Check if user has a PIN (for password logins)
    fun hasPin(userId: Int): Boolean {
        readableDatabase.rawQuery("SELECT pin FROM users WHERE id = ?", arrayOf(userId.toString())).use { c ->
            if (c.moveToFirst()) {
                val pin = c.getString(0)
                return !pin.isNullOrBlank()
            }
        }
        return false
    }

    fun getSecurityQuestion(username: String): String? {
        readableDatabase.rawQuery(
            "SELECT security_question FROM users WHERE username = ?",
            arrayOf(username)
        ).use { c ->
            if (c.moveToFirst()) return c.getString(0)
        }
        return null
    }

    fun verifySecurityAnswer(username: String, answer: String): Int {
        readableDatabase.rawQuery(
            "SELECT id FROM users WHERE username = ? AND security_answer = ?",
            arrayOf(username, answer.lowercase().trim())
        ).use { c ->
            if (c.moveToFirst()) return c.getInt(0)
        }
        return -1
    }

    // ... (The rest of the task functions remain exactly the same) ...
    fun addTask(ownerId: Int, title: String, description: String, dueDate: String, priority: String): Boolean {
        val values = ContentValues().apply {
            put("owner_id", ownerId)
            put("title", title)
            put("description", description)
            put("due_date", dueDate)
            put("priority", priority)
        }
        return writableDatabase.insert("tasks", null, values) != -1L
    }

    fun markTaskComplete(taskId: Int, userId: Int, date: String): Boolean {
        val values = ContentValues().apply {
            put("completed", 1)
            put("completed_at", date)
        }
        val success = writableDatabase.update("tasks", values, "id = ?", arrayOf(taskId.toString())) > 0

        if (success) {
            val historyValues = ContentValues().apply {
                put("user_id", userId)
                put("task_id", taskId)
                put("completed_at", date)
            }
            writableDatabase.insert("task_history", null, historyValues)
        }
        return success
    }

    fun getTasks(ownerId: Int): List<Task> {
        val list = mutableListOf<Task>()
        readableDatabase.rawQuery(
            "SELECT * FROM tasks WHERE owner_id = ? ORDER BY completed, due_date",
            arrayOf(ownerId.toString())
        ).use { c -> while (c.moveToNext()) list.add(c.toTask()) }
        return list
    }

    fun updateTask(task: Task): Boolean {
        val values = ContentValues().apply {
            put("title", task.title)
            put("description", task.description)
            put("due_date", task.dueDate)
            put("priority", task.priority)
            put("completed", if (task.completed) 1 else 0)
            put("completed_at", task.completedAt)
        }
        val success = writableDatabase.update("tasks", values, "id = ?", arrayOf(task.id.toString())) > 0
        if (success) {
            if (task.completed && task.completedAt != null) {
                val cursor = readableDatabase.rawQuery("SELECT id FROM task_history WHERE task_id = ?", arrayOf(task.id.toString()))
                if (!cursor.moveToFirst()) {
                    val historyValues = ContentValues().apply {
                        put("user_id", task.ownerId)
                        put("task_id", task.id)
                        put("completed_at", task.completedAt)
                    }
                    writableDatabase.insert("task_history", null, historyValues)
                }
                cursor.close()
            } else if (!task.completed) {
                writableDatabase.delete("task_history", "task_id = ?", arrayOf(task.id.toString()))
            }
        }
        return success
    }

    fun deleteTask(id: Int): Boolean =
        writableDatabase.delete("tasks", "id = ?", arrayOf(id.toString())) > 0

    fun getCompletedDates(userId: Int): List<String> {
        val list = mutableListOf<String>()
        readableDatabase.rawQuery(
            "SELECT completed_at FROM task_history WHERE user_id = ?",
            arrayOf(userId.toString())
        ).use { c -> while (c.moveToNext()) { list.add(c.getString(0)) } }
        return list
    }

    private fun Cursor.toTask() = Task(
        id = getInt(getColumnIndexOrThrow("id")),
        ownerId = getInt(getColumnIndexOrThrow("owner_id")),
        title = getString(getColumnIndexOrThrow("title")),
        description = getString(getColumnIndexOrThrow("description")) ?: "",
        dueDate = getString(getColumnIndexOrThrow("due_date")) ?: "",
        priority = getString(getColumnIndexOrThrow("priority")) ?: "LOW",
        completed = getInt(getColumnIndexOrThrow("completed")) == 1,
        completedAt = getString(getColumnIndexOrThrow("completed_at"))
    )
    // Add this inside DatabaseHelper.kt

    fun getSecurityQuestionById(userId: Int): String? {
        readableDatabase.rawQuery(
            "SELECT security_question FROM users WHERE id = ?",
            arrayOf(userId.toString())
        ).use { c ->
            if (c.moveToFirst()) return c.getString(0)
        }
        return null
    }

    companion object {
        @Volatile private var instance: DatabaseHelper? = null
        fun get(context: Context): DatabaseHelper =
            instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
    }
}