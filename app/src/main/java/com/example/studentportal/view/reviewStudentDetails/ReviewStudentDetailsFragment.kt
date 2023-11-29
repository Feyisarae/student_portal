package com.example.studentportal.view.reviewStudentDetails

import android.app.Application
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

class ReviewStudentDetailsFragment : Fragment() {
    private lateinit var binding: FragmentStudentProfileBinding
    private lateinit var viewModel: StudentListViewModel
    private lateinit var application: Application
    var images: List<String>? = null

    private val navArgs by navArgs<ReviewStudentDetailsFragmentArgs>()

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
        binding.tvName.text = "${navArgs.studentDetails?.firstName} ${navArgs.studentDetails?.lastName}"
        binding.tvStudentId.text = navArgs.studentDetails?.studentId ?: "-"
        binding.tvDepartment.text = navArgs.studentDetails?.department ?: "-"
        binding.tvFaculty.text = navArgs.studentDetails?.faculty ?: "-"
        binding.tvEmail.text = navArgs.studentDetails?.email ?: "-"
        binding.address.text = navArgs.studentDetails?.address ?: "-"
        binding.gender.text = navArgs.studentDetails?.gender ?: "-"
        binding.tvEntryYear.text = navArgs.studentDetails?.entryYear ?: "-"
        binding.tvPhoneNumber.text = navArgs.studentDetails?.phoneNumber ?: "-"
        binding.tvState.text = navArgs.studentDetails?.stateOfOrigin ?: "-"
        loadCapturedImage()

        binding.btnEdit.setOnClickListener {
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
            "${navArgs.studentDetails?.studentId}_$formattedDate.jpg"

        )
        if (studentImage.exists()) {
            Picasso.get().load(studentImage)
                .into(binding.studentImageview)
        }
    }

    private fun proceedToNextFragment() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                viewModel.saveStudentDetailsToDB(navArgs.studentDetails!!)
            }
            Toast.makeText(
                requireContext(),
                "Student Registered Successfully",
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigate(ReviewStudentDetailsFragmentDirections.actionReviewStudentDetailsFragmentToHomeFragment())
        }
    }
}