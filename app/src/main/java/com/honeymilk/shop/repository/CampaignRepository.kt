package com.honeymilk.shop.repository

import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    val campaigns: Flow<List<Campaign>>
    suspend fun getCampaign(campaignId: String): Flow<Resource<Campaign?>>
    suspend fun newCampaign(campaign: Campaign): Flow<Resource<String>>
    suspend fun updateCampaign(campaign: Campaign): Flow<Resource<String>>
    suspend fun deleteCampaign(campaignId: String): Flow<Resource<String>>
}