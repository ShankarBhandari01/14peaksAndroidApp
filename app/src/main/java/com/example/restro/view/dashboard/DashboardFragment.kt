package com.example.restro.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.restro.R
import com.example.restro.data.model.User
import com.example.restro.databinding.DashboardFragmentBinding
import com.example.restro.databinding.DrawerHeaderBinding
import com.example.restro.utils.AuthEvent
import com.example.restro.utils.AuthEventBus
import com.example.restro.utils.Utilities.showExitConfirmationDialog
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

        headerBinding.logoutContainer.setOnClickListener {
            requireActivity().showExitConfirmationDialog(
                title = "Logout",
                message = "Are you sure you want to logout?",
                onExit = { logout() }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                AuthEventBus.events.collect { event ->
                    if (event is AuthEvent.Logout) {
                        Toast.makeText(requireActivity(), "Token Expired!!", Toast.LENGTH_LONG)
                            .show()

                        logout()

                    }
                }
            }
        }
    }


    private fun logout() {

        offlineDatabaseViewModel.setLogout(true)

        findNavController().navigate(
            R.id.loginFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, inclusive = true)
                .build()
        )
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
            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)   // Avoid re-creating same fragment
                .setRestoreState(true)      // Restore previous state
                .setPopUpTo(
                    navController.graph.startDestinationId,
                    inclusive = false,
                    saveState = true
                )
                .build()
            navController.navigate(menuItem.itemId, null, options)

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        // set menu times checked
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navigationView.setCheckedItem(destination.id)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}