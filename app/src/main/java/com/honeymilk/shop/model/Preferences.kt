package com.honeymilk.shop.model

data class Preferences(
    val colorList: MutableList<String> = mutableListOf(),
    val sizeList: MutableList<String> = mutableListOf(),
    val typeList: MutableList<String> = mutableListOf(),
    val notificationToken: String = ""
)