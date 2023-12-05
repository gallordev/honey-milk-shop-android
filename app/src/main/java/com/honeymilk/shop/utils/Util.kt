package com.honeymilk.shop.utils

import java.util.Date

object Util {

    fun Long.toDate() : Date? {
        return Date(this)
    }

}