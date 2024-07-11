package com.honeymilk.shop.ui.campaign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.repository.CampaignRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignDetailViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _campaign = MutableLiveData<Resource<Campaign?>>(null)
    val campaign: LiveData<Resource<Campaign?>>
        get() = _campaign

    fun getCampaign(campaignId: String) {
        viewModelScope.launch {
            campaignRepository.getCampaign(campaignId).collect {
                _campaign.value = it
            }
        }
    }

}