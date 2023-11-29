package com.example.studentportal.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.studentportal.R
import com.example.studentportal.databinding.FragmentLoginScreenBinding
import com.example.studentportal.roomDataBase.admin.AdminRepository
import com.example.studentportal.roomDataBase.student.StudentRepository

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginScreenBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModel
                .LoginViewModelFactory(requireNotNull(this.activity).application, adminRepository = AdminRepository(requireContext()), studentRepository = StudentRepository(requireContext()))
        ).get()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        binding.tvSignup.setOnClickListener{
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
        }
        binding.btnLogin.setOnClickListener {
            validateLogin()
        }
    }

    private fun validateLogin() {
        // Clear previous errors
        binding.usernameLayout.error = null
        binding.passwordLayout.error = null

        // Validate username
        val username = binding.usernameEditText.text.toString().lowercase().trim()
        if (username.isEmpty()) {
            binding.usernameLayout.error = getString(R.string.enter_username)
            return
        }

        // Validate password
        val password = binding.passwordEditText.text.toString().trim()
        if (password.isEmpty()) {
            binding.passwordLayout.error = getString(R.string.enter_password)
            return
        }

        // All fields are valid, proceed with login logic
        viewModel.checkLoginCredentials(username, password)

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                Toast.makeText(
                    requireContext(),
                    "Login successful",
                    Toast.LENGTH_SHORT
                ).show()
                // Login successful, navigate to the next screen
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
            } else {
                // Login failed, show a toast or handle the failure accordingly
                Toast.makeText(
                    requireContext(),
                    "Login failed. Invalid Username or Password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}