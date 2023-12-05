package com.honeymilk.shop.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.honeymilk.shop.model.User
import com.honeymilk.shop.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
): AuthRepository {

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

    override suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    override suspend fun logout() {
        auth.signOut()
    }


}