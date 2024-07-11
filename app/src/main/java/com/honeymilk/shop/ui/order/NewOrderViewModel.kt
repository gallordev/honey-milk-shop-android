package com.honeymilk.shop.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.model.OrderItem
import com.honeymilk.shop.repository.OrderRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderItems: MutableLiveData<List<OrderItem>> = MutableLiveData(null)
    val orderItems: LiveData<List<OrderItem>>
        get() = _orderItems

    private val _resource = MutableLiveData<Resource<String>>(null)
    val resource: LiveData<Resource<String>>
        get() = _resource

    fun addOrderItems(designs: List<Design>) {
        val newOrderItems = designs.map { OrderItem(design = it) }.toMutableList()
        val currentOrders = _orderItems.value
        currentOrders?.let { newOrderItems.addAll(it) }
        _orderItems.value = newOrderItems
    }

    fun removeOrderItem(orderItem: OrderItem) {
        _orderItems.value = _orderItems.value?.dropWhile { it.id == orderItem.id }
    }

    fun newOrder(campaignId: String, order: Order) {
        viewModelScope.launch {
            orderRepository.newOrder(campaignId, order).collect {
                _resource.value = it
            }
        }
    }

}