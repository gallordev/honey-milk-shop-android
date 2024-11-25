package com.honeymilk.shop.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.repository.AuthRepository
import com.honeymilk.shop.repository.OrderRepository
import com.honeymilk.shop.utils.FirebaseKeys.CAMPAIGNS_COLLECTION
import com.honeymilk.shop.utils.FirebaseKeys.ORDERS_COLLECTION
import com.honeymilk.shop.utils.FirebaseKeys.USERS_COLLECTION
import com.honeymilk.shop.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val auth: AuthRepository,
    private val firestore: FirebaseFirestore
): OrderRepository {

    private val campaignsCollection
        get() = firestore.collection(USERS_COLLECTION)
            .document(auth.currentUserId)
            .collection(CAMPAIGNS_COLLECTION)

    override suspend fun getOrder(campaignId: String, orderId: String): Flow<Resource<Order?>> = flow {
        emit(Resource.Loading())
        val data: Order? = campaignsCollection
            .document(campaignId)
            .collection(ORDERS_COLLECTION)
            .document(orderId)
            .get()
            .await()
            .toObject()
        emit(Resource.Success(data))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun getOrders(campaignId: String): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        val data: List<Order> = campaignsCollection
            .document(campaignId)
            .collection(ORDERS_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects()
        emit(Resource.Success(data))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun newOrder(campaignId: String, order: Order): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val data = campaignsCollection
            .document(campaignId)
            .collection(ORDERS_COLLECTION)
            .add(order)
            .await()
            .id
        emit(Resource.Success(data))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun updateOrder(campaignId: String, order: Order): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            campaignsCollection
                .document(campaignId)
                .collection(ORDERS_COLLECTION)
                .document(order.id)
                .set(order)
                .await()
            emit(Resource.Success(order.id))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override suspend fun deleteOrder(campaignId: String, orderId: String): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            campaignsCollection
                .document(campaignId)
                .collection(ORDERS_COLLECTION)
                .document(orderId)
                .delete()
                .await()
            emit(Resource.Success(orderId))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)
}