package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

data class Question(
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val score: Int = 1
)

// Converters inside code for simplicity and robust execution
fun List<Question>.toJsonString(): String {
    val array = JSONArray()
    for (q in this) {
        val obj = JSONObject()
        obj.put("text", q.questionText)
        val optsArray = JSONArray()
        q.options.forEach { optsArray.put(it) }
        obj.put("options", optsArray)
        obj.put("correct", q.correctOptionIndex)
        obj.put("score", q.score)
        array.put(obj)
    }
    return array.toString()
}

fun parseQuestionsJson(jsonStr: String): List<Question> {
    val list = mutableListOf<Question>()
    if (jsonStr.isEmpty()) return list
    try {
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val text = obj.getString("text")
            val optsArray = obj.getJSONArray("options")
            val options = mutableListOf<String>()
            for (j in 0 until optsArray.length()) {
                options.add(optsArray.getString(j))
            }
            val correct = obj.getInt("correct")
            val score = obj.optInt("score", 1)
            list.add(Question(text, options, correct, score))
        }
    } catch (e: Exception) {
        Log.e("QuestionsConverter", "Error parsing questions JSON", e)
    }
    return list
}

class SchoolRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)

    val userDao = db.userDao()
    val attendanceDao = db.attendanceDao()
    val homeworkDao = db.homeworkDao()
    val homeworkSubmissionDao = db.homeworkSubmissionDao()
    val studyMaterialDao = db.studyMaterialDao()
    val onlineTestDao = db.onlineTestDao()
    val testResultDao = db.testResultDao()
    val eventDao = db.eventDao()
    val galleryItemDao = db.galleryItemDao()
    val noticeDao = db.noticeDao()

    // Users
    val allStudents: Flow<List<User>> = userDao.getAllStudentsFlow()
    val allTeachers: Flow<List<User>> = userDao.getAllTeachersFlow()

    suspend fun getUserById(id: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserById(id)
    }

    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        userDao.deleteUser(user)
    }

    // Attendance
    val allAttendanceList: Flow<List<Attendance>> = attendanceDao.getAllAttendanceFlow()

    fun getAttendanceForStudent(studentId: String): Flow<List<Attendance>> {
        return attendanceDao.getAttendanceForStudent(studentId)
    }

    suspend fun markAttendance(studentId: String, date: String, status: String) = withContext(Dispatchers.IO) {
        val existing = attendanceDao.getAttendanceForStudentAndDate(studentId, date)
        if (existing != null) {
            attendanceDao.insertAttendance(existing.copy(status = status))
        } else {
            attendanceDao.insertAttendance(Attendance(studentId = studentId, date = date, status = status))
        }
    }

    // Homework
    val allHomework: Flow<List<Homework>> = homeworkDao.getAllHomeworkFlow()

    fun getHomeworkForClass(classLevel: String): Flow<List<Homework>> {
        return homeworkDao.getHomeworkForClass(classLevel)
    }

    suspend fun insertHomework(homework: Homework) = withContext(Dispatchers.IO) {
        homeworkDao.insertHomework(homework)
    }

    suspend fun deleteHomework(homework: Homework) = withContext(Dispatchers.IO) {
        homeworkDao.deleteHomework(homework)
    }

    // Homework Submissions
    fun getSubmissionsForStudent(studentId: String): Flow<List<HomeworkSubmission>> {
        return homeworkSubmissionDao.getSubmissionsForStudent(studentId)
    }

    fun getSubmissionsForHomework(homeworkId: Int): Flow<List<HomeworkSubmission>> {
        return homeworkSubmissionDao.getSubmissionsForHomework(homeworkId)
    }

    suspend fun getSubmission(homeworkId: Int, studentId: String): HomeworkSubmission? = withContext(Dispatchers.IO) {
        homeworkSubmissionDao.getSubmission(homeworkId, studentId)
    }

    suspend fun insertSubmission(submission: HomeworkSubmission) = withContext(Dispatchers.IO) {
        homeworkSubmissionDao.insertSubmission(submission)
    }

    // Study Materials
    val allStudyMaterials: Flow<List<StudyMaterial>> = studyMaterialDao.getAllMaterials()

    suspend fun insertStudyMaterial(material: StudyMaterial) = withContext(Dispatchers.IO) {
        studyMaterialDao.insertMaterial(material)
    }

    suspend fun deleteStudyMaterial(material: StudyMaterial) = withContext(Dispatchers.IO) {
        studyMaterialDao.deleteMaterial(material)
    }

    // Online Tests
    val allTests: Flow<List<OnlineTest>> = onlineTestDao.getAllTests()

    fun getTestsByClass(classLevel: String): Flow<List<OnlineTest>> {
        return onlineTestDao.getTestsByClass(classLevel)
    }

    suspend fun getTestById(id: Int): OnlineTest? = withContext(Dispatchers.IO) {
        onlineTestDao.getTestById(id)
    }

    suspend fun insertTest(test: OnlineTest) = withContext(Dispatchers.IO) {
        onlineTestDao.insertTest(test)
    }

    suspend fun deleteTest(test: OnlineTest) = withContext(Dispatchers.IO) {
        onlineTestDao.deleteTest(test)
    }

    // Test Results
    val allResults: Flow<List<TestResult>> = testResultDao.getAllResults()
    val allResultsOrdered: Flow<List<TestResult>> = testResultDao.getAllResultsOrdered()

    fun getResultsForStudent(studentId: String): Flow<List<TestResult>> {
        return testResultDao.getResultsForStudent(studentId)
    }

    suspend fun insertTestResult(result: TestResult) = withContext(Dispatchers.IO) {
        testResultDao.insertResult(result)
    }

    // Events
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    suspend fun insertEvent(event: Event) = withContext(Dispatchers.IO) {
        eventDao.insertEvent(event)
    }

    suspend fun deleteEvent(event: Event) = withContext(Dispatchers.IO) {
        eventDao.deleteEvent(event)
    }

    // Gallery Item
    val allGalleryItems: Flow<List<GalleryItem>> = galleryItemDao.getAllGalleryItems()

    suspend fun insertGalleryItem(item: GalleryItem) = withContext(Dispatchers.IO) {
        galleryItemDao.insertGalleryItem(item)
    }

    suspend fun deleteGalleryItem(item: GalleryItem) = withContext(Dispatchers.IO) {
        galleryItemDao.deleteGalleryItem(item)
    }

    // Study Notices
    val allNotices: Flow<List<Notice>> = noticeDao.getAllNotices()

    suspend fun insertNotice(notice: Notice) = withContext(Dispatchers.IO) {
        noticeDao.insertNotice(notice)
    }

    suspend fun deleteNotice(notice: Notice) = withContext(Dispatchers.IO) {
        noticeDao.deleteNotice(notice)
    }

    // Database Pre-population helper
    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // If "admin" user is not found, we pre-populate all mock data
        if (userDao.getUserById("admin") == null) {
            Log.d("Prepopulate", "Prepopulating database with rich school metadata")
            
            // 1. Users
            val admin = User(id = "admin", password = "anjali301", role = "ADMIN", name = "Head Administration")
            val student1 = User(id = "S101", password = "password", role = "STUDENT", name = "Aarav Sharma", classLevel = "10th A", rollNo = 12)
            val student2 = User(id = "S102", password = "password", role = "STUDENT", name = "Ishaan Verma", classLevel = "10th A", rollNo = 15)
            val student3 = User(id = "S103", password = "password", role = "STUDENT", name = "Priya Patel", classLevel = "10th A", rollNo = 22)
            val student4 = User(id = "S104", password = "password", role = "STUDENT", name = "Karan Malhotra", classLevel = "10th A", rollNo = 8)
            val teacher1 = User(id = "T201", password = "password", role = "TEACHER", name = "Mrs. Anjali Sen", subject = "Mathematics")
            val teacher2 = User(id = "T202", password = "password", role = "TEACHER", name = "Mr. Rajesh Shah", subject = "Physics")

            userDao.insertUser(admin)
            userDao.insertUser(student1)
            userDao.insertUser(student2)
            userDao.insertUser(student3)
            userDao.insertUser(student4)
            userDao.insertUser(teacher1)
            userDao.insertUser(teacher2)

            // 2. Attendance (Aarav, Ishaan, Priya)
            val attList = listOf(
                Attendance(studentId = "S101", date = "2026-05-22", status = "Present"),
                Attendance(studentId = "S101", date = "2026-05-23", status = "Present"),
                Attendance(studentId = "S101", date = "2026-05-24", status = "Present"),
                Attendance(studentId = "S101", date = "2026-05-25", status = "Absent"),
                Attendance(studentId = "S101", date = "2026-05-26", status = "Present"),

                Attendance(studentId = "S102", date = "2026-05-25", status = "Present"),
                Attendance(studentId = "S102", date = "2026-05-26", status = "Present"),
                Attendance(studentId = "S103", date = "2026-05-25", status = "Present"),
                Attendance(studentId = "S103", date = "2026-05-26", status = "Present")
            )
            attendanceDao.insertAll(attList)

            // 3. Notices
            noticeDao.insertNotice(Notice(title = "Welcome Back!", content = "Hirawati School welcomes all boarders and day scholars back for the final semester term starting Monday. Full uniform guidelines apply.", date = "2026-05-24", postedBy = "Admin"))
            noticeDao.insertNotice(Notice(title = "Trigonometry Mock Test Live", content = "10th grade students are advised to attempt the online mock MCQ test covering sin/cos/tan identities in chapter 8 before Friday. Active timer is enabled.", date = "2026-05-26", postedBy = "Mrs. Anjali Sen"))
            noticeDao.insertNotice(Notice(title = "Annual Science Exhibition", content = "Model entries for Physics and Chemistry projects are open. Please submit your abstract to Mr. Rajesh Shah by 30th May.", date = "2026-05-25", postedBy = "Mr. Rajesh Shah"))

            // 4. Homework
            homeworkDao.insertHomework(Homework(title = "Quadratic Equations Exercise 4.2", description = "Solve questions 1 to 10 in your Maths homework register. Show full steps of factorization.", subject = "Mathematics", deadline = "2026-06-02", classLevel = "10th A", teacherName = "Mrs. Anjali Sen"))
            homeworkDao.insertHomework(Homework(title = "Ray Diagrams & Lens Formula", description = "Draw lens ray diagrams for convex and concave lenses. Label the object distance u, image distance v, and焦點 f.", subject = "Physics", deadline = "2026-05-29", classLevel = "10th A", teacherName = "Mr. Rajesh Shah"))

            // 5. Homework Submissions for Aarav (S101)
            // S101 has submitted Ray Diagrams (completed)
            homeworkSubmissionDao.insertSubmission(HomeworkSubmission(homeworkId = 2, studentId = "S101", submissionText = "Submitted high-quality scanned lens ray diagrams properly marked.", feedback = "Perfect focal distance scaling! Excellent work.", grade = "Excellent", isSubmitted = true, submittedAt = "2026-05-25"))
            
            // 6. Study Materials (Notes)
            studyMaterialDao.insertMaterial(StudyMaterial(title = "Trigonometry Quick Revision notes", subject = "Mathematics", category = "PDF Notes", chapter = "Introduction to Trigonometry", content = "Comprehensive formulas list: sin²A+cos²A=1, sec²A-tan²A=1, cosec²A-cot²A=1. Angle values: sin(30)=1/2, cos(30)=√3/2. tan(45)=1.", description = "Formulas list sheet pdf format"))
            studyMaterialDao.insertMaterial(StudyMaterial(title = "Reflection of Light Imp. Q&A", subject = "Physics", category = "Important Questions", chapter = "Light Reflection and Refraction", content = "Q1: State Laws of Reflection. Q2: Why is focal length of convex mirror positive? Q3: Derive magnification formula for mirrors.", description = "Must-do board questions"))
            studyMaterialDao.insertMaterial(StudyMaterial(title = "Trigonometry Sine Graph Explanation", subject = "Mathematics", category = "Video Lecture", chapter = "Trigonometry", content = "Video tutorial explaining trigonometry graphical curves and angles dynamically.", description = "YouTube Class Lecture Summary Link: https://youtu.be/mock_trig"))

            // 7. Events
            eventDao.insertEvent(Event(title = "Trigonometry Class Midterm Test", description = "Mandatory physical pen & paper written monthly test of mathematics.", date = "2026-05-30", type = "Exam"))
            eventDao.insertEvent(Event(title = "Guru Purnima Celebration", description = "Cultural dance progress, guru vandana, speech by students, and sweet distributions.", date = "2026-06-04", type = "Function"))
            eventDao.insertEvent(Event(title = "Inter-School Chess Tournament", description = "Hirawati senior secondary vs Modern Academy. Top 3 winners get gold medals.", date = "2026-06-15", type = "Competition"))
            eventDao.insertEvent(Event(title = "Summer Solstice Mini-Holiday", description = "School will remain closed for students on account of peak heatwaves.", date = "2026-06-01", type = "Holiday"))

            // 8. Gallery items (using beautiful Unsplash school stock images)
            galleryItemDao.insertGalleryItem(GalleryItem(albumName = "Annual Function", imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=500", title = "Lighting of the Inaugural Lamp by Principal"))
            galleryItemDao.insertGalleryItem(GalleryItem(albumName = "Annual Function", imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=500", title = "Bharatnatyam Performance by Grade 9 Students"))
            galleryItemDao.insertGalleryItem(GalleryItem(albumName = "Sports Day", imageUrl = "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=500", title = "Boys Under-17 Relay Finals"))
            galleryItemDao.insertGalleryItem(GalleryItem(albumName = "Sports Day", imageUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=500", title = "High Jump Competition Winner"))
            galleryItemDao.insertGalleryItem(GalleryItem(albumName = "Trips", imageUrl = "https://images.unsplash.com/photo-1564981797816-1043664bf78d?w=500", title = "Educational Science Museum Excursion Group Photo"))
            galleryItemDao.insertGalleryItem(GalleryItem(albumName = "Farewell", imageUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=500", title = "Class of 2026 Seniors Photo Shoot"))

            // 9. Standard MCQ Test
            val questions = listOf(
                Question("What is sin(30°)?", listOf("1", "1/2", "√3/2", "0"), 1),
                Question("What is cos(0°)?", listOf("1", "0", "1/2", "√3/2"), 0),
                Question("If sin(A) = 3/5, what is cos(A)?", listOf("4/5", "3/4", "5/4", "1/2"), 0),
                Question("What is tan(45°)?", listOf("0", "1", "√3", "1/√3"), 1),
                Question("Which identity is mathematically CORRECT?", listOf("sin²A + cos²A = 0", "sec²A - tan²A = 1", "cosec²A - sec²A = 1", "tan²A - sec²A = 1"), 1)
            )
            onlineTestDao.insertTest(OnlineTest(title = "Trigonometry Basic Formulas", subject = "Mathematics", classLevel = "10th A", durationMinutes = 5, questionsJson = questions.toJsonString()))

            // 10. Prepopulated test results to establish rankings
            testResultDao.insertResult(TestResult(testId = 1, testTitle = "Trigonometry Basic Formulas", studentId = "S102", studentName = "Ishaan Verma", score = 4, totalMarks = 5, percentage = 80.0, date = "2026-05-25"))
            testResultDao.insertResult(TestResult(testId = 1, testTitle = "Trigonometry Basic Formulas", studentId = "S103", studentName = "Priya Patel", score = 5, totalMarks = 5, percentage = 100.0, date = "2026-05-25"))
            testResultDao.insertResult(TestResult(testId = 1, testTitle = "Trigonometry Basic Formulas", studentId = "S104", studentName = "Karan Malhotra", score = 3, totalMarks = 5, percentage = 60.0, date = "2026-05-25"))
        }
    }
}
