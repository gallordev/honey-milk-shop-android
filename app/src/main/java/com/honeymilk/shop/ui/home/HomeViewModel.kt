package com.honeymilk.shop.ui.home

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
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _lastCampaign = MutableLiveData<Resource<Campaign?>>(null)
    val lastCampaign: LiveData<Resource<Campaign?>>
        get() = _lastCampaign

    fun loadLastCampaign() {
        viewModelScope.launch {
            campaignRepository.getLastCampaign().collect {
                _lastCampaign.value = it
            }
        }
    }

}