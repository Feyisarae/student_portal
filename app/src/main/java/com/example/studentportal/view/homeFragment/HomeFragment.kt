package com.example.studentportal.view.homeFragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.studentportal.R
import com.example.studentportal.databinding.FragmentPortalStartBinding
import com.example.studentportal.roomDataBase.CardModel

class HomeFragment : Fragment(), HomeAdapter.OnCardClick {

    private lateinit var binding: FragmentPortalStartBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var application: Application

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPortalStartBinding.inflate(layoutInflater, container, false)
        initCompulsoryVariables()
        return binding.root
    }

    private fun initCompulsoryVariables() {
        application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModel.HomeViewModelFactory(application)
        ).get()
        initModuleRecycler(viewModel.getModules())

        setBinding()

        setOnBackPressed()
    }

    private fun setBinding() {
        binding.nextBtn.text
        binding.nextBtn.setOnClickListener {
            proceedToNextFragment()
        }
    }

    private fun proceedToNextFragment() {
        if (viewModel.selectedItem == null) {
            return
        }

        when (viewModel.selectedItem!!.title) {
            getString(R.string.register_student) -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRegisterNewStudent())
            }

            getString(R.string.view_student_profile) -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStudentListFragment())

            }

            getString(R.string.analytics_view) -> {
                TODO()
            }
        }
    }

    private fun initModuleRecycler(modules: ArrayList<CardModel>) {
        val adapter = HomeAdapter(
            requireContext(),
            modules,
            viewModel.selectedItem,
            this
        )
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminActivities.layoutManager = layoutManager
        binding.rvAdminActivities.itemAnimator = DefaultItemAnimator()
        binding.rvAdminActivities.adapter = adapter
        (binding.rvAdminActivities.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        adapter.notifyDataSetChanged()
        updateNextBtnState()
    }

    /**
     *
     * @return true if the "Next" button is active otherwise false
     */
    private fun updateNextBtnState(): Boolean {
        if (viewModel.selectedItem == null) {
            disableNextBtnState()
            return false
        }
        activateNextBtnState()
        return true
    }

    /**
     * set isAllFieldValid to false
     * Next button state is bound to this variable
     */
    private fun disableNextBtnState() {
        binding.isSelected = false
    }

    /**
     * set isAllFieldValid to true
     * Next button state is bound to this variable
     */
    private fun activateNextBtnState() {
        binding.isSelected = true
    }

    override fun onCardClick(selectedModule: CardModel?) {
        viewModel.selectedItem = selectedModule
        updateNextBtnState()
    }

    private fun setOnBackPressed() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {}
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}