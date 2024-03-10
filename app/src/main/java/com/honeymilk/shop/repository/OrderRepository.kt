package com.honeymilk.shop.repository

import com.honeymilk.shop.model.Order
import com.honeymilk.shop.utils.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getOrder(campaignId: String, orderId: String): Flow<Resource<Order?>>
    suspend fun getOrders(campaignId: String): Flow<Resource<List<Order>>>
    suspend fun newOrder(campaignId: String, order: Order): Flow<Resource<String>>
    suspend fun updateOrder(campaignId: String, order: Order): Flow<Resource<String>>
    suspend fun deleteOrder(campaignId: String, orderId: String): Flow<Resource<String>>
}
