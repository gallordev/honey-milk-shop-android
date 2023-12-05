package com.honeymilk.shop.repository

import com.honeymilk.shop.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUserId: String
    val hasUser: Boolean
    val currentUser: Flow<User>

    suspend fun signUp(email: String, password: String)
    suspend fun authenticate(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun deleteAccount()
    suspend fun logout()

}