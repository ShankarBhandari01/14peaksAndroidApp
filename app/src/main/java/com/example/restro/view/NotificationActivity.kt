package com.example.restro.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.example.restro.R
import com.example.restro.data.model.Notification
import com.example.restro.databinding.ActivityNotificationBinding
import com.example.restro.databinding.NotificationLayoutBinding
import com.example.restro.utils.Utils.setDrawableStartClickListener
import com.example.restro.view.adapters.BasePagingAdapter
import com.example.restro.view.adapters.LoadingStateAdapter
import com.example.restro.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context) = Intent(context, NotificationActivity::class.java)
    }

    val binding by lazy {
        ActivityNotificationBinding.inflate(layoutInflater)
    }
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        // Handle click
        binding.tvNotificationTitle.setDrawableStartClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val notificationAdapter = BasePagingAdapter(
            inflate = NotificationLayoutBinding::inflate,
            bindItem = { binding, item: Notification ->
                binding.tvTitle.text = item.title
                binding.tvMessage.text = item.message
                binding.tvDate.text = item.createdAt

                binding.viewIndicator.visibility = if (item.isRead) View.INVISIBLE else View.VISIBLE

            },
            onItemClick = { notification ->
                Toast.makeText(this, "Clicked: ${notification.title}", Toast.LENGTH_SHORT).show()
            },
            diffCallback = object : DiffUtil.ItemCallback<Notification>() {
                override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                    oldItem._id == newItem._id

                override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
                    oldItem == newItem
            }
        )

        // load notifications
        lifecycleScope.launch {
            notificationViewModel.notification.collectLatest { pagingData ->
                binding.recyclerView.adapter = notificationAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter { notificationAdapter.retry() }
                )
                notificationAdapter.submitData(pagingData)

            }
        }

    }
}