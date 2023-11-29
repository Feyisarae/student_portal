package com.example.studentportal.roomDataBase.image

import android.content.Context
import com.example.studentportal.roomDataBase.StudentDatabase

class ImageRepository (context: Context) {
    private val database = StudentDatabase.getDatabase(context)
    private val imageDao = database.imageDao()

    fun insertImage(image: Image) {
        imageDao.insert(image)
    }

    fun getAllImages(): List<String> {
        return imageDao.getAllImages()
    }
}