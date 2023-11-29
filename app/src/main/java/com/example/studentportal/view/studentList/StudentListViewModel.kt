package com.example.studentportal.view.studentList

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.studentportal.R
import com.example.studentportal.roomDataBase.image.ImageRepository
import com.example.studentportal.roomDataBase.student.Student
import com.example.studentportal.roomDataBase.student.StudentRepository

class StudentListViewModel (private val application: Application) : ViewModel() {
    private val studentRepository = StudentRepository(application.applicationContext)
    private val imageRepository = ImageRepository(application.applicationContext)

    private var filterText: MutableLiveData<String> = MutableLiveData("%%")
    var selectedStudent: Student? = null
    var selectedOption: String = application.getString(R.string.all)

    var mFilterText: String? = "%%"

    fun blackListStudent(blackListStatus: Int, studentId: String) {
        studentRepository.blackListStudent(blackListStatus, studentId)
    }

    fun saveStudentDetailsToDB(student: Student) {
        studentRepository.insertStudent(student)
    }

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        filterText.postValue(query)
    }

    fun getAllStudentDetails(): LiveData<PagingData<Student>> {
        return Transformations.switchMap(
            filterText
        ) {
            studentRepository.getStudentDetails(
                10,
                it
            ).cachedIn(viewModelScope)
        }
    }

    fun getAllStudentDetailsById(): LiveData<PagingData<Student>> {
        return Transformations.switchMap(
            filterText
        ) {
            studentRepository.getStudentDetailsByStudentId(
                10,
                it
            ).cachedIn(viewModelScope)
        }
    }

    fun getAllStudentDetailsByName(): LiveData<PagingData<Student>> {
        return Transformations.switchMap(
            filterText
        ) {
            studentRepository.getStudentDetailsByName(
                10,
                it
            ).cachedIn(viewModelScope)
        }
    }

    fun getAllStudentDetailsByEntryYear(): LiveData<PagingData<Student>> {
        return Transformations.switchMap(
            filterText
        ) {
            studentRepository.getStudentDetailsByYearOfEntry(
                10,
                it
            ).cachedIn(viewModelScope)
        }
    }

    fun getAllStudentDetailsByDepartment(): LiveData<PagingData<Student>> {
        return Transformations.switchMap(
            filterText
        ) {
            studentRepository.getStudentDetailsByDepartment(
                10,
                it
            ).cachedIn(viewModelScope)
        }
    }

    fun getAllImages(): List<String> {
        return imageRepository.getAllImages()
    }

    class StudentListViewModelFactory constructor(
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StudentListViewModel(
                application = application
            ) as T
        }
    }
}