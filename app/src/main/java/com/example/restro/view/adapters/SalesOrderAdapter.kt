package com.example.restro.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restro.databinding.SalesListViewBinding
import com.example.restro.data.model.Sales


class SalesOrderAdapter(private val sales: List<Sales>) :
    RecyclerView.Adapter<SalesOrderAdapter.SalesViewHolder>() {

    inner class SalesViewHolder(val binding: SalesListViewBinding) :
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
                "delivered" -> Color.parseColor("#388E3C")
                "pending" -> Color.parseColor("#FBC02D")
                "cancelled" -> Color.parseColor("#D32F2F")
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
