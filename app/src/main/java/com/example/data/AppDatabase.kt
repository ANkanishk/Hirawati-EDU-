package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Attendance::class,
        Homework::class,
        HomeworkSubmission::class,
        StudyMaterial::class,
        OnlineTest::class,
        TestResult::class,
        Event::class,
        GalleryItem::class,
        Notice::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun homeworkDao(): HomeworkDao
    abstract fun homeworkSubmissionDao(): HomeworkSubmissionDao
    abstract fun studyMaterialDao(): StudyMaterialDao
    abstract fun onlineTestDao(): OnlineTestDao
    abstract fun testResultDao(): TestResultDao
    abstract fun eventDao(): EventDao
    abstract fun galleryItemDao(): GalleryItemDao
    abstract fun noticeDao(): NoticeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hirawati_school_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
