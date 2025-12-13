package com.example.restro.view.reservations

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.restro.R
import com.example.restro.databinding.FragmentReservationBinding
import com.example.restro.view.adapters.ReservationTabAdapter
import com.example.restro.view.bottom_sheet_dialog.FilterBottomSheet
import com.example.restro.viewmodel.SalesViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReservationFragment : Fragment(R.layout.fragment_reservation) {
    private val TAG = "ReservationFragment"
    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SalesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()
        showAppliedFilters()
        setUpBottomSheetView()
        handleSearchBarview()
    }

    fun handleSearchBarview() {

        val searchView = binding.searchBar

        // Start collapsed (icon only)
        searchView.onActionViewCollapsed()

        // When clicked → expand with animation
        searchView.setOnClickListener {
            searchView.onActionViewExpanded()
        }
        // When closed → collapse with animation
        searchView.setOnCloseListener {
            false
        }

        // Search events
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterData(it) }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterData(it) }
                return true
            }
        })
    }

    private fun filterData(query: String) {
        // val filteredList = reservationList.filter { reservation ->
        //     reservation.customer_name.contains(query, ignoreCase = true) ||
        //            reservation.reservation_code.contains(query, ignoreCase = true)
        // }

        // Update your RecyclerView adapter
        // reservationAdapter.submitList(filteredList)
    }

    private fun setupTabLayout() {

        val adapter = ReservationTabAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.reservationTabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "New Reservations"
                1 -> "Old Reservations"
                else -> ""
            }
        }.attach()
    }

    private fun setUpBottomSheetView() {
        val bottomSheet = FilterBottomSheet(emptySet())

        binding.iconFilterButton.setOnClickListener {
            bottomSheet.onFiltersApplied = {

            }
            bottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }
    }

    private fun showAppliedFilters() {
        lifecycleScope.launch {
            viewModel.selectedFilters.collect { filters ->
                binding.appliedFiltersChipGroup.removeAllViews()
                filters.forEach { filter ->
                    val chip = Chip(requireContext()).apply {
                        text = filter.name
                        isCloseIconVisible = true

                        setOnCloseIconClickListener {
                            binding.appliedFiltersChipGroup.removeView(this)
                            viewModel.removeFilters(filter)
                        }
                    }
                    binding.appliedFiltersChipGroup.addView(chip)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}