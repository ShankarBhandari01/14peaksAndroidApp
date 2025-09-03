package com.example.restro.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.restro.R
import com.example.restro.databinding.DashboardFragmentBinding
import com.example.restro.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("user", User::class.java)
        } else {
            arguments?.getSerializable("user") as User?
        }

    }

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


        savedInstanceState?.let {
            binding.bottomNavigation.selectedItemId =
                it.getInt("selectedTab", R.id.salesOrderFragment)
        }
    }


    private fun setBottomNavigation() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.dashboard_nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // Ensure state is restored properly
        binding.bottomNavigation.setOnItemReselectedListener {
            // Prevent reloading the same fragment when reselecting
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