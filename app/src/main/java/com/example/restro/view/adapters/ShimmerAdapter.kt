package com.example.restro.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restro.R
import com.facebook.shimmer.ShimmerFrameLayout

class ShimmerAdapter : RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sales_shimmer, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        val shimmer = holder.itemView.findViewById<ShimmerFrameLayout>(R.id.shimmerLayout)
        shimmer.startShimmer()
    }

    override fun getItemCount(): Int = 6

    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}