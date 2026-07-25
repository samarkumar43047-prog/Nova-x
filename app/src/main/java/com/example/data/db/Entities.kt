package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String = "default_session",
    val sender: String, // "user" or "nova"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val imageUri: String? = null,
    val codeLanguage: String? = null,
    val category: String = "General"
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val task: String,
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // "High", "Medium", "Low"
    val dueDate: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Commander",
    val email: String = "commander@novax.ai",
    val personality: String = "Friendly & Powerful", // Friendly, Developer, Professional, Creative
    val preferredLanguage: String = "English",
    val darkThemeEnabled: Boolean = true,
    val isEncrypted: Boolean = true
)
