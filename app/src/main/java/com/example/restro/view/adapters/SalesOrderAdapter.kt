package com.example.restro.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restro.databinding.SalesListViewBinding
import com.example.restro.data.model.Sales
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager


class SalesOrderAdapter(private val sales: List<Sales>) :
    RecyclerView.Adapter<SalesOrderAdapter.SalesViewHolder>() {

    class SalesViewHolder(val binding: SalesListViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder =
        SalesViewHolder(
            SalesListViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        val sale = sales[position]
        holder.binding.sales = sale

        if (sale.status.lowercase() == "accepted") {
            holder.binding.llChangeStatus.visibility = ViewGroup.GONE
        } else {
            holder.binding.llChangeStatus.visibility = ViewGroup.VISIBLE
        }

        holder.binding.orderStatus.setTextColor(
            when (sale.status.lowercase()) {
                "delivered" -> "#388E3C".toColorInt()
                "pending" -> "#FBC02D".toColorInt()
                "cancelled" -> "#D32F2F".toColorInt()
                else -> Color.BLACK
            }
        )

        holder.binding.btnAccept.setOnClickListener {
            // Handle accept button click
        }
        holder.binding.btnReject.setOnClickListener {
            // Handle reject button click
        }

    }

    override fun getItemCount(): Int = sales.size
}
