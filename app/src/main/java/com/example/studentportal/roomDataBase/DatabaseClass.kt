package com.example.studentportal.roomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studentportal.roomDataBase.admin.Admin
import com.example.studentportal.roomDataBase.admin.AdminDao
import com.example.studentportal.roomDataBase.image.Image
import com.example.studentportal.roomDataBase.image.ImageDao
import com.example.studentportal.roomDataBase.student.Student
import com.example.studentportal.roomDataBase.student.StudentDao

object DatabaseConstants {
    const val DATABASE_NAME = "student_database"
}

@Database(
    entities = [
        Student::class,
        Admin::class,
        Image::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class StudentDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun adminDao(): AdminDao
    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: StudentDatabase? = null

        fun getDatabase(context: Context): StudentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    DatabaseConstants.DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}