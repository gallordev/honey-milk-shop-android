package com.honeymilk.shop.ui.campaign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.repository.CampaignRepository
import com.honeymilk.shop.repository.OrderRepository
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.toSnakeCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CampaignDetailViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _result = MutableLiveData<Resource<String>>()
    val result: LiveData<Resource<String>>
        get() = _result

    private val _campaign = MutableLiveData<Resource<Campaign?>>(null)
    val campaign: LiveData<Resource<Campaign?>>
        get() = _campaign

    private val _campaignOrders = MutableLiveData<Resource<List<Order>>>(null)
    val campaignOrders: LiveData<Resource<List<Order>>>
        get() = _campaignOrders

    val searchQuery = MutableStateFlow("")

    val filteredCampaignOrders: LiveData<List<Order>> =
        searchQuery.combine(campaignOrders.asFlow()) { query, ordersResource ->
            if (ordersResource is Resource.Success) {
                ordersResource.data?.filter { order ->
                    order.customer.name.contains(
                        query,
                        ignoreCase = true
                    ) || order.customer.email.contains(
                        query,
                        ignoreCase = true
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        }.asLiveData()

    fun getCampaign(campaignId: String) {
        viewModelScope.launch {
            campaignRepository.getCampaign(campaignId).collect {
                _campaign.value = it
            }
            orderRepository.getOrders(campaignId).collect {
                _campaignOrders.value = it
            }
        }
    }

    fun deleteOrder(campaignId: String, orderId: String) {
        viewModelScope.launch {
            orderRepository.deleteOrder(campaignId, orderId).collect {
                _result.value = it
            }
            orderRepository.getOrders(campaignId).collect {
                _campaignOrders.value = it
            }
        }
    }

    fun getCampaignName(): String {
        val campaignName = campaign.value?.data?.name
        if (campaignName.isNullOrEmpty()) {
            val dateFormat = SimpleDateFormat("MM")
            val date = Date()
            return dateFormat.format(date).toSnakeCase()
        } else {
            return campaignName.toSnakeCase()
        }
    }

}