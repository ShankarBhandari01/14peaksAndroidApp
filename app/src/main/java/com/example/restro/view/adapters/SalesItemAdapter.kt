package com.example.restro.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restro.data.model.OrderItems
import com.example.restro.databinding.ItemSalesItemBinding

class SalesItemAdapter(private val items: List<OrderItems>) :
    RecyclerView.Adapter<SalesItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: ItemSalesItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemSalesItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val orderItem = items[position]
        holder.binding.items = orderItem
    }

    override fun getItemCount(): Int = items.size
}
