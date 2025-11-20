package com.example.baro.core.util

import android.content.Context
import android.view.View
import android.widget.Toast

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun Context.showToast(message: String, long: Boolean = false) {
    Toast.makeText(
        this,
        message,
        if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}
