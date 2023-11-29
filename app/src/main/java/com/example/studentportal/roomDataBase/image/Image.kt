package com.example.studentportal.roomDataBase.image

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "image")
data class Image(
    @PrimaryKey
    val studentId: String,
    val imageFileName: String,
    // Add other properties as needed
) : Parcelable