package com.example.restro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.restro.viewmodel.OfflineDatabaseViewModel
import com.example.restro.viewmodel.SocketIOViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null


    // shared across multiple fragments
    private val offlineDatabaseViewModel by activityViewModels<OfflineDatabaseViewModel>()
    private val socketIOViewModel: SocketIOViewModel by activityViewModels()


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
            connectSocketIo(user._id!!)
        }

        savedInstanceState?.let {
            binding.bottomNavigation.selectedItemId =
                it.getInt("selectedTab", R.id.salesOrderFragment)
        }


        binding.imgNotificationBtn.setOnClickListener {
            startActivity(NotificationActivity.getIntent(requireContext()))
        }
    }

    private fun connectSocketIo(userId: String) {
        // Connect Socket
        socketIOViewModel.connect(userId)

        // Observe notification messages
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    socketIOViewModel.isConnected.collect { connected ->
                        if (connected) {
                            Timber.d(" Socket connected")
                        } else {
                            Timber.d(" Socket disconnected")
                        }
                    }
                }

                // observe notification changes
                launch {
                    socketIOViewModel.latestMessage.collect { message ->
                        if (message != null) {
                            Timber.d("New socket message: $message")
                            // TODO: parse message -> update salesList or notify adapter
                        }
                    }
                }

            }
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

    override fun onDestroy() {
        super.onDestroy()
        socketIOViewModel.disconnect()
    }
}