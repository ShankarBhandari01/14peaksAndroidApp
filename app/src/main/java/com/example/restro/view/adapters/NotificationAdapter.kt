package com.example.restro.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.restro.R
import com.example.restro.data.model.Notification

class NotificationAdapter :
    PagingDataAdapter<Notification, NotificationAdapter.NotificationViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem._id == newItem._id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem == newItem
        }
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvEmail = itemView.findViewById<TextView>(R.id.tvEmail)

        fun bind(notification: Notification) {
            tvName.text = notification._id
            tvEmail.text = notification.title
        }
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_layout, parent, false)
        return NotificationViewHolder(view)
    }


}


