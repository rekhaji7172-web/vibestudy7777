package com.example.ui

import android.app.Application
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.FocusSynth
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.sin

class VibeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VibeDatabase.getDatabase(application)
    val repository = VibeRepository(db.vibeDao())
    val synth = FocusSynth()

    // Database Flows
    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<Note>> = repository.notes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcards: StateFlow<List<Flashcard>> = repository.flashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySession>> = repository.sessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mindMapNodes: StateFlow<List<MindMapNode>> = repository.nodes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    // --- TIMERS ---
    val timeRemaining = MutableStateFlow(25 * 60) // 25 mins by default
    val totalDurationSeconds = MutableStateFlow(25 * 60)
    val focusMode = MutableStateFlow("Pomodoro") // "Pomodoro", "Short Break", "Long Break", "Free"
    val timerStatus = MutableStateFlow("Stopped") // "Stopped", "Running", "Paused"
    private var timerJob: Job? = null

    // --- MUSIC ---
    val activeSynthMode = MutableStateFlow("None") // "None", "Binaural", "WhiteNoise", "Rain"
    val synthVolume = MutableStateFlow(0.5f)

    // --- CELEBRATIONS ---
    val levelUpCelebration = MutableStateFlow<String?>(null) // Show Level Up pop-up if set
    val unlockedBadgeCelebration = MutableStateFlow<String?>(null) // Show badge overlay if unlocked

    init {
        viewModelScope.launch {
            repository.initializeDefaultUserIfNeeded()
        }
    }

    // --- TASK MODULE ---
    fun addTask(title: String, priority: String, dueDate: String, scheduledTime: String = "") {
        viewModelScope.launch {
            if (title.isBlank()) return@launch
            val newTask = Task(
                title = title,
                priority = priority,
                dueDate = if (dueDate.isBlank()) "Today" else dueDate,
                scheduledTime = scheduledTime
            )
            repository.insertTask(newTask)
            addXP(10, "Task created! Keep it up.")
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            repository.updateTask(updated)
            if (updated.isCompleted) {
                addXP(20, "Completed task: '${task.title}'")
            } else {
                addXP(-10, "Task uncompleted")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- NOTE MODULE ---
    fun addNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            if (title.isBlank() || content.isBlank()) return@launch
            val newNote = Note(
                title = title,
                content = content,
                category = category
            )
            repository.insertNote(newNote)
            addXP(15, "Note synthesized: '${title}'")
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // --- FLASHCARDS MODULE ---
    fun addFlashcard(question: String, answer: String, category: String) {
        viewModelScope.launch {
            if (question.isBlank() || answer.isBlank()) return@launch
            val card = Flashcard(
                question = question,
                answer = answer,
                category = category
            )
            repository.insertFlashcard(card)
            addXP(15, "Flashcard added! Swipe to revise.")
        }
    }

    fun updateFlashcardConfidence(card: Flashcard, confidence: Int) {
        viewModelScope.launch {
            val updated = card.copy(
                confidence = confidence,
                reviewedCount = card.reviewedCount + 1,
                lastReviewed = System.currentTimeMillis()
            )
            repository.updateFlashcard(card)
            // Extra XP reward on Mastering a card
            val xpEarned = when (confidence) {
                3 -> 30 // Mastered
                2 -> 15 // Good
                1 -> 5  // Hard
                else -> 2 // New
            }
            addXP(xpEarned, "Flashcard confidence set to ${getConfidenceText(confidence)}")
        }
    }

    fun deleteFlashcard(card: Flashcard) {
        viewModelScope.launch {
            repository.deleteFlashcard(card)
        }
    }

    private fun getConfidenceText(c: Int) = when(c) {
        3 -> "Mastered \uD83C\uCFC6"
        2 -> "Good \uD83D\uDC4D"
        1 -> "Hard \uD83E\uDDD0"
        else -> "New 🆕"
    }

    // --- FOCUS TIMER ACTIONS ---
    fun startTimer() {
        if (timerStatus.value == "Running") return
        timerStatus.value = "Running"
        timerJob = viewModelScope.launch {
            while (timeRemaining.value > 0 && timerStatus.value == "Running") {
                delay(1000)
                timeRemaining.value -= 1
            }
            if (timeRemaining.value <= 0) {
                onTimerFinished()
            }
        }
    }

    fun pauseTimer() {
        timerStatus.value = "Paused"
        timerJob?.cancel()
    }

    fun resetTimer() {
        timerStatus.value = "Stopped"
        timerJob?.cancel()
        setFocusMode(focusMode.value) // Reset to focus mode defaults
    }

    fun setFocusMode(mode: String) {
        focusMode.value = mode
        timerStatus.value = "Stopped"
        timerJob?.cancel()
        val duration = when (mode) {
            "Pomodoro" -> 25 * 60
            "Short Break" -> 5 * 60
            "Long Break" -> 15 * 60
            "Free Focus" -> 60 * 60
            else -> 25 * 60
        }
        totalDurationSeconds.value = duration
        timeRemaining.value = duration
    }

    fun adjustTimeRemaining(deltaSeconds: Int) {
        val newVal = (timeRemaining.value + deltaSeconds).coerceIn(10, 180 * 60)
        timeRemaining.value = newVal
        if (timerStatus.value == "Stopped") {
            totalDurationSeconds.value = newVal
        }
    }

    private suspend fun onTimerFinished() {
        timerStatus.value = "Stopped"
        
        // Dynamic XP and Session Logging
        val durationMins = totalDurationSeconds.value / 60
        val isWork = focusMode.value == "Pomodoro" || focusMode.value == "Free Focus"
        val xpBonus = if (isWork) durationMins * 2 else durationMins // break sessions give single XP
        
        val finishedSession = StudySession(
            durationSeconds = totalDurationSeconds.value,
            mode = focusMode.value,
            xpEarned = xpBonus
        )
        repository.insertSession(finishedSession)
        
        // Play Chime Synthesis
        triggerChimeSynthesis()

        // Apply XP rewards
        addXP(xpBonus + 10, "Focus Session Complete! studied $durationMins mins 🔥")
        
        // Auto Reset to Pomodoro or next mode
        if (focusMode.value == "Pomodoro") {
            setFocusMode("Short Break")
        } else {
            setFocusMode("Pomodoro")
        }
    }

    private fun triggerChimeSynthesis() {
        // Simple synthetic chime using AudioTrack tone playing for 0.5s in independent coroutine!
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 22050
                val bufferSize = sampleRate / 2 // 0.5 seconds
                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize * 2,
                    AudioTrack.MODE_STATIC
                )
                val buffer = ShortArray(bufferSize)
                // Generate simple harmonic chord
                for (i in 0 until bufferSize) {
                    val time = i.toDouble() / sampleRate
                    // Chord: A4 (440Hz) + C#5 (554.37Hz) + E5 (659.25Hz)
                    val value = (sin(2.0 * Math.PI * 440.0 * time) * 0.3 +
                                 sin(2.0 * Math.PI * 554.37 * time) * 0.3 +
                                 sin(2.0 * Math.PI * 659.25 * time) * 0.3)
                    buffer[i] = (value * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
                }
                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                delay(1200)
                audioTrack.release()
            } catch (e: Exception) {
                // Ignore silent failure
            }
        }
    }


    // --- MUSIC TIMBRE CONTROL ---
    fun toggleSound(mode: String) {
        if (activeSynthMode.value == mode) {
            activeSynthMode.value = "None"
            synth.stop()
        } else {
            activeSynthMode.value = mode
            synth.start(mode)
            synth.setVolume(synthVolume.value)
        }
    }

    fun setMusicVolume(vol: Float) {
        synthVolume.value = vol
        synth.setVolume(vol)
    }


    // --- MIND MAP INTERACTION ---
    fun addMindMapNode(label: String, parentId: Int? = null, x: Float, y: Float) {
        viewModelScope.launch {
            if (label.isBlank()) return@launch
            val node = MindMapNode(parentId = parentId, label = label, x = x, y = y)
            repository.insertMindMapNode(node)
            addXP(10, "Mind Map expanded: '$label'")
        }
    }

    fun updateNodePosition(node: MindMapNode, newX: Float, newY: Float) {
        viewModelScope.launch {
            repository.updateMindMapNode(node.copy(x = newX, y = newY))
        }
    }

    fun deleteNode(node: MindMapNode) {
        viewModelScope.launch {
            repository.deleteMindMapNode(node)
        }
    }

    fun resetMindMap() {
        viewModelScope.launch {
            repository.clearMindMap()
            // Make simple structure
            val parentId = repository.insertMindMapNode(MindMapNode(label = "New Goal", x = 300f, y = 300f)).toInt()
            repository.insertMindMapNode(MindMapNode(parentId = parentId, label = "Read Chapter 1", x = 120f, y = 200f))
            repository.insertMindMapNode(MindMapNode(parentId = parentId, label = "Review formulas", x = 460f, y = 400f))
        }
    }


    // --- XP & REWARDS MANAGEMENT ---
    fun selectTheme(themeIndex: Int) {
        viewModelScope.launch {
            val prof = userProfile.value
            if (prof.unlockedThemes.split(",").contains(themeIndex.toString())) {
                repository.saveUserProfile(prof.copy(selectedThemeIndex = themeIndex))
                addXP(5, "Theme swatch changed!")
            }
        }
    }

    fun editUsername(newName: String) {
        viewModelScope.launch {
            if (newName.isBlank()) return@launch
            val prof = userProfile.value
            repository.saveUserProfile(prof.copy(username = newName))
        }
    }

    fun purchaseTheme(themeIndex: Int, costXP: Int) {
        viewModelScope.launch {
            val prof = userProfile.value
            val unlockedList = prof.unlockedThemes.split(",").toMutableList()
            if (unlockedList.contains(themeIndex.toString())) return@launch
            
            if (prof.xp >= costXP) {
                unlockedList.add(themeIndex.toString())
                val newUnlocked = unlockedList.joinToString(",")
                // Spend XP or keep XP and just unlock as milestone! Let's say milestone (so we don't reduce level)
                val updatedProf = prof.copy(
                    unlockedThemes = newUnlocked,
                    selectedThemeIndex = themeIndex,
                    xp = prof.xp - costXP // cost is spent
                )
                repository.saveUserProfile(updatedProf)
                levelUpCelebration.value = "Theme unlocked! Active theme set successfully!"
            }
        }
    }

    private fun addXP(amount: Int, message: String) {
        viewModelScope.launch {
            val prof = userProfile.value
            val currentXp = (prof.xp + amount).coerceAtLeast(0)
            val currentLevel = prof.level
            
            // Level cap threshold logic
            val xpNeededForNext = getXpNeededForLevel(currentLevel)
            
            var targetLevel = currentLevel
            var finalXp = currentXp
            
            if (finalXp >= xpNeededForNext) {
                targetLevel += 1
                finalXp -= xpNeededForNext
                
                // Triggers celebration PopUp
                levelUpCelebration.value = "LEVEL UP! You reached Level $targetLevel! \uD83C\uDFC6\u2728"
                
                // Unlock new themes on specific level milestones
                val unlockedThemes = prof.unlockedThemes.split(",").toMutableList()
                val newlyUnlockedMsg = StringBuilder()
                
                if (targetLevel >= 2 && !unlockedThemes.contains("1")) {
                    unlockedThemes.add("1")
                    newlyUnlockedMsg.append("\n• Unlocked Theme: Electric Cyan! ⚡")
                }
                if (targetLevel >= 3 && !unlockedThemes.contains("2")) {
                    unlockedThemes.add("2")
                    newlyUnlockedMsg.append("\n• Unlocked Theme: Deep Velvet Orchid! 🔮")
                }
                if (targetLevel >= 4 && !unlockedThemes.contains("3")) {
                    unlockedThemes.add("3")
                    newlyUnlockedMsg.append("\n• Unlocked Theme: Emerald Zen Garden! 🌿")
                }
                
                val updatedWithTheme = prof.copy(
                    xp = finalXp,
                    level = targetLevel,
                    unlockedThemes = unlockedThemes.distinct().joinToString(",")
                )
                repository.saveUserProfile(updatedWithTheme)
                
                if (newlyUnlockedMsg.isNotEmpty()) {
                    delay(500)
                    unlockedBadgeCelebration.value = "Celebration Rewards:$newlyUnlockedMsg"
                }

            } else {
                repository.saveUserProfile(prof.copy(xp = finalXp))
            }
        }
    }

    fun getXpNeededForLevel(level: Int): Int {
        return 100 + (level * 50) // E.g., Level 1 takes 150XP, Level 2 takes 200XP...
    }

    fun dismissCelebration() {
        levelUpCelebration.value = null
    }

    fun dismissBadge() {
        unlockedBadgeCelebration.value = null
    }

    override fun onCleared() {
        super.onCleared()
        synth.stop() // Always tear down synthesizer!
    }
}
