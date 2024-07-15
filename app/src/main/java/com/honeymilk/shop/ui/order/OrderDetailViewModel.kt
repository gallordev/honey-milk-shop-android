package com.honeymilk.shop.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.repository.OrderRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _order = MutableLiveData<Resource<Order?>>(null)

    val order: LiveData<Resource<Order?>>
        get() = _order

    fun getOrder(campaignId: String, orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrder(campaignId, orderId).collect {
                _order.value = it
            }
        }
    }

}