package com.example.restro.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.restro.R
import com.example.restro.data.model.User
import com.example.restro.databinding.DashboardFragmentBinding
import com.example.restro.view.notification.NotificationActivity
import com.example.restro.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null


    // shared across multiple fragments
    private val offlineDatabaseViewModel by activityViewModels<UserViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomNavigation()
        // get user from local room database
        offlineDatabaseViewModel.getUser.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            this.user = user
        }

        savedInstanceState?.let {
            binding.bottomNavigation.selectedItemId =
                it.getInt("selectedTab", R.id.salesOrderFragment)
        }


        binding.imgNotificationBtn.setOnClickListener {
            val intent = NotificationActivity.Companion.getIntent(requireContext())
            startActivity(intent)
        }
    }


    private fun setBottomNavigation() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.dashboard_nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // Ensure state is restored properly
        binding.bottomNavigation.setOnItemReselectedListener {
            // Prevent reloading the same fragment when reSelecting
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedTab", binding.bottomNavigation.selectedItemId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}