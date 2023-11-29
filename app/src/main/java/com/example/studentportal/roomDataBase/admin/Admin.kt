package com.example.studentportal.roomDataBase.admin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "admin")
data class Admin(
    @PrimaryKey
    val adminId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val homeAddress: String,
    val username: String,
    val password: String,
    val dateRegistered: String,
    val dateUpdated: String,
    val deleteStatus: Int,
    val loginStatus: Int,
    // Add other properties as needed
) : Parcelable