package com.chandana.quotecrate.utils.extensions

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import com.chandana.quotecrate.R

fun AppCompatImageView.setPasswordVisibility(
    passwordET: EditText,
    isPasswordVisible: Boolean
): Boolean {
    return if (isPasswordVisible) {
        passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
        this.setImageResource(R.drawable.visibility_on_icon)
        false
    } else {
        passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
        this.setImageResource(R.drawable.visibility_off_icon)
        true
    }.also {
        passwordET.setSelection(passwordET.text.length)
    }
}
