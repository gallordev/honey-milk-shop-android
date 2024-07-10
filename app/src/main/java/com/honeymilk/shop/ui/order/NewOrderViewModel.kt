package com.honeymilk.shop.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.model.OrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor() : ViewModel() {

    private val _orderItems: MutableLiveData<List<OrderItem>> = MutableLiveData(null)
    val orderItems: LiveData<List<OrderItem>>
        get() = _orderItems

    fun addOrderItems(designs: List<Design>) {
        val newOrderItems = designs.map { OrderItem(design = it) }.toMutableList()
        val currentOrders = _orderItems.value
        currentOrders?.let { newOrderItems.addAll(it) }
        _orderItems.value = newOrderItems
    }

    fun removeOrderItem(orderItem: OrderItem) {
        _orderItems.value = _orderItems.value?.dropWhile { it.id == orderItem.id }
    }

}