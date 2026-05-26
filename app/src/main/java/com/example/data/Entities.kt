package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String, // S101, T201, admin
    val password: String,
    val role: String, // STUDENT, TEACHER, ADMIN
    val name: String,
    val classLevel: String = "", // Student only (e.g., "10th A")
    val rollNo: Int = 0,          // Student only
    val subject: String = "",      // Teacher only (e.g., "Mathematics")
    val profilePic: String = ""
)

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: String,
    val date: String, // YYYY-MM-DD
    val status: String // "Present", "Absent"
)

@Entity(tableName = "homework")
data class Homework(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val subject: String,
    val deadline: String, // YYYY-MM-DD
    val classLevel: String, // e.g., "10th A"
    val teacherName: String
)

@Entity(tableName = "homework_submissions")
data class HomeworkSubmission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val homeworkId: Int,
    val studentId: String,
    val submissionText: String = "",
    val feedback: String = "",
    val grade: String = "", // e.g. "A+", "B", "Excellent"
    val isSubmitted: Boolean = true,
    val submittedAt: String = ""
)

@Entity(tableName = "study_material")
data class StudyMaterial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val category: String, // "PDF Notes", "Important Questions", "Video Lecture"
    val chapter: String,
    val content: String, // Can be detail notes or a mock link
    val description: String = ""
)

@Entity(tableName = "tests")
data class OnlineTest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val classLevel: String = "10th A",
    val durationMinutes: Int = 10,
    val questionsJson: String // Native JSON array serialization of questions
)

@Entity(tableName = "test_results")
data class TestResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val testId: Int,
    val testTitle: String,
    val studentId: String,
    val studentName: String,
    val score: Int,
    val totalMarks: Int,
    val percentage: Double,
    val date: String
)

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: String, // YYYY-MM-DD
    val type: String // "Exam", "Holiday", "Function", "Competition"
)

@Entity(tableName = "gallery")
data class GalleryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val albumName: String, // "Annual Function", "Sports Day", "Farewell", "Trips", "Competitions"
    val imageUrl: String,  // stock photo URLs or tags
    val title: String,
    val date: String = ""
)

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: String, // YYYY-MM-DD
    val postedBy: String // Admin or Mrs. Anjali Sen
)
