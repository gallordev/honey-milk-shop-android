package com.honeymilk.shop.utils

import android.view.View
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

fun TextInputLayout.getText(): String {
    return this.editText?.text?.trim().toString()
}

fun String.toValidFloat(): Float {
    return this.toFloatOrNull() ?: 0f
}

fun TextInputLayout.setText(value: String) {
    this.editText?.setText(value)
}

fun TextInputLayout.setText(value: Number) {
    this.editText?.setText(value.toString())
}

fun View.hide(hide: Boolean) {
    this.visibility = if (hide) View.INVISIBLE else View.VISIBLE
}

fun View.isGone(isGone: Boolean) {
    this.visibility = if (isGone) View.GONE else View.VISIBLE
}

object Extensions {
    fun Float.toCurrencyFormat(): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance(Locale.getDefault())
        return format.format(this)
    }

}

