package com.example.restro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.restro.R
import com.example.restro.application.AppLifecycleTracker
import com.example.restro.data.model.SocketNotification
import com.example.restro.repos.SocketIORepository
import com.example.restro.utils.Utils
import com.example.restro.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.jvm.java

@AndroidEntryPoint
class SocketForegroundService : LifecycleService() {

    @Inject
    lateinit var socketRepository: SocketIORepository

    lateinit var userId: String


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
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
        if (AppLifecycleTracker.isAppInForeground) {
            // Send broadcast to activity
            val intent = Intent("SHOW_SOCKET_DIALOG")
            intent.putExtra("notification", notification)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } else {
            showSystemNotification(notification)
        }
    }

    private fun showSystemNotification(notification: SocketNotification) {
        val nm = getSystemService(NotificationManager::class.java)

        val channelId = "socket_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the channel with sound & high importance
            val channel = NotificationChannel(
                channelId,
                "Socket Notifications",
                NotificationManager.IMPORTANCE_HIGH // ensures heads-up
            ).apply {
                description = "Notifications from socket messages"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT
                )
            }
            nm.createNotificationChannel(channel)
        }

        //  fullScreenIntent for pop-up (heads-up)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(notification.title ?: "New update")
            .setContentText(notification.body ?: "You have a new message")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL) // vibration, lights, sound
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true) // heads-up pop-up

        nm.notify(System.currentTimeMillis().toInt(), builder.build())
    }


    override fun onDestroy() {
        socketRepository.disconnect()
        super.onDestroy()
    }
}
