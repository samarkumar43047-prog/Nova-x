package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.data.repository.NovaXRepository
import com.example.util.TextToSpeechHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NovaXViewModel(application: Application) : AndroidViewModel(application) {

    val repository = NovaXRepository(application)
    private val ttsHelper = TextToSpeechHelper(application)

    // Chat State
    private val _currentSessionId = MutableStateFlow("session_1")
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessageEntity>> = _currentSessionId.flatMapLatest { sessionId ->
        repository.getMessages(sessionId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteMessages: StateFlow<List<ChatMessageEntity>> = repository.getFavoriteMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _speechEnabled = MutableStateFlow(false)
    val speechEnabled: StateFlow<Boolean> = _speechEnabled.asStateFlow()

    // Notes & Todos
    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todos: StateFlow<List<TodoEntity>> = repository.allTodos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Profile
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity())

    // Studio & Utility State
    private val _codeOutput = MutableStateFlow("")
    val codeOutput: StateFlow<String> = _codeOutput.asStateFlow()

    private val _writingOutput = MutableStateFlow("")
    val writingOutput: StateFlow<String> = _writingOutput.asStateFlow()

    private val _documentText = MutableStateFlow("Nova X AI Whitepaper v3.0\n\nAbstract:\nNova X is a next-generation decentralized AI Assistant designed for hyper-scalable edge reasoning, private document synthesis, multimodal computer vision, and real-time developer workflows. Integrated with custom vector spaces and low-latency coroutine engines, Nova X ensures 99.9% uptime across desktop, mobile, and embedded platforms.")
    val documentText: StateFlow<String> = _documentText.asStateFlow()

    private val _documentSummary = MutableStateFlow("")
    val documentSummary: StateFlow<String> = _documentSummary.asStateFlow()

    init {
        // Ensure default welcome message in chat if empty
        viewModelScope.launch {
            repository.getMessages("session_1").firstOrNull()?.let { list ->
                if (list.isEmpty()) {
                    repository.saveMessage(
                        ChatMessageEntity(
                            sessionId = "session_1",
                            sender = "nova",
                            text = "Greetings! I am **Nova X AI**, your futuristic virtual assistant. How can I help you today with coding, writing, learning, or productivity?",
                            category = "Welcome"
                        )
                    )
                }
            }
        }
    }

    fun sendMessage(
        prompt: String,
        base64Image: String? = null
    ) {
        if (prompt.isBlank() && base64Image == null) return
        val session = _currentSessionId.value

        viewModelScope.launch {
            // Save user message
            repository.saveMessage(
                ChatMessageEntity(
                    sessionId = session,
                    sender = "user",
                    text = prompt,
                    imageUri = base64Image
                )
            )

            _isGenerating.value = true

            val profile = userProfile.value ?: UserProfileEntity()
            val aiResponse = repository.generateAiResponse(
                prompt = prompt,
                personality = profile.personality,
                language = profile.preferredLanguage,
                base64Image = base64Image
            )

            // Save Nova response
            repository.saveMessage(
                ChatMessageEntity(
                    sessionId = session,
                    sender = "nova",
                    text = aiResponse
                )
            )

            _isGenerating.value = false

            if (_speechEnabled.value) {
                ttsHelper.speak(aiResponse.replace("#", "").replace("*", ""))
            }
        }
    }

    fun toggleFavorite(messageId: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(messageId, !current)
        }
    }

    fun toggleSpeechOutput() {
        _speechEnabled.value = !_speechEnabled.value
        if (!_speechEnabled.value) {
            ttsHelper.stop()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearChatHistory(_currentSessionId.value)
        }
    }

    // Coding Studio Action
    fun executeCodingTask(prompt: String, lang: String, mode: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isGenerating.value = true
            val fullPrompt = "Task Mode: $mode for Language: $lang.\nCode / Prompt:\n$prompt"
            val profile = userProfile.value ?: UserProfileEntity()
            val result = repository.generateAiResponse(
                prompt = fullPrompt,
                personality = profile.personality,
                language = profile.preferredLanguage,
                isCodingTask = true
            )
            _codeOutput.value = result
            _isGenerating.value = false
        }
    }

    // Writing Studio Action
    fun executeWritingTask(prompt: String, type: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isGenerating.value = true
            val fullPrompt = "Writing Task Type: $type.\nTopic / Outline:\n$prompt"
            val profile = userProfile.value ?: UserProfileEntity()
            val result = repository.generateAiResponse(
                prompt = fullPrompt,
                personality = profile.personality,
                language = profile.preferredLanguage
            )
            _writingOutput.value = result
            _isGenerating.value = false
        }
    }

    // Document Reader Action
    fun summarizeDocument(text: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val result = repository.generateAiResponse(
                prompt = "Please provide a clear bulleted summary, key highlights, and main takeaway for this document:\n$text"
            )
            _documentSummary.value = result
            _isGenerating.value = false
        }
    }

    // Notes
    fun addNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.saveNote(
                NoteEntity(title = title, content = content, category = category)
            )
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    // Todos
    fun addTodo(task: String, priority: String) {
        viewModelScope.launch {
            repository.saveTodo(
                TodoEntity(task = task, priority = priority)
            )
        }
    }

    fun toggleTodo(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTodoCompletion(id, !isCompleted)
        }
    }

    fun deleteTodo(id: Long) {
        viewModelScope.launch {
            repository.deleteTodo(id)
        }
    }

    // Profile Settings
    fun updateProfile(name: String, email: String, personality: String, language: String, darkTheme: Boolean) {
        viewModelScope.launch {
            repository.saveUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = name,
                    email = email,
                    personality = personality,
                    preferredLanguage = language,
                    darkThemeEnabled = darkTheme
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
