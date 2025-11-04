package com.example.restro.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.restro.R
import com.example.restro.databinding.ActivityNotificationBinding
import com.example.restro.view.adapters.LoadingStateAdapter
import com.example.restro.view.adapters.NotificationAdapter
import com.example.restro.viewmodel.SocketIOViewModel
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
    private val socketIOViewModel: SocketIOViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        notificationAdapter = NotificationAdapter()
        binding.recyclerView.adapter = notificationAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { notificationAdapter.retry() }
        )

        // load notifications
        lifecycleScope.launch {
            socketIOViewModel.notification.collectLatest { pagingData ->
                notificationAdapter.submitData(pagingData)
            }
        }


    }
}