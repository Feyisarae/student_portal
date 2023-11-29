package com.example.studentportal

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.example.studentportal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(binding.root)

        val toolbar = binding.portalToolbar.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = " "
        setUpNavGraph()

    }

    private fun setupToolBar(destination: NavDestination) {
        binding.portalToolbar.backTap.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpNavGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.portal_launch) as NavHostFragment?

        navController = navHostFragment?.navController

        // Getting the navController's navigation inflater
        val navInflater: NavInflater? = navController?.navInflater

        // Inflating navController's graph using its navigation inflater to inflate
        // the navigation resource file containing fragment navigation flow

        val graph = navInflater!!.inflate(R.navigation.student_portal_nav_graph)
        graph.startDestination = R.id.loginFragment
        navController?.setGraph(graph, null)
        // Choose the fragment the navigation will show first.

        // Use 1 or more fragment as a multiple top-level destinations.
        // This would remove the UP icon and replace with a drawer icon if the
        // destination uses a DrawerLayout.

        navController!!.addOnDestinationChangedListener(
                NavController.OnDestinationChangedListener(::onDestinationChanged)
            )
        }

    private fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?,
    ) {
        // You can compare {destination.id} to say {R.id.hr_members_create_edit_events_locations_dest},
        // to perform some operation like:
        // 1. Show or hide bottom navigation view
        //    E.g. {mBinding.hrMembersEventLocationsBottomNavView.visibility = View.GONE}
        // 2. Show or hide the action bar
        //    E.g. {actionBar?.hide()}

        val currDestination = try {
            resources.getResourceName(destination.id)
        } catch (e: Resources.NotFoundException) {
            destination.id.toString()
        }

        // hide and display custom back button
        if (destination.id == R.id.loginFragment) {
            binding.portalToolbar.backTap.visibility = View.GONE
        } else {
            binding.portalToolbar.backTap.visibility = View.VISIBLE
        }

        setupToolBar(destination)
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )

    }
}