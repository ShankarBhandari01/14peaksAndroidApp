package com.example.restro.utils

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText

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

