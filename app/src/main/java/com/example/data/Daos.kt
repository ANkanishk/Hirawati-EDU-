package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE role = 'STUDENT'")
    fun getAllStudentsFlow(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = 'STUDENT'")
    suspend fun getAllStudents(): List<User>

    @Query("SELECT * FROM users WHERE role = 'TEACHER'")
    fun getAllTeachersFlow(): Flow<List<User>>

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attendanceList: List<Attendance>)

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceForStudent(studentId: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND date = :date LIMIT 1")
    suspend fun getAttendanceForStudentAndDate(studentId: String, date: String): Attendance?

    @Query("SELECT * FROM attendance")
    fun getAllAttendanceFlow(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<Attendance>>
}

@Dao
interface HomeworkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: Homework)

    @Query("SELECT * FROM homework WHERE classLevel = :classLevel ORDER BY id DESC")
    fun getHomeworkForClass(classLevel: String): Flow<List<Homework>>

    @Query("SELECT * FROM homework ORDER BY id DESC")
    fun getAllHomeworkFlow(): Flow<List<Homework>>

    @Delete
    suspend fun deleteHomework(homework: Homework)
}

@Dao
interface HomeworkSubmissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: HomeworkSubmission)

    @Query("SELECT * FROM homework_submissions WHERE studentId = :studentId")
    fun getSubmissionsForStudent(studentId: String): Flow<List<HomeworkSubmission>>

    @Query("SELECT * FROM homework_submissions WHERE homeworkId = :homeworkId")
    fun getSubmissionsForHomework(homeworkId: Int): Flow<List<HomeworkSubmission>>

    @Query("SELECT * FROM homework_submissions WHERE homeworkId = :homeworkId AND studentId = :studentId LIMIT 1")
    suspend fun getSubmission(homeworkId: Int, studentId: String): HomeworkSubmission?

    @Query("SELECT * FROM homework_submissions WHERE homeworkId = :homeworkId AND studentId = :studentId LIMIT 1")
    fun getSubmissionFlow(homeworkId: Int, studentId: String): Flow<HomeworkSubmission?>
}

@Dao
interface StudyMaterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: StudyMaterial)

    @Query("SELECT * FROM study_material ORDER BY id DESC")
    fun getAllMaterials(): Flow<List<StudyMaterial>>

    @Delete
    suspend fun deleteMaterial(material: StudyMaterial)
}

@Dao
interface OnlineTestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: OnlineTest)

    @Query("SELECT * FROM tests WHERE classLevel = :classLevel ORDER BY id DESC")
    fun getTestsByClass(classLevel: String): Flow<List<OnlineTest>>

    @Query("SELECT * FROM tests ORDER BY id DESC")
    fun getAllTests(): Flow<List<OnlineTest>>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    suspend fun getTestById(id: Int): OnlineTest?

    @Delete
    suspend fun deleteTest(test: OnlineTest)
}

@Dao
interface TestResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TestResult)

    @Query("SELECT * FROM test_results WHERE studentId = :studentId ORDER BY id DESC")
    fun getResultsForStudent(studentId: String): Flow<List<TestResult>>

    @Query("SELECT * FROM test_results ORDER BY percentage DESC")
    fun getAllResultsOrdered(): Flow<List<TestResult>>

    @Query("SELECT * FROM test_results ORDER BY id DESC")
    fun getAllResults(): Flow<List<TestResult>>
}

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Delete
    suspend fun deleteEvent(event: Event)
}

@Dao
interface GalleryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItem(item: GalleryItem)

    @Query("SELECT * FROM gallery ORDER BY id DESC")
    fun getAllGalleryItems(): Flow<List<GalleryItem>>

    @Delete
    suspend fun deleteGalleryItem(item: GalleryItem)
}

@Dao
interface NoticeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice)

    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Delete
    suspend fun deleteNotice(notice: Notice)
}
