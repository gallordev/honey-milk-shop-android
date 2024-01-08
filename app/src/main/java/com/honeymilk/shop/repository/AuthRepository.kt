package com.honeymilk.shop.repository

import com.honeymilk.shop.model.User
import com.honeymilk.shop.utils.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUserId: String
    val hasUser: Boolean
    val currentUser: Flow<User>

    suspend fun signUp(email: String, password: String): Flow<Result<String>>
    suspend fun signIn(email: String, password: String): Flow<Result<String>>
    suspend fun sendRecoveryEmail(email: String): Flow<Result<String>>
    suspend fun deleteAccount()
    suspend fun logout()

}