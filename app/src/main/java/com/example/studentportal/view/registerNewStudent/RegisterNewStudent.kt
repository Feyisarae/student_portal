package com.example.studentportal.view.registerNewStudent

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.studentportal.R
import com.example.studentportal.databinding.FragmentStudentRegistrationFormBinding
import com.example.studentportal.roomDataBase.Constant
import com.example.studentportal.roomDataBase.admin.AdminRepository
import com.example.studentportal.roomDataBase.student.Student
import com.example.studentportal.roomDataBase.student.StudentRepository
import com.example.studentportal.view.checks.SignupCheck
import com.example.studentportal.view.login.LoginViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegisterNewStudent : Fragment() {

    companion object {
        const val TAKE_PHOTO = 2
        const val CAMERA_REQUEST_RESULT = 1
        const val Is_phone_number_valid = 3
        const val NAME = 4
        const val Required = 5
    }

    private lateinit var binding: FragmentStudentRegistrationFormBinding
    private lateinit var viewModel: LoginViewModel
    private var saveStudentDetails: Student? = null
    private var currentPhotoPath: String? = null

    private val formErrorMap: MutableMap<String, Boolean> by lazy {
        mutableMapOf(
            getString(R.string.enter_student_first_name) to true,
            getString(R.string.enter_student_last_name) to true,
            getString(R.string.enter_student_phone_no) to true,
            getString(R.string.enter_student_dob) to true,
            getString(R.string.gender) to true,
            getString(R.string.enter_student_state) to true,
            getString(R.string.enter_faculty) to true,
            getString(R.string.enter_department) to true,
            getString(R.string.entry_year) to true,
            getString(R.string.enter_student_address) to true,

            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentRegistrationFormBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModel
                .LoginViewModelFactory(requireNotNull(this.activity).application, adminRepository = AdminRepository(requireContext()), studentRepository = StudentRepository(requireContext()))
        ).get()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        addValidationTextWatchers()
    }

    private fun setBinding() {
        binding.btnNext.setOnClickListener {
            if (!wasCameraPermissionWasGiven()) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_RESULT
                )
            } else {
                takePhoto(TAKE_PHOTO, requireContext())
            }
        }

        val states = resources.getStringArray(R.array.nigeria_states)
        val stateArrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            states
        )
        binding.state.apply {
            setAdapter(stateArrayAdapter)
        }

        val faculty = resources.getStringArray(R.array.faculties)
        val facultyArrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            faculty
        )
        binding.faculty.apply {
            setAdapter(facultyArrayAdapter)
        }

        val startYear = 2015
        val endYear = 2050

        val yearsArray = mutableListOf<String>()

        for (year in startYear..endYear) {
            yearsArray.add(year.toString())
        }

        val yearsTypedArray = yearsArray.toTypedArray()
        val yearArrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            yearsTypedArray
        )
        binding.entryYear.apply {
            setAdapter(yearArrayAdapter)
        }

        val gender = resources.getStringArray(R.array.gender)
        val genderArrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            gender
        )
        binding.gender.apply {
            setAdapter(genderArrayAdapter)
        }

        val departmentAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )
        binding.department.apply {
            setAdapter(departmentAdapter)
        }

        binding.faculty.setOnItemClickListener { _, _, position, _ ->
            val selectedDepartments = when (faculty[position]) {
                "Engineering" -> arrayOf("Electrical Engineering", "Mechanical Engineering", "Civil Engineering")
                "Science" -> arrayOf("Physics", "Chemistry", "Biology", "Mathematics")
                "Arts" -> arrayOf("English", "History", "Philosophy")
                "Business Administration" -> arrayOf("Marketing", "Finance", "Management")
                "Medicine" -> arrayOf("Internal Medicine", "Surgery", "Pediatrics")
                "Social Sciences" -> arrayOf("Psychology", "Sociology", "Political Science")
                else -> emptyArray()
            }

            departmentAdapter.clear()
            departmentAdapter.addAll(selectedDepartments.toList())
        }
    }

    private fun updateError(
        inputLayout: TextInputLayout,
        editText: EditText,
        editText2: EditText? = null,
        type: Int? = 0,
    ) {
        val hasError: Boolean =

            when (type) {
                NAME -> {
                    !SignupCheck.isName(
                        context = requireContext(),
                        inputLayout,
                        editText,
                        requireContext().getString(R.string.name)
                    )
                }

                Is_phone_number_valid -> {
                    !SignupCheck.isPhoneNumberValid(
                        context = requireContext(),
                        inputLayout,
                        editText,
                    )
                }

                Required -> {
                    !SignupCheck.isRequired(
                        context = requireContext(),
                        inputLayout,
                        editText,
                    )
                }

                else -> {
                    !SignupCheck.isRequired(
                        context = requireContext(),
                        inputLayout,
                        editText,
                    )
                }
            }
        formErrorMap[editText.hint.toString()] = hasError
    }

    private inner class ValidationTextWatcher(
        val inputLayout: TextInputLayout,
        val editText: EditText,
        val editText2: EditText? = null,
    ) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {
            when (editText.id) {
                R.id.enter_first_name_et -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = NAME
                    )
                }

                R.id.enter_last_name_et -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = NAME
                    )
                }

                R.id.enter_phone_no_et -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Is_phone_number_valid
                    )
                }

                R.id.enter_email_et -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Required
                    )
                }

                R.id.enter_dob_et -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Required
                    )
                }

                R.id.gender -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Required
                    )
                }

                R.id.state -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Required
                    )
                }

                R.id.faculty -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Required
                    )
                }

                R.id.department -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Required
                    )
                }

                R.id.entry_year -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Required
                    )
                }

                R.id.enter_address_et -> {
                    updateError(
                        inputLayout,
                        editText,
                        type = Required
                    )
                }
            }

            updateNextBtnState()
        }
    }

    private fun addValidationTextWatchers() {
        binding.enterFirstNameEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterFirstNameLayout,
                binding.enterFirstNameEt
            )
        )

        binding.enterLastNameEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterLastNameLayout,
                binding.enterLastNameEt
            )
        )
        binding.enterPhoneNoEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterPhoneNoLayout,
                binding.enterPhoneNoEt
            )
        )

        binding.enterEmailEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterEmailLayout,
                binding.enterEmailEt
            )
        )

        binding.enterDobEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterDobLayout,
                binding.enterDobEt
            )
        )

        binding.gender.addTextChangedListener(
            ValidationTextWatcher(
                binding.genderLayout,
                binding.gender
            )
        )

        binding.state.addTextChangedListener(
            ValidationTextWatcher(
                binding.stateLayout,
                binding.state
            )
        )

        binding.faculty.addTextChangedListener(
            ValidationTextWatcher(
                binding.facultyLayout,
                binding.faculty
            )
        )

        binding.department.addTextChangedListener(
            ValidationTextWatcher(
                binding.departmentLayout,
                binding.department
            )
        )

        binding.entryYear.addTextChangedListener(
            ValidationTextWatcher(
                binding.entryYearLayout,
                binding.entryYear
            )
        )

        binding.enterAddressEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterAddressLayout,
                binding.enterAddressEt
            )
        )
    }

    private fun updateNextBtnState(): Boolean {
        if (SignupCheck.isErrorInFields(formErrorMap)) {
            disableNextBtnState()
            return false
        }
        activateNextBtnState()
        return true
    }

    private fun disableNextBtnState() {
        binding.fieldsComplete = false
    }

    private fun activateNextBtnState() {
        binding.fieldsComplete = true
    }

    private fun proceedToNextFragment() {
        val department = binding.department.text
        val words = department.split("").take(2)
        val departmentCode = words.joinToString("") {it.uppercase()}
        val yearOfEntry = binding.entryYear.text
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        lifecycleScope.launch(Dispatchers.Main) {
            saveStudentDetails = Student(
                studentId = "$departmentCode/$yearOfEntry/${generateUniqueId()}",
                firstName = binding.enterFirstNameEt.text.toString(),
                lastName = binding.enterLastNameEt.text.toString(),
                dateOfBirth = binding.enterDobEt.text.toString(),
                gender = binding.gender.text.toString(),
                faculty = binding.faculty.text.toString(),
                department = binding.department.text.toString(),
                stateOfOrigin = binding.state.text.toString(),
                entryYear = binding.entryYear.text.toString(),
                dateRegistered = formattedDate,
                address = binding.enterAddressEt.text.toString(),
                phoneNumber = binding.enterPhoneNoEt.text.toString(),
                email = binding.enterEmailEt.text.toString(),
                dateUpdated = "",
                blacklistStatus = 0,
                deleteStatus = 1
            )
            findNavController().navigate(RegisterNewStudentDirections.actionRegisterNewStudentToReviewStudentDetailsFragment(saveStudentDetails!!))
        }
    }

    private fun generateUniqueId(): String {
        val uniqueId = UUID.randomUUID().toString()
        return uniqueId.substring(0, 4)
    }

    private fun wasCameraPermissionWasGiven(): Boolean {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun takePhoto(code: Int, context: Context) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile(requireContext())
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.example.studentportal.fileProvider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, code)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            if (currentPhotoPath != null) {
                val myFile = File(currentPhotoPath as String)
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) { viewModel.insertFileSyncImage(file = myFile) }
                }
            }
        }
        proceedToNextFragment()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createImageFile(context: Context): File {
        val storageDir: File? =
            context.getExternalFilesDir("${Environment.DIRECTORY_PICTURES}/${Constant.Student_Directory}")
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val studentId = saveStudentDetails?.studentId
        return File(
            storageDir,
            "$studentId _$formattedDate.jpg",
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
}