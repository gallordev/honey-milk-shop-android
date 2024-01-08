package com.honeymilk.shop.repository.impl

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.honeymilk.shop.model.User
import com.honeymilk.shop.repository.AuthRepository
import com.honeymilk.shop.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid, it.email ?: "") } ?: User())
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun signUp(email: String, password: String): Flow<Result<String>> = flow {
        emit(Result.Loading())
        val authResult: AuthResult = auth.createUserWithEmailAndPassword(email, password).await()
        emit(Result.Success(authResult.user?.uid.toString()))
    }.catch {
        emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun signIn(email: String, password: String): Flow<Result<String>> = flow {
        emit(Result.Loading())
        val authResult: AuthResult = auth.signInWithEmailAndPassword(email, password).await()
        emit(Result.Success(authResult.user?.uid.toString()))
    }.catch {
        emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun sendRecoveryEmail(email: String): Flow<Result<String>> = flow {
        emit(Result.Loading())
        auth.sendPasswordResetEmail(email).await()
        emit(Result.Success(email))
    }.catch {
        emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    override suspend fun logout() {
        auth.signOut()
    }


}