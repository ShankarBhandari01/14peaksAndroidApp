package com.example.restro.view.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.restro.R
import com.example.restro.data.model.UserResponse
import com.example.restro.databinding.LoginFragmentBinding
import com.example.restro.service.SocketForegroundService
import com.example.restro.utils.UiState
import com.example.restro.utils.Utilities
import com.example.restro.view.MainActivity
import com.example.restro.viewmodel.LoginViewModel
import com.example.restro.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel>()

    private val offlineViewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@LoginFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        binding.loginButton.setOnClickListener {
            if (viewModel.validateData()) {
                viewModel.login()
            }
        }

        // welcome message
        binding.welcomeText.text = Utilities.getGreetingMessage()

        disableLoginButton()
        observeViewModel()

        automaticLogin()
    }

    private fun automaticLogin() {
        lifecycleScope.launch {
            if (!offlineViewModel.isFirstLaunch.first()) {
                binding.loginButton.performClick()
            }
        }
    }

    private fun disableLoginButton() {
        binding.loginButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.md_theme_outline)
        binding.loginButton.isEnabled = false
    }

    private fun navigateToDashboard() {
        // navigate to dashboard
        findNavController().navigate(
            R.id.dashboardFragment, null, R.id.loginFragment.let { popId ->
                NavOptions.Builder().setPopUpTo(popId, true).build()
            })
    }

    fun startService(userId: String) {
        // start background services
        val intent = Intent(
            activity as MainActivity, SocketForegroundService::class.java
        ).apply {
            putExtra("USER_ID", userId)
        }
        ContextCompat.startForegroundService(activity as MainActivity, intent)
    }

    private fun observeViewModel() {

        viewModel.isLoginEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.loginButton.isEnabled = enabled
            binding.loginButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(), if (enabled) R.color.md_theme_scrim else R.color.md_theme_outline
            )
        }

        viewModel.emailError.observe(viewLifecycleOwner) {
            binding.emailInputLayout.error = it
        }
        viewModel.passwordError.observe(viewLifecycleOwner) {
            binding.passwordInputLayout.error = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.LoginuiState.collect { state ->
                    Utilities.dismissProgressDialog()
                    when (state) {
                        is UiState.Loading -> Utilities.showProgressDialog(
                            "Logging in…", activity as MainActivity
                        )

                        is UiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is UiState.Success -> {
                            // save user id to local storage
                            val userResponse = state.data as UserResponse
                            offlineViewModel.saveUserId(userResponse.user._id)
                            offlineViewModel.saveUser(userResponse.user)
                            // set first launch false
                            offlineViewModel.setFirstLaunch(false)
                            // set user session
                            offlineViewModel.saveSession(userResponse.session)
                            // start service
                            startService(userResponse.user._id)
                            // navigate to dashboard
                            navigateToDashboard()
                        }
                    }
                }
            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}