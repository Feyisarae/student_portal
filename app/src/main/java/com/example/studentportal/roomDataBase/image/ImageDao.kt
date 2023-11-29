package com.example.studentportal.roomDataBase.image

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert
    fun insert(image: Image)

    @Query("SELECT * FROM IMAGE WHERE studentId = :studentId")
    fun getImageById(studentId: String): Image?

    @Query(
        "SELECT imageFileName FROM image"
    )
    fun getAllImages(): List<String>

    // Add other operations as needed
}