package com.example.restro.view.notification

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.restro.R
import com.example.restro.data.model.Notification
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.model.SocketNotification
import com.example.restro.databinding.ActivityNotificationDetailsBinding
import com.example.restro.utils.Utilities.to
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import java.util.Locale.getDefault

@AndroidEntryPoint
class NotificationDetailsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityNotificationDetailsBinding.inflate(layoutInflater)
    }
    val gson = Gson()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        val notification =
            intent.getSerializableExtra("notification", SocketNotification::class.java)

        binding.actionBtns.actionLayout.visibility = View.VISIBLE

        if (notification?.type?.lowercase() == "reservation") {
            val json = gson.toJson(notification.data)
            val data = json.to<Reservation>()

            // Show reservation card, hide order card
            binding.cardReservation.visibility = View.VISIBLE
            binding.cardOrder.visibility = View.GONE

            // Customer info
            binding.tvCustomerName.text = data.customer_name
            binding.tvCustomerEmail.text = data.customer_email
            binding.tvCustomerPhone.text = data.phone_number

            // Reservation info
            binding.tvReservationDate.text = "Date: ${formatDate(data.reservation_date)}"
            binding.tvGuests.text = "Guests: ${data.number_of_guests}"
            binding.tvSpecialRequests.text =
                "Special Requests: ${data.special_requests.ifEmpty { "None" }}"
            binding.tvReservationStatus.text = "Status: ${
                data.status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        getDefault()
                    ) else it.toString()
                }
            }"
            binding.tvReservationCode.text = "Reservation Code: ${data.reservation_code}"

        } else if (notification?.type?.lowercase() == "order") {
            val json = gson.toJson(notification.data)
            val data = json.to<Sales>()

            // Show order card, hide reservation card
            binding.cardReservation.visibility = View.GONE
            binding.cardOrder.visibility = View.VISIBLE

            // Customer info
            binding.tvCustomerName.text = data.customer?.name
            binding.tvCustomerEmail.text = data.customer?.email
            binding.tvCustomerPhone.text = data.customer?.phone

            // Order info
            binding.tvOrderType.text = "Order Type: ${data.orderType}"
            binding.tvOrderStatus.text = "Status: ${
                data.status?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        getDefault()
                    ) else it.toString()
                }
            }"
            binding.tvOrderId.text = "Order ID: ${data.orderId}"

            // Clear previous items
            binding.containerOrderItems.removeAllViews()

            // Populate order items dynamically
            data.items?.forEach { item ->
                val itemView = TextView(this).apply {
                    text = "• ${item.name.en} x${item.quantity} - €${item.totalPrice}"
                    setPadding(0, 4, 0, 4)
                    textSize = 15f
                }
                binding.containerOrderItems.addView(itemView)
            }

            // Totals
            binding.tvSubtotal.text = "Subtotal: €${data.subtotal}"
            binding.tvVat.text = "VAT (${data.vatPercent}%): €${data.vatAmount}"
            binding.tvTotal.text = "Total: €${data.totalAmount}"
        }


    }

    inline fun <reified T> SocketNotification<*>.getDataAs(): T? {
        return if (data is T) data as T else null
    }

    fun formatDate(dateStr: String): String {
        return try {
            val parser =
                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", getDefault())
            val formatter = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", getDefault())
            val date = parser.parse(dateStr)
            formatter.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }
}