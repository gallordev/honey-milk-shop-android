package com.honeymilk.shop.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.google.firebase.firestore.firestore

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun firestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun auth(): FirebaseAuth = Firebase.auth

}