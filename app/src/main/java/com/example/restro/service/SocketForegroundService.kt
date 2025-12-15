package com.example.restro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.restro.R
import com.example.restro.application.AppLifecycleTracker
import com.example.restro.data.model.SocketNotification
import com.example.restro.service.SocketIOService
import com.example.restro.utils.Utilities.canUseFullScreen
import com.example.restro.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.jar.Manifest
import javax.inject.Inject
import kotlin.jvm.java


@AndroidEntryPoint
class SocketForegroundService : LifecycleService() {

    @Inject
    lateinit var socketRepository: SocketIOService

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
            socketRepository.getMessage().collect { msg ->
                handleIncomingNotification(msg)
            }
        }

        lifecycleScope.launch {
            socketRepository.isConnected().collect { connected ->
                Timber.d("Socket ${if (connected) "connected" else "disconnected"}")
            }
        }

        return START_STICKY
    }

    private fun startForegroundNotification() {
        val channelId = "socket_channel"
        val nm = this.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Socket Connection", NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(channel)
        }

        val notification =
            NotificationCompat
                .Builder(this, channelId).setContentTitle("Listening for updates")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .build()

        startForeground(1, notification)


    }

    private fun handleIncomingNotification(notification: SocketNotification<Any>) {
        if (AppLifecycleTracker.isAppInForeground) {
            // Send broadcast to activity
            val intent = Intent("SHOW_SOCKET_DIALOG")
            intent.putExtra("notification", notification)
            intent.setPackage("com.example.restro")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } else {
            showSystemNotification(this, notification)
        }
    }

    private fun createHighPriorityChannel(context: Context): String {
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
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return channelId
    }

    private fun showSystemNotification(
        context: Context,
        notification: SocketNotification<Any>
    ) {
        // notification manager
        val nm = context.getSystemService(NotificationManager::class.java)
        val requestCode = System.currentTimeMillis().toInt()

        // create channel
        val channelId = createHighPriorityChannel(this)

        //  fullScreenIntent for pop-up (heads-up)
        val baseIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        baseIntent.setPackage("com.example.restro")

        // pending indent
        val currentPendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            baseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // full screen pending intent
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            requestCode + 1,
            baseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // notification builder
        val builder = NotificationCompat
            .Builder(this, channelId)
            .setContentTitle(notification.title ?: "New update")
            .setContentText(notification.body ?: "You have a new message")
            .setSmallIcon(R.drawable.logo)
            .setDefaults(Notification.DEFAULT_ALL) // vibration, lights, sound
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setAutoCancel(true)
            .setContentIntent(currentPendingIntent)

        // android 14 compatibility
        if (canUseFullScreen(this)) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(fullScreenPendingIntent, true)
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        }
        // notification
        val notification = builder.build()

        // notify to user
        nm.notify(requestCode, notification)
    }


    override fun onDestroy() {
        socketRepository.disconnect()
        super.onDestroy()
    }
}
