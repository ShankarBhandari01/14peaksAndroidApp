package com.example.restro.view.bottom_sheet_dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restro.data.model.FilterOption
import com.example.restro.databinding.BottomsheetFiltersBinding
import com.example.restro.view.adapters.FilterAdapter
import com.example.restro.viewmodel.SalesViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import timber.log.Timber
import kotlin.getValue

@AndroidEntryPoint
class FilterBottomSheet(private var inputFilters: Set<FilterOption>) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetFiltersBinding? = null
    private val binding get() = _binding!!


    private val viewModel by viewModels<SalesViewModel>(
        ownerProducer = { requireParentFragment() })
    private val filters = mutableSetOf<FilterOption>()

    var onFiltersApplied: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

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

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDynamicFilters()
        setupRecyclerView()

        binding.btnApplyFilters.setOnClickListener {
            onFiltersApplied?.invoke()
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {

        val adapter = FilterAdapter(filters) { selectedFilter ->
            viewModel.addFilters(selectedFilter)
        }

        binding.rvFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFilters.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedFilters.collect { items ->

                    filters.forEach { item ->
                        item.isSelected = items.any { it.id == item.id }
                    }

                    adapter.notifyDataSetChanged()
                }
            }
        }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
