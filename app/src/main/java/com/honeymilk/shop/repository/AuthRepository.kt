package com.honeymilk.shop.repository

import com.honeymilk.shop.model.User
import com.honeymilk.shop.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUserId: String
    val hasUser: Boolean
    val currentUser: Flow<User>

    suspend fun signUp(email: String, password: String): Flow<Resource<String>>
    suspend fun signIn(email: String, password: String): Flow<Resource<String>>
    suspend fun sendRecoveryEmail(email: String): Flow<Resource<String>>
    suspend fun registerNotificationToken()
    suspend fun deleteAccount()
    suspend fun logout()

}