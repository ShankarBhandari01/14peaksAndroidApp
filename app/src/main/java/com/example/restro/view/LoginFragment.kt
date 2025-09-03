package com.example.restro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.restro.R
import com.example.restro.databinding.LoginFragmentBinding
import com.example.restro.utils.UiEvent
import com.example.restro.utils.Utils
import com.example.restro.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        disableLoginButton()
        observeViewModel()
    }

    private fun disableLoginButton() {
        binding.loginButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.gray)
        binding.loginButton.isEnabled = false
    }

    private fun observeViewModel() {

        viewModel.isLoginEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.loginButton.isEnabled = enabled
            binding.loginButton.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireContext(),
                    if (enabled) R.color.black else R.color.gray
                )
        }

        viewModel.emailError.observe(viewLifecycleOwner) {
            binding.emailInputLayout.error = it
        }
        viewModel.passwordError.observe(viewLifecycleOwner) {
            binding.passwordInputLayout.error = it
        }

        viewModel.uiEvents.observe(viewLifecycleOwner) { event ->
            when (event) {
                is UiEvent.ShowLoading -> Utils.showProgressDialog(
                    "Logging in…",
                    activity as MainActivity
                )

                is UiEvent.HideLoading -> Utils.dismissProgressDialog()
                is UiEvent.ShowMessage -> Toast.makeText(
                    requireContext(),
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()

                is UiEvent.Navigate -> {

                    val bundle = Bundle()
                    bundle.putSerializable("user", event.data)

                    event.destinationId?.let {
                        findNavController().navigate(
                            it,
                            bundle,
                            event.popUpToId?.let { popId ->
                                NavOptions.Builder().setPopUpTo(popId, true).build()
                            }
                        )
                    }
                }

                is UiEvent.NavigateToActivity -> {}
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}