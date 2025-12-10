package com.example.restro.view.reservations

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import com.example.restro.R
import com.example.restro.data.model.Reservation
import com.example.restro.databinding.FragmentOldReservationFragmentsBinding
import com.example.restro.databinding.ReservationItemBinding
import com.example.restro.databinding.ReservationItemBinding.inflate
import com.example.restro.utils.Utilities.applyGradient
import com.example.restro.utils.setFormattedDate
import com.example.restro.view.adapters.BasePagingAdapter
import com.example.restro.view.adapters.LoadingStateAdapter
import com.example.restro.view.adapters.ReservationTabAdapter
import com.example.restro.view.adapters.ShimmerAdapter
import com.example.restro.viewmodel.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import kotlin.getValue
import kotlin.text.ifEmpty

@AndroidEntryPoint
class OldReservationFragments() :
    Fragment(R.layout.fragment_old_reservation_fragments) {
    private var _binding: FragmentOldReservationFragmentsBinding? = null
    private val binding get() = _binding!!

    lateinit var reservationJob: Job
    private val viewModel by viewModels<SalesViewModel>(
        ownerProducer = { requireParentFragment() }
    )

    companion object {
        fun newInstance(type: String): OldReservationFragments {
            return OldReservationFragments().apply {
                arguments = bundleOf("type" to type)
            }
        }
    }

    private val type: String by lazy {
        requireArguments().getString("type") ?: "new"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOldReservationFragmentsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setReservationType(type)
        setUpRecyclerViewPagingData()
    }


    private fun setUpRecyclerViewPagingData() {
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

                    val reservationDate =
                        LocalDate.parse(reservation.reservation_date.take(10))
                    // Take only yyyy-MM-dd part if your string is ISO format "2025-12-06T12:00:21.791Z"

                    val today = LocalDate.now()
                    when {
                        reservationDate.isEqual(today) -> {
                            tvStatusToday.visibility = View.VISIBLE
                            tvStatusToday.text = getString(R.string.today)
                            tvStatusToday.applyGradient(R.drawable.grad_today)
                        }

                        reservationDate.isAfter(today) -> {
                            tvStatusToday.visibility = View.VISIBLE
                            tvStatusToday.text = getString(R.string.upcoming)
                            tvStatusToday.applyGradient(R.drawable.grad_upcoming)
                        }

                        reservationDate.isBefore(today) -> {
                            tvStatusToday.visibility = View.VISIBLE
                            tvStatusToday.text = getString(R.string.past)
                            tvStatusToday.applyGradient(R.drawable.grad_past)
                        }

                        else -> tvStatusToday.visibility = View.GONE
                    }


                    if (reservation.status.lowercase() == "pending") {
                        llChangeStatus.actionLayout.visibility = ViewGroup.VISIBLE
                    } else {
                        llChangeStatus.actionLayout.visibility = ViewGroup.GONE
                    }


                    // Color status badge
                    val statusColor = when (reservation.status.lowercase()) {
                        "confirmed" -> ContextCompat.getColor(
                            root.context,
                            R.color.md_theme_onPrimaryContainer
                        )

                        "pending" -> ContextCompat.getColor(
                            root.context,
                            R.color.md_theme_errorContainer_mediumContrast
                        )

                        "cancelled" -> ContextCompat.getColor(
                            root.context,
                            R.color.md_theme_error
                        )

                        else -> ContextCompat.getColor(root.context, R.color.md_theme_onPrimaryContainer)
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


        binding.reservationRecyclerView.adapter =
            reservationAdapter.withLoadStateFooter(LoadingStateAdapter { reservationAdapter.retry() })

        reservationAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> binding.reservationRecyclerView.adapter = ShimmerAdapter()
                is LoadState.NotLoading -> binding.reservationRecyclerView.adapter =
                    reservationAdapter.withLoadStateFooter(LoadingStateAdapter { reservationAdapter.retry() })

                is LoadState.Error -> Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // handles the version of data
        reservationJob = lifecycleScope.launch {
            viewModel.reservations.collectLatest {
                reservationAdapter.submitData(it)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}