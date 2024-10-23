package com.honeymilk.shop.utils

import android.content.Context

object PreferencesHelper {

    private const val PREFS_NAME = "my_prefs"
    private const val COUNTER_KEY = "counter"

    fun getCounter(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(COUNTER_KEY, 0) // Default value is 0
    }

    fun incrementCounter(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(COUNTER_KEY, getCounter(context) + 1)
        editor.apply()
    }

}