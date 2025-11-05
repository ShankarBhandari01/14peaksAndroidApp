package com.example.restro.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BasePagingAdapter<T : Any, VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val bindItem: (VB, T) -> Unit,
    private val onItemClick: ((T) -> Unit)? = null,
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, BasePagingAdapter<T, VB>.BaseViewHolder>(diffCallback) {

    inner class BaseViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            bindItem(binding, item)
            binding.root.setOnClickListener { onItemClick?.invoke(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}