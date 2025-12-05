package com.example.restro.view.bottom_sheet_dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restro.data.model.FilterOption
import com.example.restro.databinding.BottomsheetFiltersBinding
import com.example.restro.view.adapters.FilterAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetFiltersBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: FilterAdapter
    private var selectedFilters = mutableListOf<FilterOption>()
    private val filters = mutableListOf<FilterOption>()
    lateinit var dialog: BottomSheetDialog
    var onFiltersApplied: ((List<FilterOption>) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                it.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }

        dialog.setCancelable(false)
        binding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadDynamicFilters()


        binding.btnApplyFilters.setOnClickListener {
            onFiltersApplied?.invoke(selectedFilters)
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        adapter = FilterAdapter(filters) { selectedFilter ->
            Timber.tag("Filter").d("${selectedFilter.name} = ${selectedFilter.isSelected}")
            selectedFilters.add(selectedFilter)
        }
        binding.rvFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFilters.adapter = adapter
    }

    private fun loadDynamicFilters() {
        val dynamicFilters = listOf(
            FilterOption("1", "Pending Orders"),
            FilterOption("2", "Delivered Orders"),
            FilterOption("3", "Last 7 Days"),
            FilterOption("4", "Last 30 Days")
        )
        filters.clear()
        filters.addAll(dynamicFilters)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
