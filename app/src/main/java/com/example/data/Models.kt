package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val priority: String, // "High", "Medium", "Low"
    val isCompleted: Boolean = false,
    val dueDate: String = "",
    val scheduledTime: String = ""
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // e.g. "Biology", "Maths", "Personal"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val answer: String,
    val category: String,
    val confidence: Int = 0, // 0 = New, 1 = Hard, 2 = Good, 3 = Mastered
    val reviewedCount: Int = 0,
    val lastReviewed: Long = 0L
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val durationSeconds: Int,
    val mode: String, // "Pomodoro", "Short Break", "Long Break", "Free"
    val timestamp: Long = System.currentTimeMillis(),
    val xpEarned: Int = 0
)

@Entity(tableName = "mind_map_nodes")
data class MindMapNode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parentId: Int? = null, // ID of parent node, or null for root
    val label: String,
    val x: Float,
    val y: Float
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single user row
    val username: String = "Study Buddy",
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 1,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val selectedThemeIndex: Int = 0, // 0: Indigo Slate, 1: Electric Cyan, 2: Cyber Purple, 3: Emerald Zen
    val unlockedThemes: String = "0" // Comma-separated list of unlocked theme indices: "0,1,2,3"
)
