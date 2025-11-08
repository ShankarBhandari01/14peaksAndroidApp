package com.example.restro.utils

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@BindingAdapter("afterTextChanged")
fun setAfterTextChangedListener(
    view: TextInputEditText,
    listener: ((String) -> Unit)?
) {
    view.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            listener?.invoke(s?.toString() ?: "")
        }
    })
}


@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("formattedDate")
fun TextView.setFormattedDate(isoDate: String?) {
    isoDate?.let {
        val zonedDateTime = ZonedDateTime.parse(it)
        val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
        val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
        text =
            "${zonedDateTime.format(dateFormatter)} (${zonedDateTime.format(dayFormatter)})"
    }
}
