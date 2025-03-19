package com.chandana.quotecrate.utils.extensions

import android.content.Context
import android.widget.Toast

fun Context.displayMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}