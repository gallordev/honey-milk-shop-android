package com.honeymilk.shop.ui.campaign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignListViewModel @Inject constructor(
    private val campaignsRepository: CampaignRepository
) : ViewModel() {
    private val _campaigns = MutableLiveData<List<Campaign>>(null)
    val campaigns: LiveData<List<Campaign>>
        get() = _campaigns

    init {
        viewModelScope.launch {
            campaignsRepository.campaigns.collect {
                _campaigns.value = it
            }
        }
    }
}