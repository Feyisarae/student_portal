package com.example.studentportal.roomDataBase.student

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudentDao {
    @Insert
    abstract fun insert(student: Student)

    @Query("SELECT * FROM students")
    abstract fun getAllStudents(): List<Student>

    @Query(
        "SELECT * FROM students " +
                "WHERE (firstName LIKE :searchText " +
                "OR lastName LIKE :searchText " +
                "OR department LIKE :searchText " +
                "OR faculty LIKE :searchText " +
                "OR studentId LIKE :searchText " +
                "OR entryYear LIKE :searchText " +
                "OR phoneNumber LIKE :searchText " +
                "OR stateOfOrigin LIKE :searchText)"
    )
    abstract fun getStudentDetails(
        searchText: String,
    ): PagingSource<Int, Student>

    @Query(
        "SELECT * FROM students WHERE studentId LIKE :searchText"
    )
    abstract fun getStudentDetailsByStudentId(
        searchText: String,
    ): PagingSource<Int, Student>

    @Query(
        "SELECT * FROM students WHERE (firstName LIKE :searchText OR lastName LIKE :searchText)"
    )
    abstract fun getStudentDetailsByName(
        searchText: String,
    ): PagingSource<Int, Student>

    @Query(
        "SELECT * FROM students WHERE entryYear LIKE :searchText"
    )
    abstract fun getStudentDetailsByYearOfEntry(
        searchText: String,
    ): PagingSource<Int, Student>

    @Query(
        "SELECT * FROM students WHERE department LIKE :searchText"
    )
    abstract fun getStudentDetailsByDepartment(
        searchText: String,
    ): PagingSource<Int, Student>

    @Query("UPDATE STUDENTS SET blacklistStatus = :blacklistStatus WHERE studentId = :studentId")
    abstract fun blackListStudent(
        blacklistStatus: Int,
        studentId: String
    )

    // Add other operations as needed
}