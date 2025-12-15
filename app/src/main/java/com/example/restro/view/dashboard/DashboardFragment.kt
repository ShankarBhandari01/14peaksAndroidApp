package com.example.restro.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.restro.R
import com.example.restro.data.model.User
import com.example.restro.databinding.DashboardFragmentBinding
import com.example.restro.databinding.DrawerHeaderBinding
import com.example.restro.utils.AuthEvent
import com.example.restro.utils.AuthEventBus
import com.example.restro.view.notification.NotificationActivity
import com.example.restro.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null

    private lateinit var drawerToggle: ActionBarDrawerToggle

    // shared across multiple fragments
    private val offlineDatabaseViewModel by activityViewModels<UserViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSideNavigation()

        val headerView = binding.navigationView.getHeaderView(0)
        val headerBinding = DrawerHeaderBinding.bind(headerView)

        // get user from local room database
        offlineDatabaseViewModel.getUser.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            this.user = user
            // Set text
            headerBinding.tvUser.text = user.name
            headerBinding.tvEmail.text = user.email
        }

        binding.imgNotificationBtn.setOnClickListener {
            val intent = NotificationActivity.getIntent(requireContext())
            startActivity(intent)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                AuthEventBus.events.collect { event ->
                    if (event is AuthEvent.Logout) {
                        Timber.d("logout")
                    }
                }
            }
        }
    }

    private fun setSideNavigation() {
        drawerToggle = object : ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                binding.btnOpenDrawer.rotation = slideOffset * 90
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        }

        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState() // Sync initial state

        binding.btnOpenDrawer.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.dashboard_nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        // NavigationView item clicks
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.reportFragment -> navController.navigate(R.id.reportFragment)
                R.id.reservationFragment -> navController.navigate(R.id.reservationFragment)
                R.id.salesOrderFragment -> navController.navigate(R.id.salesOrderFragment)
                R.id.menuItemsFragment -> navController.navigate(R.id.menuItemsFragment)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}