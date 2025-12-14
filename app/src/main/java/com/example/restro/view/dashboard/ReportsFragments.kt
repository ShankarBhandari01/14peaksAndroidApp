package com.example.restro.view.dashboard

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
import com.example.restro.data.model.Reports
import com.example.restro.databinding.FragmentReportsFragmentsBinding
import com.example.restro.utils.Charts.setupBarChart
import com.example.restro.utils.Charts.setupLineChart
import com.example.restro.utils.UiState
import com.example.restro.utils.Utilities.toEuroFi
import com.example.restro.viewmodel.ReportsViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportsFragments : Fragment(R.layout.fragment_reports_fragments) {
    private var _binding: FragmentReportsFragmentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ReportsViewmodel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.generateReports()
        observeViewModel()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsFragmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun FragmentReportsFragmentsBinding.bindReports(reports: Reports) = apply {
        lTotalSalesSum.tvTotalSales.text = reports.totalSales.toEuroFi()

        itemSalesLayout.tvCountSales.text = reports.countSales.toString()

        lItemTodayReservations.tvTodaysReservation.text = reports.todaysReservation.toString()

        lItemPendingReservations.tvPendingReservations.text = reports.pendingReservations.toString()

        lItemTotalReservations.tvTotalReservations.text = reports.totalReservations.toString()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reportsUiState.collect { state ->

                    when (state) {
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                            val reports: Reports = state.data as Reports
                            binding.bindReports(reports)

                            binding.chartLayout.apply {
                                tvLineChartTitle.text = "Orders Counts "
                                setupLineChart(lineChart, reports.charts.orders)
                                tvBarCharttTitle.text = "Reservation Counts "
                                setupBarChart(barChart, reports.charts.reservations)
                            }
                        }

                        is UiState.Error -> {
                            Toast.makeText(
                                requireContext(), state.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

}