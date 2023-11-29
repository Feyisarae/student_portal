package com.example.studentportal.view.studentProfile

import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentportal.R
import com.example.studentportal.databinding.FragmentStudentProfileBinding
import com.example.studentportal.roomDataBase.Constant
import com.example.studentportal.view.studentList.StudentListViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class StudentProfileFragment : Fragment() {
    private lateinit var binding: FragmentStudentProfileBinding
    private lateinit var viewModel: StudentListViewModel
    private lateinit var application: Application
    var images: List<String>? = null

    private val navArgs by navArgs<StudentProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentProfileBinding.inflate(layoutInflater, container, false)

        initCompulsoryVariables()
        return binding.root
    }

    private fun initCompulsoryVariables() {
        application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(
            requireActivity(),
            StudentListViewModel
                .StudentListViewModelFactory(application)
        ).get()

        setBinding()
    }

    private fun setBinding() {
        binding.button.setOnClickListener {
            showBlacklistConfirmationDialogue()
        }
        binding.tvName.text = "${navArgs.selectedStudent?.firstName} ${navArgs.selectedStudent?.lastName}"
        binding.tvStudentId.text = navArgs.selectedStudent?.studentId ?: "-"
        binding.tvDepartment.text = navArgs.selectedStudent?.department ?: "-"
        binding.tvFaculty.text = navArgs.selectedStudent?.faculty ?: "-"
        binding.tvEmail.text = navArgs.selectedStudent?.email ?: "-"
        binding.address.text = navArgs.selectedStudent?.address ?: "-"
        binding.gender.text = navArgs.selectedStudent?.gender ?: "-"
        binding.tvEntryYear.text = navArgs.selectedStudent?.entryYear ?: "-"
        binding.tvPhoneNumber.text = navArgs.selectedStudent?.phoneNumber ?: "-"
        binding.tvState.text = navArgs.selectedStudent?.stateOfOrigin ?: "-"

        loadCapturedImage()

        binding.btnEdit.setOnClickListener {
            findNavController().popBackStack()
            findNavController().popBackStack()
        }
        binding.btnNext.setOnClickListener {
            proceedToNextFragment()
        }

    }

    private fun loadCapturedImage() {
        val studentDirectory =
            File(
                Objects.requireNonNull<File>(
                    requireContext().applicationContext.getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                    )
                ).absoluteFile,
                Constant.Student_Directory
            )
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val studentImage = File(
            studentDirectory.absoluteFile,
            "${navArgs.selectedStudent?.studentId}_$formattedDate.jpg"

        )
        if (studentImage.exists()) {
            Picasso.get().load(studentImage)
                .into(binding.studentImageview)
        }
    }

    private fun showBlacklistConfirmationDialogue() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Blacklist Student")
            .setMessage("Are you sure you want to blacklist this student?")
            .setPositiveButton("Yes") { dialog, which ->
                // Blacklist the student (set blacklistStatus to 1)
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        viewModel.blackListStudent(blackListStatus = 1, studentId = navArgs.selectedStudent?.studentId!!)
                    }
                    binding.button.text = getString(R.string.studentBlacklisted)
                }
            }
            .setNegativeButton("No") { dialog, which ->
                // Do nothing or handle the cancellation
            }

        builder.create().show()
    }

    private fun proceedToNextFragment() {
        findNavController().navigate(StudentProfileFragmentDirections.actionStudentProfileFragmentToHomeFragment())
    }
}