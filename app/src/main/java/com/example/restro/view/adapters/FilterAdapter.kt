package com.example.restro.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restro.data.model.FilterOption
import com.example.restro.databinding.ItemFilterOptionBinding

class FilterAdapter(
    private val filters: MutableList<FilterOption>,
    private val onFilterToggled: (FilterOption) -> Unit
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {
    inner class FilterViewHolder(val binding: ItemFilterOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FilterOption) {
            with(binding.checkBoxFilter) {
                setOnCheckedChangeListener(null)

                text = item.name
                isChecked = item.isSelected

                setOnCheckedChangeListener { _, isChecked ->
                    item.isSelected = isChecked
                    onFilterToggled(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = ItemFilterOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int = filters.size
}
