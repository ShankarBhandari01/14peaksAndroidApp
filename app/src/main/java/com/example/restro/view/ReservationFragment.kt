package com.example.restro.view

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import com.example.restro.R
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.databinding.FragmentReservationBinding
import com.example.restro.databinding.ReservationItemBinding
import com.example.restro.view.adapters.BasePagingAdapter
import com.example.restro.view.adapters.LoadingStateAdapter
import com.example.restro.view.adapters.ShimmerAdapter
import com.example.restro.view.bottom_sheet_dialog.FilterBottomSheet
import com.example.restro.viewmodel.SalesViewModel
import com.example.restro.viewmodel.SocketIOViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ReservationFragment : Fragment(R.layout.fragment_reservation) {

    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!
    // shared across multiple fragments

    private val activeFilters = mutableListOf<String>()
    private val socketIOViewModel: SocketIOViewModel by activityViewModels()

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

        val shimmerAdapter = ShimmerAdapter()
        binding.reservationRecyclerView.adapter = shimmerAdapter


        val reservationAdapter = BasePagingAdapter(
            inflate = ReservationItemBinding::inflate,
            bindItem = { binding, reservation: Reservation ->
                binding.tvCustomerName.text = reservation.customer_name
                binding.tvReservationCode.text = "Code: ${reservation.reservation_code}"
                binding.tvReservationDate.text = reservation.reservation_date
                binding.tvPhone.text = "📞 ${reservation.phone_number}"
                binding.tvGuests.text = "👥 ${reservation.number_of_guests} guests"
                binding.tvSpecialRequests.text =
                    reservation.special_requests.ifEmpty { "No special requests" }
                binding.tvCreatedDate.text = "Created: ${reservation.createdDate}"

                // Color status badge
                val statusColor = when (reservation.status.lowercase()) {
                    "confirmed" -> ContextCompat.getColor(
                        binding.root.context,
                        R.color.successColor
                    )

                    "pending" -> ContextCompat.getColor(binding.root.context, R.color.warningColor)
                    "cancelled" -> ContextCompat.getColor(binding.root.context, R.color.errorColor)
                    else -> ContextCompat.getColor(binding.root.context, R.color.primaryColor)
                }
                (binding.tvStatus.background as GradientDrawable).setColor(statusColor)
                binding.tvStatus.text = reservation.status
            },
            onItemClick = { reservation ->
                Toast.makeText(context, "Clicked: ${reservation.customer_name}", Toast.LENGTH_SHORT)
                    .show()
            },
            diffCallback = object : DiffUtil.ItemCallback<Reservation>() {
                override fun areItemsTheSame(oldItem: Reservation, newItem: Reservation) =
                    oldItem._id == newItem._id

                override fun areContentsTheSame(oldItem: Reservation, newItem: Reservation) =
                    oldItem == newItem
            }
        )


        lifecycleScope.launch {
            viewModel.loadReservations().collectLatest { pagingData ->
                binding.reservationRecyclerView.adapter = reservationAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter { reservationAdapter.retry() }
                )
                reservationAdapter.submitData(pagingData)
            }

        }

        reservationAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    binding.reservationRecyclerView.adapter = shimmerAdapter
                }

                is LoadState.NotLoading -> {
                    // Show actual data
                    binding.reservationRecyclerView.adapter =
                        reservationAdapter.withLoadStateFooter(footer = LoadingStateAdapter { reservationAdapter.retry() })
                }

                is LoadState.Error -> {
                    // Handle errors
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.iconFilterButton.setOnClickListener {
            val bottomSheet = FilterBottomSheet()
            bottomSheet.onFiltersApplied = { selectedFilters ->
                activeFilters.clear()
                activeFilters.addAll(selectedFilters)
                showAppliedFilters(activeFilters)
            }
            bottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }


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
                }
            }
            binding.appliedFiltersChipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
      //  socketIOViewModel.disconnect()
        _binding = null
    }
}
