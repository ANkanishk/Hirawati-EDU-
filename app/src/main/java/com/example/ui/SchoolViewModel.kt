package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Login : Screen()
    object StudentDashboard : Screen()
    object TeacherDashboard : Screen()
    object AdminDashboard : Screen()
    data class SolveTest(val testId: Int) : Screen()
}

class SchoolViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SchoolRepository(application)

    // Layout configuration
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isDarkThemeOverride = MutableStateFlow(false)
    val isDarkThemeOverride: StateFlow<Boolean> = _isDarkThemeOverride.asStateFlow()

    // Authentication States
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Data Flows from Repository
    val allStudents: StateFlow<List<User>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeachers: StateFlow<List<User>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotices: StateFlow<List<Notice>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHomework: StateFlow<List<Homework>> = repository.allHomework
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudyMaterials: StateFlow<List<StudyMaterial>> = repository.allStudyMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTests: StateFlow<List<OnlineTest>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResults: StateFlow<List<TestResult>> = repository.allResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResultsOrdered: StateFlow<List<TestResult>> = repository.allResultsOrdered
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<Event>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGalleryItems: StateFlow<List<GalleryItem>> = repository.allGalleryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state specifically for current logged in Student
    val myAttendance: StateFlow<List<Attendance>> = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == "STUDENT") {
            repository.getAttendanceForStudent(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mySubmissions: StateFlow<List<HomeworkSubmission>> = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == "STUDENT") {
            repository.getSubmissionsForStudent(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculate dynamic attendance %
    val myAttendancePercentage: StateFlow<Double> = myAttendance.map { list ->
        if (list.isEmpty()) return@map 100.0 // Default score
        val presentCount = list.count { it.status.equals("Present", ignoreCase = true) }
        (presentCount.toDouble() / list.size.toDouble()) * 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100.0)

    // AI States
    private val _aiHomeworkResponse = MutableStateFlow<String?>(null)
    val aiHomeworkResponse: StateFlow<String?> = _aiHomeworkResponse.asStateFlow()
    private val _aiHomeworkLoading = MutableStateFlow(false)
    val aiHomeworkLoading: StateFlow<Boolean> = _aiHomeworkLoading.asStateFlow()

    private val _aiDoubtResponse = MutableStateFlow<String?>(null)
    val aiDoubtResponse: StateFlow<String?> = _aiDoubtResponse.asStateFlow()
    private val _aiDoubtLoading = MutableStateFlow(false)
    val aiDoubtLoading: StateFlow<Boolean> = _aiDoubtLoading.asStateFlow()

    private val _aiSummarizeResponse = MutableStateFlow<String?>(null)
    val aiSummarizeResponse: StateFlow<String?> = _aiSummarizeResponse.asStateFlow()
    private val _aiSummarizeLoading = MutableStateFlow(false)
    val aiSummarizeLoading: StateFlow<Boolean> = _aiSummarizeLoading.asStateFlow()

    private val _aiChatHistory = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList()) // Pair of message to isBot
    val aiChatHistory: StateFlow<List<Pair<String, Boolean>>> = _aiChatHistory.asStateFlow()
    private val _aiChatLoading = MutableStateFlow(false)
    val aiChatLoading: StateFlow<Boolean> = _aiChatLoading.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // Ensure prepopulated users exist
                repository.prepopulateIfEmpty()
            } catch (e: Exception) {
                Log.e("SchoolViewModel", "Room pre-population error", e)
            }
        }
    }

    // Toggle Themes
    fun toggleDarkTheme() {
        _isDarkThemeOverride.value = !_isDarkThemeOverride.value
    }

    // Navigation and Logout
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun logout() {
        _currentUser.value = null
        _loginError.value = null
        _currentScreen.value = Screen.Login
    }

    // Login Action
    fun login(id: String, password: String, role: String) {
        viewModelScope.launch {
            _loginError.value = null
            
            // Trim inputs and perform safety checks
            val trimmedId = id.trim()
            val trimmedPassword = password.trim()
            
            if (trimmedId.isEmpty() || trimmedPassword.isEmpty()) {
                _loginError.value = "Enter valid login ID and password"
                return@launch
            }

            // Custom Admin Override password
            if (role == "ADMIN") {
                if (trimmedId.equals("admin", ignoreCase = true) && trimmedPassword == "anjali301") {
                    val adminUser = User(id = "admin", password = "anjali301", role = "ADMIN", name = "Head Administration")
                    _currentUser.value = adminUser
                    _currentScreen.value = Screen.AdminDashboard
                } else {
                    _loginError.value = "Invalid Admin details or incorrect secure password!"
                }
                return@launch
            }

            val fetched = repository.getUserById(trimmedId)
            if (fetched != null && fetched.password == trimmedPassword && fetched.role == role) {
                _currentUser.value = fetched
                when (role) {
                    "STUDENT" -> _currentScreen.value = Screen.StudentDashboard
                    "TEACHER" -> _currentScreen.value = Screen.TeacherDashboard
                }
            } else {
                _loginError.value = "Credential Mismatch! Check your credentials and role choice."
            }
        }
    }

    // Admin Controls
    fun addNewUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    fun addNewEvent(title: String, description: String, date: String, type: String) {
        viewModelScope.launch {
            repository.insertEvent(Event(title = title, description = description, date = date, type = type))
        }
    }

    fun addNewGalleryItem(albumName: String, imageUrl: String, title: String) {
        viewModelScope.launch {
            repository.insertGalleryItem(GalleryItem(albumName = albumName, imageUrl = imageUrl, title = title, date = "2026-05-26"))
        }
    }

    // Teacher Panel Controls
    fun markStudentAttendance(studentId: String, date: String, status: String) {
        viewModelScope.launch {
            repository.markAttendance(studentId, date, status)
        }
    }

    fun submitHomework(title: String, description: String, subject: String, deadline: String, classLevel: String) {
        val teacherName = _currentUser.value?.name ?: "Mrs. Anjali Sen"
        viewModelScope.launch {
            repository.insertHomework(Homework(
                title = title,
                description = description,
                subject = subject,
                deadline = deadline,
                classLevel = classLevel,
                teacherName = teacherName
            ))
        }
    }

    fun submitStudyMaterial(title: String, subject: String, category: String, chapter: String, content: String, description: String) {
        viewModelScope.launch {
            repository.insertStudyMaterial(StudyMaterial(
                title = title,
                subject = subject,
                category = category,
                chapter = chapter,
                content = content,
                description = description
            ))
        }
    }

    fun publishNotice(title: String, content: String, date: String) {
        val postedBy = _currentUser.value?.name ?: "Admin Office"
        viewModelScope.launch {
            repository.insertNotice(Notice(title = title, content = content, date = date, postedBy = postedBy))
        }
    }

    fun createOnlineTest(title: String, subject: String, classLevel: String, duration: Int, questions: List<Question>) {
        viewModelScope.launch {
            repository.insertTest(OnlineTest(
                title = title,
                subject = subject,
                classLevel = classLevel,
                durationMinutes = duration,
                questionsJson = questions.toJsonString()
            ))
        }
    }

    // Student Solutions
    fun recordHomeworkSubmission(homeworkId: Int, solutionText: String) {
        val sId = _currentUser.value?.id ?: "S101"
        viewModelScope.launch {
            repository.insertSubmission(HomeworkSubmission(
                homeworkId = homeworkId,
                studentId = sId,
                submissionText = solutionText,
                isSubmitted = true,
                submittedAt = "2026-05-26",
                feedback = "Evaluating shortly...",
                grade = "Awaiting"
            ))
        }
    }

    fun recordOnlineTestResult(testId: Int, testTitle: String, score: Int, total: Int) {
        val student = _currentUser.value ?: return
        val percentageValue = (score.toDouble() / total.toDouble()) * 100.0
        viewModelScope.launch {
            repository.insertTestResult(TestResult(
                testId = testId,
                testTitle = testTitle,
                studentId = student.id,
                studentName = student.name,
                score = score,
                totalMarks = total,
                percentage = percentageValue,
                date = "2026-05-26"
            ))
            _currentScreen.value = Screen.StudentDashboard
        }
    }

    // AI Dynamic Service Calls
    fun callAIHomeworkHelper(question: String) {
        viewModelScope.launch {
            _aiHomeworkLoading.value = true
            val systemPrompt = "You are a friendly and academic teacher at Hirawati Senior Secondary School. Keep answers highly interactive, easy for a high school student to understand, with clean bullet points and short definitions."
            val reply = GeminiService.generateContent(question, systemPrompt)
            _aiHomeworkResponse.value = reply
            _aiHomeworkLoading.value = false
        }
    }

    fun callAIDoubtSolver(subject: String, question: String) {
        viewModelScope.launch {
            _aiDoubtLoading.value = true
            val prompt = "Subject: $subject. Doubt Question: $question. Solve it step-by-step with formulas and clear notes."
            val systemPrompt = "You are an expert Subject Matter Guru. Answer with professional clarity, and present equations or steps logically."
            val reply = GeminiService.generateContent(prompt, systemPrompt)
            _aiDoubtResponse.value = reply
            _aiDoubtLoading.value = false
        }
    }

    fun callAINotesSummarizer(notesText: String) {
        viewModelScope.launch {
            _aiSummarizeLoading.value = true
            val prompt = "Please create a concise summaries list with important keywords, formulas, and 3-sentence takeaways of: $notesText"
            val systemPrompt = "You are an expert exam revision summarizer. Extract key definitions and condense text."
            val reply = GeminiService.generateContent(prompt, systemPrompt)
            _aiSummarizeResponse.value = reply
            _aiSummarizeLoading.value = false
        }
    }

    fun callAIChatbot(msgText: String) {
        if (msgText.trim().isEmpty()) return
        val currentHistory = _aiChatHistory.value.toMutableList()
        currentHistory.add(Pair(msgText, false))
        _aiChatHistory.value = currentHistory

        viewModelScope.launch {
            _aiChatLoading.value = true
            val systemPrompt = "You are 'Hira chatbot' of Hirawati Senior Secondary School. Help teachers, admin, and students navigate subjects, clarify syllabus schedules (Affiliated to CBSE, Aff No. 330923, School code 65920). Be extremely cheerful and polite!"
            
            // Build simple helper context
            val builder = StringBuilder()
            builder.append("Recent chat logs context:\n")
            currentHistory.takeLast(10).forEach {
                builder.append(if (it.second) "Bot: " else "User: ").append(it.first).append("\n")
            }
            builder.append("\nUser message: ").append(msgText)

            val reply = GeminiService.generateContent(builder.toString(), systemPrompt)
            
            val updatedHistory = _aiChatHistory.value.toMutableList()
            updatedHistory.add(Pair(reply, true))
            _aiChatHistory.value = updatedHistory
            _aiChatLoading.value = false
        }
    }

    fun clearAIChat() {
        _aiChatHistory.value = emptyList()
    }
}
