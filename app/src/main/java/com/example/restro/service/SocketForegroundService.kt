package com.example.restro.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.restro.R
import com.example.restro.data.model.SocketNotification
import com.example.restro.repos.SocketIORepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SocketForegroundService : LifecycleService() {


    @Inject
    lateinit var socketRepository: SocketIORepository

    lateinit var userId: String


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("USER_ID") ?: return START_NOT_STICKY

        // Start foreground
        startForegroundNotification()

        // Connect socket
        socketRepository.connect(userId)

        // Collect messages
        lifecycleScope.launch {
            socketRepository.messages.collect { msg ->
                handleIncomingNotification(msg)
            }
        }

        lifecycleScope.launch {
            socketRepository.isConnected.collect { connected ->
                Timber.d("Socket ${if (connected) "connected" else "disconnected"}")
            }
        }

        return START_STICKY
    }

    private fun startForegroundNotification() {
        val channelId = "socket_channel"
        val nm = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Socket Connection", NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Listening for updates")
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .build()

        startForeground(1, notification)
    }

    private fun handleIncomingNotification(notification: SocketNotification) {
        // if (AppLifecycleTracker.isAppInForeground) {
        // Show custom dialog
        // } else {
        // Show system notification
        showSystemNotification(notification)
        //}
    }

    private fun showSystemNotification(notification: SocketNotification) {
        val nm = getSystemService(NotificationManager::class.java)
        val builder = NotificationCompat.Builder(this, "socket_channel")
            .setContentTitle(notification.title ?: "New update")
            .setContentText(notification.body ?: "You have a new message")
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        nm.notify(0, builder.build())
    }

    override fun onDestroy() {
        socketRepository.disconnect()
        super.onDestroy()
    }
}
