package com.example.restro.utils

import android.graphics.Color
import com.example.restro.data.model.OrderReports
import com.example.restro.data.model.ReservationReports
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Charts {
    fun setupLineChart(lineChart: LineChart, orders: List<OrderReports>) {
        if (orders.isEmpty()) return

        // Prepare entries for chart
        val entries = orders.mapIndexed { index, order ->
            Entry(index.toFloat(), order.total)
        }

        val dataSet = LineDataSet(entries, "Orders")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(true)
        dataSet.circleRadius = 4f

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Format X-axis labels
        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val index = value.toInt()
                return if (index >= 0 && index < orders.size) {
                    // Format date as "dd MMM" e.g., "15 Nov"
                    val localDate = LocalDate.parse(orders[index].date.substring(0, 10))
                    localDate.format(DateTimeFormatter.ofPattern("dd MMM"))
                } else ""
            }
        }

        lineChart.xAxis.valueFormatter = formatter
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.isGranularityEnabled = true
        lineChart.xAxis.setDrawGridLines(true)
        lineChart.axisRight.isEnabled = true

        lineChart.description.isEnabled = true
        lineChart.invalidate() // refresh chart
    }

    fun setupBarChart(barChart: BarChart, reservations: List<ReservationReports>) {
        if (reservations.isEmpty()) return

        // Prepare bar entries
        val entries = reservations.mapIndexed { index, reservation ->
            BarEntry(index.toFloat(), reservation.count.toFloat())
        }

        val dataSet = BarDataSet(entries, "Reservations")
        dataSet.color = Color.MAGENTA
        dataSet.valueTextColor = Color.BLACK

        val barData = BarData(dataSet)
        barChart.data = barData

        // Format X-axis with dates
        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val index = value.toInt()
                return if (index >= 0 && index < reservations.size) {
                    val localDate = LocalDate.parse(reservations[index].date.substring(0, 10))
                    localDate.format(DateTimeFormatter.ofPattern("dd MMM"))
                } else ""
            }
        }

        barChart.xAxis.valueFormatter = formatter
        barChart.xAxis.granularity = 1f
        barChart.xAxis.isGranularityEnabled = true
        barChart.xAxis.setDrawGridLines(true)
        barChart.axisRight.isEnabled = true
        barChart.description.isEnabled = true

        barChart.invalidate() // refresh chart
    }
}