package com.honeymilk.shop.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.repository.AuthRepository
import com.honeymilk.shop.repository.CampaignRepository
import com.honeymilk.shop.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AuthRepository
) : CampaignRepository {

    private val collection
        get() = firestore.collection(CAMPAIGN_COLLECTION)
            .whereEqualTo(USER_ID_FIELD, auth.currentUserId)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val campaigns: Flow<List<Campaign>>
        get() = auth.currentUser.flatMapLatest { user ->
            firestore.collection(CAMPAIGN_COLLECTION)
                .whereEqualTo(USER_ID_FIELD, user.id)
                .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
                .dataObjects()
        }

    override suspend fun getCampaign(campaignId: String): Flow<Resource<Campaign?>> = flow {
            emit(Resource.Loading())
            val data: Campaign? = firestore.collection(CAMPAIGN_COLLECTION)
                .document(campaignId).get().await()
                .toObject()
            emit(Resource.Success(data))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override suspend fun newCampaign(campaign: Campaign): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val updatedCampaign = campaign.copy(userId = auth.currentUserId)
        val data: String = firestore.collection(CAMPAIGN_COLLECTION).add(updatedCampaign).await().id
        emit(Resource.Success(data))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun updateCampaign(campaign: Campaign): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCampaign(campaignId: String): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val CAMPAIGN_COLLECTION = "campaigns"

        private const val USER_ID_FIELD = "userId"
        private const val CREATED_AT_FIELD = "createdAt"
    }

}