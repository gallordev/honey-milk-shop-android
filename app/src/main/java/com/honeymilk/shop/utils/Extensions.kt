package com.honeymilk.shop.utils

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.getText(): String {
    return this.editText?.text?.trim().toString()
}