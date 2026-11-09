package com.example.studentaskmanager.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper private constructor(context: Context) :
// Bumped version to 2 so it recreates the tables with the new password column
    SQLiteOpenHelper(context, "StudentTasks.db", null, 2) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                    "password TEXT NOT NULL)" // <-- ADDED PASSWORD HERE
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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tasks")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // ---------- USERS ----------

    // 1. Create a new user with a password
    fun addUser(username: String, password: String): Boolean {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        return writableDatabase.insert("users", null, values) != -1L
    }

    // 2. Check if the username and password match. Returns ID or -1 if failed.
    fun authenticateUser(username: String, password: String): Int {
        readableDatabase.rawQuery(
            "SELECT id FROM users WHERE username = ? AND password = ?",
            arrayOf(username, password)
        ).use { c ->
            if (c.moveToFirst()) return c.getInt(0)
        }
        return -1
    }

    // ---------- TASKS ----------
    // (Your task functions remain exactly the same)
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

    fun getTasks(ownerId: Int): List<Task> {
        val list = mutableListOf<Task>()
        readableDatabase.rawQuery(
            "SELECT * FROM tasks WHERE owner_id = ? ORDER BY completed, due_date",
            arrayOf(ownerId.toString())
        ).use { c -> while (c.moveToNext()) list.add(c.toTask()) }
        return list
    }

    fun getTask(id: Int): Task? {
        readableDatabase.rawQuery(
            "SELECT * FROM tasks WHERE id = ?", arrayOf(id.toString())
        ).use { c -> return if (c.moveToFirst()) c.toTask() else null }
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
        return writableDatabase.update("tasks", values, "id = ?", arrayOf(task.id.toString())) > 0
    }

    fun deleteTask(id: Int): Boolean =
        writableDatabase.delete("tasks", "id = ?", arrayOf(id.toString())) > 0

    // ---------- helper ----------

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

    companion object {
        @Volatile private var instance: DatabaseHelper? = null

        fun get(context: Context): DatabaseHelper =
            instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
    }
}