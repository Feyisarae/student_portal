package com.example.studentportal.roomDataBase.admin

import android.content.Context
import com.example.studentportal.roomDataBase.StudentDatabase

class AdminRepository (context: Context) {
    private val database = StudentDatabase.getDatabase(context)
    private val adminDao = database.adminDao()

    fun insertAdmin(admin: Admin) {
        adminDao.insert(admin)
    }

    fun isUsernameTaken(username: String): Boolean {
        return adminDao.getAdminByUsername(username) != null
    }

    fun getAdminByUsername(username: String): Admin? {
        return adminDao.getAdminByUsername(username)
    }
}