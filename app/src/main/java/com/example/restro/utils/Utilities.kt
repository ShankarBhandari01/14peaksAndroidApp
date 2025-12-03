package com.example.restro.utils

import android.R.drawable.ic_dialog_alert
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.Window
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.restro.R
import com.example.restro.databinding.DialogNotificationPopupBinding
import com.example.restro.databinding.DialogProgressBinding
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

object Utils {

    fun Context.isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = packageName
        return appProcesses.any {
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    it.processName == packageName
        }
    }


    fun sendNotification(context: Context, message: String) {
        val channelId = "socket_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel if Android >= O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Socket Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("New Message")
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    @SuppressLint("HardwareIds")
    suspend fun extractDeviceId(context: Context): String {
        return try {
            // Try Google Advertising ID first
            val adInfo = withContext(Dispatchers.IO) {
                AdvertisingIdClient.getAdvertisingIdInfo(context)
            }
            val adId = adInfo.id
            if (!adId.isNullOrBlank() && adId != "00000000-0000-0000-0000-000000000000") {
                adId
            } else {
                // Fallback → ANDROID_ID
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            }
        } catch (e: Exception) {
            // In case Google Play Services is missing or blocked
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    }

    fun getGreetingMessage(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return when (timeOfDay) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..19 -> "Good Evening"
            in 20..23 -> "Good Night"
            else -> "welcome_back"
        }
    }

    // ICMP
    fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    @SuppressLint("ServiceCast")
    fun hasInternetConnection(context: Context?): Boolean {
        try {
            if (context == null) return false
            val connectivityManager =
                context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } catch (e: Exception) {
            return false
        }
    }

    fun showAlertDialog(context: Context, message: String?) {
        try {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.app_name)
            builder.setMessage(message)
            builder.setIcon(ic_dialog_alert)
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

            val alertDialog: AlertDialog = builder.create()

            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
            alertDialog.setCancelable(false)
            alertDialog.show()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    private var progressDialog: Dialog? = null
    fun showProgressDialog(message: String?, context: Context) {
        if (context !is Activity) {
            return
        }
        if (context.isFinishing || context.isDestroyed) {
            return
        }
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
            progressDialog = null
        }
        progressDialog = Dialog(context)
        val bind = DialogProgressBinding.inflate(LayoutInflater.from(context))
        bind.root.background = ContextCompat.getDrawable(context, R.drawable.rounded_corner)
        progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog?.setContentView(bind.root)
        bind.animationView.playAnimation()
        bind.message.text = message
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    fun isActivityValid(activity: Activity): Boolean {
        return !activity.isFinishing && !activity.isDestroyed
    }

    fun dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }


    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun getApiErrorMessage(error: String = ""): String? {
        var jsonObject: JSONObject?
        var errorMessage: String?
        return try {
            jsonObject = JSONObject(error)
            errorMessage = jsonObject.getString("message")
            errorMessage
        } catch (e: JSONException) {
            e.localizedMessage
        }
    }


    fun getCurrentDateTimeWithAMPM(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    fun showDateTimePickerDialog(context: Context, onDateTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePickerDialog(context, calendar, onDateTimeSelected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(
        context: Context, calendar: Calendar, onDateTimeSelected: (String) -> Unit
    ) {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val selectedDateTime = context.formatDateTime(calendar)
                onDateTimeSelected(selectedDateTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 24-hour format
        )
        timePickerDialog.show()
    }

    fun Context.formatDateTime(calendar: Calendar = Calendar.getInstance()): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    fun Context.formatDuration(durationMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    //
    fun Context.showNotificationPopup(
        title: String,
        message: String,
        onViewClick: (() -> Unit)? = null
    ) {
        val dialogView = DialogNotificationPopupBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView.root)
            .create()

        dialogView.tvNotificationTitle.text = title
        dialogView.tvNotificationMessage.text = message

        dialogView.btnClose.setOnClickListener { dialog.dismiss() }
        dialogView.btnView.setOnClickListener {
            dialog.dismiss()
            onViewClick?.invoke()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


    fun TextView.setDrawableStartClickListener(onClick: () -> Unit) {
        this.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                val drawables = this.compoundDrawablesRelative
                val drawableStart: Drawable? = drawables[0] // start drawable

                if (drawableStart != null) {
                    val drawableWidth = drawableStart.bounds.width()
                    val touchX = event.x.toInt()

                    // If user touched within drawableStart bounds
                    if (touchX <= paddingStart + drawableWidth) {
                        onClick()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }


    inline fun <reified T> String.to(): T {
        return Gson().fromJson(this, object : TypeToken<T>() {}.type)
    }
}