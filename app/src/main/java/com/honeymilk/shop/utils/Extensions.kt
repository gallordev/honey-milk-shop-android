package com.honeymilk.shop.utils

import com.google.android.material.textfield.TextInputLayout
import java.util.Date

fun TextInputLayout.getText(): String {
    return this.editText?.text?.trim().toString()
}

fun Long.toDate() : Date? {
    return Date(this)
}