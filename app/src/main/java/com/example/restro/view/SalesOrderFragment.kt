package com.example.restro.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restro.R
import com.example.restro.data.model.FilterOption
import com.example.restro.data.model.Sales
import com.example.restro.databinding.FragmentSalesOrderBinding
import com.example.restro.databinding.SalesListViewBinding
import com.example.restro.view.adapters.BasePagingAdapter
import com.example.restro.view.adapters.LoadingStateAdapter
import com.example.restro.view.adapters.SalesItemAdapter
import com.example.restro.view.adapters.ShimmerAdapter
import com.example.restro.view.bottom_sheet_dialog.FilterBottomSheet
import com.example.restro.viewmodel.SalesViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SalesOrderFragment : Fragment(R.layout.fragment_sales_order) {
    private val TAG = "SalesOrderFragment"
    private var _binding: FragmentSalesOrderBinding? = null
    private val binding get() = _binding!!
    private val sharedPool = RecyclerView.RecycledViewPool()

    private val activeFilters = mutableListOf<FilterOption>()


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
        setupFilterBottomSheet()

        viewModel.loadSalesOrders()


    }

    private fun setupFilterBottomSheet() {
        val bottomSheet = FilterBottomSheet()

        binding.iconFilterButton.setOnClickListener {

            bottomSheet.onFiltersApplied = { selectedFilters ->
                activeFilters.clear()
                activeFilters.addAll(selectedFilters)

                showAppliedFilters(activeFilters)
                viewModel.loadSalesOrders(sort = "asc")
            }
            bottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }
    }

    // observe sales order changes
    private fun refreshRecyclerView() {
        val shimmerAdapter = ShimmerAdapter()
        binding.salesRecyclerView.adapter = shimmerAdapter

        val pagingAdapter = BasePagingAdapter(
            inflate = SalesListViewBinding::inflate,
            bindItem = { binding, item: Sales ->
                with(binding) {
                    sales = item

                    if (item.status?.lowercase() == "accepted" || item.status?.lowercase() == "completed") {
                        llChangeStatus.actionLayout.visibility = ViewGroup.GONE
                    } else {
                        llChangeStatus.actionLayout.visibility = ViewGroup.VISIBLE
                    }

                    orderStatus.setTextColor(
                        when (item.status?.lowercase()) {
                            "delivered" -> "#388E3C".toColorInt()
                            "pending" -> "#FBC02D".toColorInt()
                            "cancelled" -> "#D32F2F".toColorInt()
                            else -> Color.BLACK
                        }
                    )

                    apply {
                        rvSalesItems.apply {
                            layoutManager = LinearLayoutManager(root.context)
                            adapter = SalesItemAdapter(item.items!!)
                            setRecycledViewPool(sharedPool)
                            setHasFixedSize(true)
                        }
                    }
                    llChangeStatus.btnAccept.setOnClickListener {
                        Toast.makeText(context, "accepted", Toast.LENGTH_SHORT).show()
                    }
                    llChangeStatus.btnAccept.setOnClickListener {
                        Toast.makeText(context, "rejected", Toast.LENGTH_SHORT).show()
                    }
                }

            },
            onItemClick = { sales ->
                Toast.makeText(context, "Clicked: ${sales.customer?.name}", Toast.LENGTH_SHORT)
                    .show()
            },
            diffCallback = object : DiffUtil.ItemCallback<Sales>() {
                override fun areItemsTheSame(oldItem: Sales, newItem: Sales) =
                    oldItem._id == newItem._id

                override fun areContentsTheSame(oldItem: Sales, newItem: Sales) =
                    oldItem == newItem
            }
        )


        lifecycleScope.launch {
            viewModel.salesPagingData.collectLatest { pagingData ->
                binding.salesRecyclerView.adapter = pagingAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter { pagingAdapter.retry() }
                )

                pagingAdapter.submitData(pagingData)
            }
        }


        pagingAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    binding.salesRecyclerView.adapter = shimmerAdapter
                }

                is LoadState.NotLoading -> {
                    binding.salesRecyclerView.adapter =
                        pagingAdapter.withLoadStateFooter(footer = LoadingStateAdapter { pagingAdapter.retry() })
                }

                is LoadState.Error -> {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun showAppliedFilters(filters: List<FilterOption>) {
        binding.appliedFiltersChipGroup.removeAllViews()

        filters.forEach { filter ->
            val chip = Chip(requireContext()).apply {
                text = filter.name
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    binding.appliedFiltersChipGroup.removeView(this)
                    activeFilters.remove(filter)
                }
            }
            binding.appliedFiltersChipGroup.addView(chip)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
