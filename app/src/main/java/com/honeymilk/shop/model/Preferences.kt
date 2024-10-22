package com.honeymilk.shop.model

data class Preferences(
    val preferences: Map<String, MutableList<String>> = emptyMap()
)