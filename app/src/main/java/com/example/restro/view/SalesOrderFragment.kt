package com.example.restro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.restro.R
import com.example.restro.databinding.FragmentSalesOrderBinding
import com.example.restro.data.model.Sales
import com.example.restro.data.model.ApiResponse
import com.example.restro.utils.UiEvent
import com.example.restro.utils.Utils
import com.example.restro.view.adapters.SalesOrderAdapter
import com.example.restro.view.bottom_sheet_dialog.FilterBottomSheet
import com.example.restro.viewmodel.SalesViewModel
import com.example.restro.viewmodel.SocketIOViewModel
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class SalesOrderFragment : Fragment(R.layout.fragment_sales_order) {
    private val TAG = "SalesOrderFragment"
    private var _binding: FragmentSalesOrderBinding? = null
    private val binding get() = _binding!!

    // shared across multiple fragments
    private val socketIOViewModel: SocketIOViewModel by activityViewModels()

    private val activeFilters = mutableListOf<String>()

    private val salesList = mutableListOf<Sales>()
    private lateinit var adapter: SalesOrderAdapter
    private val viewModel by viewModels<SalesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshRecyclerView()
        viewModel.loadSalesOrders("desc", 1, 10)
        observeUiEvents()

        binding.iconFilterButton.setOnClickListener {
            val bottomSheet = FilterBottomSheet()
            bottomSheet.onFiltersApplied = { selectedFilters ->
                activeFilters.clear()
                activeFilters.addAll(selectedFilters)
                showAppliedFilters(activeFilters)
                refreshRecyclerView()
            }
            bottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }

        observeSocketIO()
    }

    // observe sales order changes
    private fun observeSocketIO() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                socketIOViewModel.latestMessage.collect { message ->
                    if (message != null) {
                        Timber.d("New socket message: $message")
                        // TODO: parse message -> update salesList or notify adapter
                    }
                }
            }
        }
    }


    private fun refreshRecyclerView() {
        adapter = SalesOrderAdapter(salesList)
        binding.salesRecyclerView.adapter = adapter
    }

    private fun showAppliedFilters(filters: List<String>) {
        binding.appliedFiltersChipGroup.removeAllViews()

        filters.forEach { filter ->
            val chip = Chip(requireContext()).apply {
                text = filter
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    binding.appliedFiltersChipGroup.removeView(this)
                    activeFilters.remove(filter)
                    refreshRecyclerView()
                }
            }
            binding.appliedFiltersChipGroup.addView(chip)
        }
    }

    private fun observeUiEvents() {
        viewModel.uiEvents.observe(viewLifecycleOwner) { event ->
            when (event) {
                is UiEvent.ShowLoading -> Utils.showProgressDialog(
                    "Loading the sales orders…",
                    activity as MainActivity
                )

                is UiEvent.HideLoading -> Utils.dismissProgressDialog()
                is UiEvent.ShowMessage -> {
                    Timber.tag(TAG).e("observeUiEvents: ${event.message} ")
                    Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is UiEvent.Navigate -> {
                    val salesData = event.data as ApiResponse<Sales>
                    Timber.tag(TAG).e("observeUiEvents: ${Gson().toJson(salesData)} ")
                    salesList.clear()
                    salesList.addAll(salesData.data)
                    adapter.notifyDataSetChanged()
                }

                is UiEvent.NavigateToActivity -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        socketIOViewModel.disconnect()
        _binding = null
    }
}
