package com.example.studentportal.roomDataBase.student

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "students")
data class Student(
    @PrimaryKey
    val studentId: String,
    val firstName: String,
    val gender: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val dateOfBirth: String,
    val faculty: String,
    val department: String,
    val stateOfOrigin: String,
    val entryYear: String,
    val address: String,
    val dateRegistered: String,
    val dateUpdated: String,
    val blacklistStatus: Int,
    val deleteStatus: Int,
    // Add other properties as needed
) : Parcelable