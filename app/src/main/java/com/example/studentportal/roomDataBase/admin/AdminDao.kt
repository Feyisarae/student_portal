package com.example.studentportal.roomDataBase.admin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AdminDao {
    @Insert
    abstract fun insert(admin: Admin)

    @Query("SELECT * FROM admin")
    suspend fun getAllAdmins(): List<Admin>

    @Query("SELECT * FROM admin WHERE username = :username LIMIT 1")
    fun getAdminByUsername(username: String): Admin?

    // Add other operations as needed
}