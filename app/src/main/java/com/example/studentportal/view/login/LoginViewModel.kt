package com.example.studentportal.view.login

import android.app.Application
import androidx.lifecycle.*
import com.example.studentportal.roomDataBase.admin.Admin
import com.example.studentportal.roomDataBase.admin.AdminRepository
import com.example.studentportal.roomDataBase.image.Image
import com.example.studentportal.roomDataBase.image.ImageRepository
import com.example.studentportal.roomDataBase.student.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LoginViewModel (
    private val application: Application,
    private val adminRepository: AdminRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {
    private val imageRepository = ImageRepository(application.applicationContext)
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    // Function to check login credentials
    fun checkLoginCredentials(username: String, password: String) {
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) { adminRepository.getAdminByUsername(username.lowercase()) }
            _loginResult.value = user != null && user.password == password
        }
    }

    fun insertFileSyncImage(file: File) {
        val image = Image(
            studentId = file.name, imageFileName = file.name,
        )
        imageRepository.insertImage(image)
    }

    fun saveAdminDetailsToDB(admin: Admin) {
        adminRepository.insertAdmin(admin)
    }

    var usernameLive = MutableLiveData("")
    var passwordLive = MutableLiveData("")

    val isUsernameUnique = MutableLiveData<Boolean>()

    // Other LiveData for your form fields

    fun isUsernameTaken(username: String): Boolean {
        return true
    }

    fun updatePassword(password: String) {
        passwordLive.value = password
    }

    fun updateusername(username: String) {
        usernameLive.value = username
    }

    class LoginViewModelFactory constructor(
        private val application: Application,
        private val adminRepository: AdminRepository,
        private val studentRepository: StudentRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(
                application = application,
                adminRepository = adminRepository,
                studentRepository= studentRepository
            ) as T
        }
    }
}