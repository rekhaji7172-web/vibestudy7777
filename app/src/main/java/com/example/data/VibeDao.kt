package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VibeDao {

    // --- TASKS ---
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)


    // --- NOTES ---
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)


    // --- FLASHCARDS ---
    @Query("SELECT * FROM flashcards ORDER BY id DESC")
    fun getAllFlashcards(): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)


    // --- STUDY SESSIONS ---
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession)


    // --- MIND MAP NODES ---
    @Query("SELECT * FROM mind_map_nodes")
    fun getAllMindMapNodes(): Flow<List<MindMapNode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMindMapNode(node: MindMapNode): Long

    @Update
    suspend fun updateMindMapNode(node: MindMapNode)

    @Delete
    suspend fun deleteMindMapNode(node: MindMapNode)

    @Query("DELETE FROM mind_map_nodes")
    suspend fun clearAllMindMapNodes()


    // --- USER PROFILE ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)
}
