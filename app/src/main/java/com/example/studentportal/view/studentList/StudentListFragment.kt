package com.example.studentportal.view.studentList

import android.app.Application
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.studentportal.R
import com.example.studentportal.databinding.FragmentStudentListBinding
import com.example.studentportal.roomDataBase.student.Student
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentListFragment :
    Fragment(), StudentListAdapter.CardSelectListener {
    private lateinit var binding: FragmentStudentListBinding
    private lateinit var viewModel: StudentListViewModel
    private lateinit var application: Application

    var optionsList = ArrayList<String>()
    private var adapter: StudentListAdapter? = null
    var images: List<String>? = null

    var viewModelFilterText: String? = null
    var viewModelSelectedStudent: Student? = null
    private var searchCallback: ((String) -> Unit)? = null
    var searchButton: ImageButton? = null
    var searchText: TextInputEditText? = null
    var startedSearch: Boolean? = null
    var isAllFieldValid: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentListBinding.inflate(layoutInflater, container, false)
        initBinding()
        initCompulsoryVariables()
        searchText?.addTextChangedListener(SearchTextWatcher())
        return binding.root
    }

    private fun initCompulsoryVariables() {
        application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(
            requireActivity(),
            StudentListViewModel
                .StudentListViewModelFactory(application)
        ).get()
        viewModelFilterText = viewModel.mFilterText
        searchCallback = { it -> viewModel.search(it) }


        setBinding()
        initRecycler()
    }

    private fun initBinding() {
        searchButton = binding.imageButtonStopSearch
        searchText = binding.search
        isAllFieldValid = binding.fieldsComplete
    }

    private fun setBinding() {
        optionsList = arrayListOf(
            getString(R.string.all),
            getString(R.string.name),
            getString(R.string.student_id),
            getString(R.string.department),
            getString(R.string.year_of_entry),
        )

        setUpDropDown()

        binding.btnNext.setOnClickListener {
            setOnClickAction()
        }

        observeStartedSearch()
    }

    private fun observeStartedSearch() {
        val startedSearchLiveData = MutableLiveData<Boolean>()
        startedSearchLiveData.observe(viewLifecycleOwner) { startedSearch ->
            binding.startedSearch = startedSearch
        }
    }

    private fun setUpDropDown() {
        lifecycleScope.launch {
            val optionsArrayAdapter =
                ArrayAdapter(
                    requireContext(),
                    R.layout.selected_spinner_item,
                    optionsList
                )
            optionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

            val spinner = binding.spinnerOptions
            spinner.adapter = optionsArrayAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedOption = optionsList[position]
                    if (viewModel.selectedOption != getString(R.string.all)) {
                        binding.search.hint =
                            getString(R.string.search_by) + " " + viewModel.selectedOption
                    } else {
                        binding.search.hint =
                            getString(R.string.search_by)
                    }

                    setRecyclerDataSource()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun initRecycler() {
        lifecycleScope.launch{
            images = withContext(Dispatchers.IO) { viewModel.getAllImages() }

            adapter =
                StudentListAdapter(requireContext(),
                    viewModel.selectedStudent, images!!, this@StudentListFragment)


            setRecyclerDataSource()

            val vLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            binding.rvAdminActivities.layoutManager = vLayoutManager
            binding.rvAdminActivities.itemAnimator = DefaultItemAnimator()
            binding.rvAdminActivities.adapter = adapter
            (binding.rvAdminActivities.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
        }
    }

    private fun setRecyclerDataSource() {
        when (viewModel.selectedOption) {
            optionsList[0] -> {
                searchText?.setText("")
                viewModel.getAllStudentDetails().observe(viewLifecycleOwner) {
                    adapter?.submitData(lifecycle, it)
                }
            }

            optionsList[1] -> {
                searchText?.setText("")
                viewModel.getAllStudentDetailsByName().observe(viewLifecycleOwner) {
                    adapter?.submitData(lifecycle, it)
                }
            }

            optionsList[2] -> {
                searchText?.setText("")
                viewModel.getAllStudentDetailsById().observe(viewLifecycleOwner) {
                    adapter?.submitData(lifecycle, it)
                }
            }

            optionsList[3] -> {
                searchText?.setText("")
                viewModel.getAllStudentDetailsByDepartment().observe(viewLifecycleOwner) {
                    adapter?.submitData(lifecycle, it)
                }
            }

            optionsList[4] -> {
                searchText?.setText("")
                viewModel.getAllStudentDetailsByEntryYear().observe(viewLifecycleOwner) {
                    adapter?.submitData(lifecycle, it)
                }
            }
        }
    }

    private fun setOnClickAction() {
        if (viewModel.selectedStudent == null) {
            return
        }
        proceedToNextFragment()
    }

    private fun clearCardSelection() {
        viewModel.selectedStudent = null
        adapter?.clearSelection()
        updateNextBtnState()
    }

    private inner class SearchTextWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            try {
                val filterString = "${s.toString().lowercase()}"
                startedSearch = filterString.isNotEmpty()
                val startedSearchLiveData = MutableLiveData<Boolean>()

                if (filterString.length >= 2) {
                    startedSearchLiveData.value = filterString.isNotEmpty()
                } else {
                    startedSearchLiveData.value = false
                }

                if (viewModelFilterText != null) {
                    if (filterString != viewModelFilterText) {
                        clearCardSelection()
                    }
                } else {
                    clearCardSelection()
                }
                // TODO: Add adapter's filter
                viewModelFilterText = filterString
                searchCallback?.invoke(filterString)

                setSearchIconAndStopSearchImageButtonVisibility()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setSearchIconAndStopSearchImageButtonVisibility() {
        if (viewModelFilterText == "%%") {
            searchButton?.visibility = View.GONE
            searchText?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.search_icon
                ),
                null
            )
        } else {
            searchButton?.visibility = View.VISIBLE
            searchText?.setCompoundDrawables(
                null, null,
                null, null
            )
        }
    }

    override fun onCardSelect(selectedStudent: Student?) {
        viewModel.selectedStudent = selectedStudent
        viewModelSelectedStudent = selectedStudent
        updateNextBtnState()
        binding.fieldsComplete = isAllFieldValid
    }

    private fun updateNextBtnState(): Boolean {
        if (viewModelSelectedStudent == null) {
            disableNextBtnState()
            return false
        }
        activateNextBtnState()
        return true
    }

    private fun disableNextBtnState() {
        isAllFieldValid = false
    }

    private fun activateNextBtnState() {
        isAllFieldValid = true
    }

    fun isSearchTextChanged(): Boolean {
        val searchText = searchText?.text.toString().trim()
        return searchText.isNotEmpty()
    }

    private fun proceedToNextFragment() {
        findNavController().navigate(StudentListFragmentDirections.actionStudentListFragmentToStudentProfileFragment(viewModel.selectedStudent!!))
    }

}