package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class VibeRepository(private val dao: VibeDao) {

    val tasks: Flow<List<Task>> = dao.getAllTasks()
    val notes: Flow<List<Note>> = dao.getAllNotes()
    val flashcards: Flow<List<Flashcard>> = dao.getAllFlashcards()
    val sessions: Flow<List<StudySession>> = dao.getAllSessions()
    val nodes: Flow<List<MindMapNode>> = dao.getAllMindMapNodes()
    val userProfile: Flow<UserProfile?> = dao.getUserProfileFlow()

    // Task Management
    suspend fun insertTask(task: Task) = dao.insertTask(task)
    suspend fun updateTask(task: Task) = dao.updateTask(task)
    suspend fun deleteTask(task: Task) = dao.deleteTask(task)
    suspend fun deleteTaskById(id: Int) = dao.deleteTaskById(id)

    // Note Management
    suspend fun insertNote(note: Note) = dao.insertNote(note)
    suspend fun deleteNote(note: Note) = dao.deleteNote(note)

    // Flashcard Management
    suspend fun insertFlashcard(flashcard: Flashcard) = dao.insertFlashcard(flashcard)
    suspend fun updateFlashcard(flashcard: Flashcard) = dao.updateFlashcard(flashcard)
    suspend fun deleteFlashcard(flashcard: Flashcard) = dao.deleteFlashcard(flashcard)

    // StudySession Management
    suspend fun insertSession(session: StudySession) = dao.insertSession(session)

    // Mind Map Node Management
    suspend fun insertMindMapNode(node: MindMapNode): Long = dao.insertMindMapNode(node)
    suspend fun updateMindMapNode(node: MindMapNode) = dao.updateMindMapNode(node)
    suspend fun deleteMindMapNode(node: MindMapNode) = dao.deleteMindMapNode(node)
    suspend fun clearMindMap() = dao.clearAllMindMapNodes()

    // User Profile Management
    suspend fun getUserProfileDirect(): UserProfile? = dao.getUserProfileDirect()
    suspend fun saveUserProfile(profile: UserProfile) = dao.saveUserProfile(profile)

    suspend fun initializeDefaultUserIfNeeded() {
        val current = dao.getUserProfileDirect()
        if (current == null) {
            val default = UserProfile(
                id = 1,
                username = "Vibe Student",
                level = 1,
                xp = 0,
                streak = 3,
                lastActiveDate = System.currentTimeMillis(),
                selectedThemeIndex = 0,
                unlockedThemes = "0"
            )
            dao.saveUserProfile(default)
            
            // Also insert some sample mindmap nodes so it's not completely blank!
            val rootId = dao.insertMindMapNode(MindMapNode(label = "VibeStudy Goals", x = 320f, y = 300f)).toInt()
            dao.insertMindMapNode(MindMapNode(parentId = rootId, label = "Consistency \uD83D\uDD25", x = 150f, y = 170f))
            dao.insertMindMapNode(MindMapNode(parentId = rootId, label = "Focus Hours ⚡", x = 450f, y = 180f))
            dao.insertMindMapNode(MindMapNode(parentId = rootId, label = "Smash Finals \uD83C\uDFC6", x = 300f, y = 470f))
        }
    }
}
