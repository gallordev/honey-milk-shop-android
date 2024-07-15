package com.honeymilk.shop.ui.campaign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.repository.CampaignRepository
import com.honeymilk.shop.repository.OrderRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignDetailViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _campaign = MutableLiveData<Resource<Campaign?>>(null)
    val campaign: LiveData<Resource<Campaign?>>
        get() = _campaign

    private val _campaignOrders = MutableLiveData<Resource<List<Order>>>(null)
    val campaignOrders: LiveData<Resource<List<Order>>>
        get() = _campaignOrders

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

}