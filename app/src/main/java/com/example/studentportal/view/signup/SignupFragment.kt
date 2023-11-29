package com.example.studentportal.view.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.studentportal.R
import com.example.studentportal.databinding.FragmentSignupScreenBinding
import com.example.studentportal.roomDataBase.admin.Admin
import com.example.studentportal.roomDataBase.admin.AdminRepository
import com.example.studentportal.roomDataBase.student.StudentRepository
import com.example.studentportal.view.checks.SignupCheck
import com.example.studentportal.view.login.LoginViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SignupFragment : Fragment() {

    companion object {
        const val Is_username_unique = 1
        const val Is_password_the_same = 2
        const val Is_phone_number_valid = 3
        const val NAME = 4
        const val Required = 5
    }

    private lateinit var binding: FragmentSignupScreenBinding
    private lateinit var viewModel: LoginViewModel
    private var saveAdminDetails: Admin? = null

    private val formErrorMap: MutableMap<String, Boolean> by lazy {
        mutableMapOf(
            getString(R.string.enter_first_name) to true,
            getString(R.string.enter_last_name) to true,
            getString(R.string.enter_phone_no) to true,
            getString(R.string.create_username) to true,
            getString(R.string.create_username) to true,
            getString(R.string.confirm_password) to true,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupScreenBinding.inflate(layoutInflater, container, false)

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
            proceedToNextFragment()
        }
    }

    private suspend fun updateError(
        inputLayout: TextInputLayout,
        editText: EditText,
        editText2: EditText? = null,
        type: Int? = 0,
    ) {
        val hasError: Boolean =

            when (type) {
                Is_username_unique -> {
                    !SignupCheck.isUsernameTaken(
                        context = requireContext(),
                        inputLayout,
                        editText,
                        AdminRepository(requireContext())
                    )
                }

                Is_password_the_same -> {
                    !SignupCheck.isPasswordEqual(
                        context = requireContext(),
                        inputLayout,
                        editText,
                        editText2
                    )
                }

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
            lifecycleScope.launch {

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

                    R.id.enter_address_et -> {
                        updateError(
                            inputLayout,
                            editText,
                            type = Required
                        )
                    }

                    R.id.enter_username_et -> {
                        updateError(
                            inputLayout,
                            editText,
                            type = Is_username_unique
                        )
                    }

                    R.id.enter_password_et -> {
                        updateError(
                            inputLayout,
                            editText,
                            type = Required
                        )
                        binding.confirmPasswordEt.text?.clear()
                    }

                    R.id.confirm_password_et -> {
                        updateError(
                            inputLayout,
                            editText,
                            editText2,
                            Is_password_the_same
                        )
                    }
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

        binding.enterAddressEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterAddressLayout,
                binding.enterAddressEt
            )
        )

        binding.enterUsernameEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterUsernameLayout,
                binding.enterUsernameEt
            )
        )

        binding.enterPasswordEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.enterPasswordLayout,
                binding.enterPasswordEt
            )
        )

        binding.confirmPasswordEt.addTextChangedListener(
            ValidationTextWatcher(
                binding.confirmPasswordLayout,
                binding.confirmPasswordEt,
                binding.enterPasswordEt
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
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        lifecycleScope.launch(Dispatchers.Main) {
            val adminId = generateAdminId()
            saveAdminDetails = Admin(
                adminId = adminId,
                firstName = binding.enterFirstNameEt.text.toString(),
                lastName = binding.enterLastNameEt.text.toString(),
                phoneNumber = binding.enterPhoneNoEt.text.toString(),
                homeAddress = binding.enterAddressEt.text.toString(),
                username = binding.enterUsernameEt.text.toString().lowercase(),
                password = binding.enterPasswordEt.text.toString(),
                dateRegistered = formattedDate,
                dateUpdated = "",
                deleteStatus = 0,
                loginStatus = 0
            )
            withContext(Dispatchers.IO) {
                saveAdminDetails?.let {
                    viewModel.saveAdminDetailsToDB(it)
                }
            }
            Toast.makeText(
                requireContext(),
                "You have successfully been registered as an admin, proceed to login.",
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
        }
    }

    private fun generateAdminId(): String {
        val uniqueId = UUID.randomUUID().toString()
        return uniqueId.substring(0, 5)
    }
}