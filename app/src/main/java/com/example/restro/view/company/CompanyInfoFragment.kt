package com.example.restro.view.company

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.restro.R
import com.example.restro.data.model.Company
import com.example.restro.databinding.FragmentCompanyInfoBinding
import com.example.restro.utils.UiState
import com.example.restro.utils.Utilities
import com.example.restro.view.MainActivity
import com.example.restro.viewmodel.CompanyViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompanyInfoFragment : Fragment(R.layout.fragment_company_info) {
    private var _binding: FragmentCompanyInfoBinding? = null
    private val binding get() = _binding!!
    lateinit var mapLink: String

    private val viewmodel by viewModels<CompanyViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelObservers()
        viewmodel.getCompanyInfo()

        binding.tvGoogleMap.setOnClickListener {
            openMap()
        }
    }

    private fun openMap() {
        if (::mapLink.isInitialized) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
            intent.setPackage("com.google.android.apps.maps")
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                // fallback: open in browser if Google Maps not installed
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
                startActivity(browserIntent)
            }
        } else {
            Toast.makeText(requireContext(), "No map links found ", Toast.LENGTH_SHORT).show()
        }
    }

    fun viewModelObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewmodel.companyUiState.collect { state ->
                    Utilities.dismissProgressDialog()
                    when (state) {
                        is UiState.Loading -> Utilities.showProgressDialog(
                            "Loading data...!", activity as MainActivity
                        )

                        is UiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is UiState.Success -> {
                            val company: Company = state.data as Company
                            binding.company = company
                            mapLink = company.googleMap
                        }
                    }
                }
            }
        }
    }
}