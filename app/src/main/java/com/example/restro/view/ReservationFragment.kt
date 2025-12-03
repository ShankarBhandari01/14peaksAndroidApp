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
import com.example.restro.data.model.FilterOption
import com.example.restro.data.model.Reservation
import com.example.restro.databinding.FragmentReservationBinding
import com.example.restro.databinding.ReservationItemBinding
import com.example.restro.utils.setFormattedDate
import com.example.restro.view.adapters.BasePagingAdapter
import com.example.restro.view.adapters.LoadingStateAdapter
import com.example.restro.view.adapters.ShimmerAdapter
import com.example.restro.view.bottom_sheet_dialog.FilterBottomSheet
import com.example.restro.viewmodel.SalesViewModel
import com.example.restro.viewmodel.NotificationViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer
import kotlin.getValue

@AndroidEntryPoint
class ReservationFragment : Fragment(R.layout.fragment_reservation) {
    private val TAG = "ReservationFragment"
    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!
    lateinit var bottomSheet: FilterBottomSheet
    private val activeFilters = mutableListOf<FilterOption>()

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

        setUpBottomSheetView()
        setUpRecyclerViewPagingData()



        binding.iconFilterButton.setOnClickListener {
            if (bottomSheet.isVisible) {
                bottomSheet.dismiss()
            }
        }


    }

    private fun setUpBottomSheetView() {
        bottomSheet = FilterBottomSheet()

        binding.iconFilterButton.setOnClickListener {
            if (bottomSheet.isVisible) {
                bottomSheet.dismiss()
            }

            bottomSheet.onFiltersApplied = { selectedFilters ->
                activeFilters.clear()
                activeFilters.addAll(selectedFilters)
                showAppliedFilters(activeFilters)
            }
            bottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }
    }

    private fun setUpRecyclerViewPagingData() {
        val shimmerAdapter = ShimmerAdapter()
        binding.reservationRecyclerView.adapter = shimmerAdapter

        val reservationAdapter = BasePagingAdapter(
            inflate = ReservationItemBinding::inflate,
            bindItem = { binding, reservation: Reservation ->
                with(binding) {
                    tvCustomerName.text = reservation.customer_name
                    tvReservationCode.text = "Code: ${reservation.reservation_code}"
                    tvReservationDate.setFormattedDate(reservation.reservation_date)
                    tvPhone.text = "📞 ${reservation.phone_number}"
                    tvGuests.text = "👥 ${reservation.number_of_guests} guests"
                    tvSpecialRequests.text =
                        reservation.special_requests.ifEmpty { "No special requests" }
                    tvCreatedDate.text = "Created: ${reservation.createdDate}"


                    if (reservation.status.lowercase() == "pending") {
                        llChangeStatus.actionLayout.visibility = ViewGroup.VISIBLE
                    } else {
                        llChangeStatus.actionLayout.visibility = ViewGroup.GONE
                    }


                    // Color status badge
                    val statusColor = when (reservation.status.lowercase()) {
                        "confirmed" -> ContextCompat.getColor(
                            root.context,
                            R.color.successColor
                        )

                        "pending" -> ContextCompat.getColor(
                            root.context,
                            R.color.warningColor
                        )

                        "cancelled" -> ContextCompat.getColor(
                            root.context,
                            R.color.errorColor
                        )

                        else -> ContextCompat.getColor(root.context, R.color.primaryColor)
                    }
                    (tvStatus.background as GradientDrawable).setColor(statusColor)
                    tvStatus.text = reservation.status


                    llChangeStatus.btnAccept.setOnClickListener {
                        Toast.makeText(context, "accepted", Toast.LENGTH_SHORT).show()
                    }
                    llChangeStatus.btnAccept.setOnClickListener {
                        Toast.makeText(context, "rejected", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onItemClick = { reservation ->
                Toast.makeText(
                    context,
                    "Clicked: ${reservation.customer_name}",
                    Toast.LENGTH_SHORT
                )
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
            viewModel.reservationData.collectLatest { pagingData ->
                binding.reservationRecyclerView.adapter =
                    reservationAdapter.withLoadStateFooter(
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

        // call load reservation api
        viewModel.loadReservations()
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
        //  socketIOViewModel.disconnect()
        _binding = null
    }

}

