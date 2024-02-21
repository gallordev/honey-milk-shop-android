package com.honeymilk.shop.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.repository.AuthRepository
import com.honeymilk.shop.repository.CampaignRepository
import com.honeymilk.shop.utils.FirebaseKeys.CAMPAIGNS_COLLECTION
import com.honeymilk.shop.utils.FirebaseKeys.CREATED_AT_FIELD
import com.honeymilk.shop.utils.FirebaseKeys.USERS_COLLECTION
import com.honeymilk.shop.utils.FirebaseKeys.USER_ID_FIELD
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
        get() = firestore.collection(USERS_COLLECTION)
            .document(auth.currentUserId)
            .collection(CAMPAIGNS_COLLECTION)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val campaigns: Flow<List<Campaign>>
        get() = auth.currentUser.flatMapLatest { user ->
            firestore.collection(USERS_COLLECTION)
                .document(user.id)
                .collection(CAMPAIGNS_COLLECTION)
                .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
                .dataObjects()
        }

    override suspend fun getCampaign(campaignId: String): Flow<Resource<Campaign?>> = flow {
            emit(Resource.Loading())
            val data: Campaign? = collection
                .document(campaignId).get().await()
                .toObject()
            emit(Resource.Success(data))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override suspend fun newCampaign(campaign: Campaign): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val updatedCampaign = campaign.copy(userId = auth.currentUserId)
        val data: String = collection.add(updatedCampaign).await().id
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

}