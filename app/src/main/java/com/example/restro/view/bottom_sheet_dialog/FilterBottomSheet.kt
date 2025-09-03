package com.example.restro.view.bottom_sheet_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.restro.databinding.BottomsheetFiltersBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetFiltersBinding? = null
    private val binding get() = _binding!!

    var onFiltersApplied: ((List<String>) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetFiltersBinding.inflate(inflater, container, false)

        binding.applyFiltersButton.setOnClickListener {
            val selected = mutableListOf<String>()
            if (binding.filterStatusPending.isChecked) selected.add("Status: Pending")
            if (binding.filterStatusDelivered.isChecked) selected.add("Status: Delivered")
            if (binding.filterLast7Days.isChecked) selected.add("Date: Last 7 Days")
            if (binding.filterLast30Days.isChecked) selected.add("Date: Last 30 Days")

            onFiltersApplied?.invoke(selected)
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
