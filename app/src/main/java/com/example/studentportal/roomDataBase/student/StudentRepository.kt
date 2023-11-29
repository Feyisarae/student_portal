package com.example.studentportal.roomDataBase.student

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.studentportal.roomDataBase.StudentDatabase

class StudentRepository (context: Context) {
    private val database = StudentDatabase.getDatabase(context)
    private val studentDao = database.studentDao()

    fun insertStudent(student: Student) {
        studentDao.insert(student)
    }

    fun getStudentDetails(
        pageSize: Int,
        filterText: String
    ): LiveData<PagingData<Student>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            studentDao.getStudentDetails(filterText)
        }
    ).liveData

    fun getStudentDetailsByDepartment(
        pageSize: Int,
        filterText: String
    ): LiveData<PagingData<Student>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            studentDao.getStudentDetailsByDepartment(filterText)
        }
    ).liveData

    fun getStudentDetailsByStudentId(
        pageSize: Int,
        filterText: String
    ): LiveData<PagingData<Student>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            studentDao.getStudentDetailsByStudentId(filterText)
        }
    ).liveData

    fun getStudentDetailsByYearOfEntry(
        pageSize: Int,
        filterText: String
    ): LiveData<PagingData<Student>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            studentDao.getStudentDetailsByYearOfEntry(filterText)
        }
    ).liveData

    fun getStudentDetailsByName(
        pageSize: Int,
        filterText: String
    ): LiveData<PagingData<Student>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            studentDao.getStudentDetailsByName(filterText)
        }
    ).liveData

    fun blackListStudent(
        blacklistStatus: Int,
        studentId: String
    ) {
        studentDao.blackListStudent(
            blacklistStatus,
            studentId
        )
    }
}