package com.example.restro.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restro.R
import com.example.restro.utils.Utils

class LoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadingViewHolder>() {

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        if (loadState is LoadState.Error) {
            Utils.showAlertDialog(
                message = loadState.error.localizedMessage,
                context = holder.itemView.context
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false)
        return LoadingViewHolder(view)
    }
}
