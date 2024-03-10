package com.honeymilk.shop.repository.impl

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.repository.AuthRepository
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.utils.FirebaseKeys.CREATED_AT_FIELD
import com.honeymilk.shop.utils.FirebaseKeys.DESIGNS_COLLECTION
import com.honeymilk.shop.utils.FirebaseKeys.USERS_COLLECTION
import com.honeymilk.shop.utils.ImageCompressorHelper
import com.honeymilk.shop.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class DesignRepositoryImpl @Inject constructor(
    private val auth: AuthRepository,
    private val firestore: FirebaseFirestore,
    private val imageCompressorHelper: ImageCompressorHelper,
    private val storage: FirebaseStorage
) : DesignRepository {

    private val collection
        get() = firestore.collection(USERS_COLLECTION)
            .document(auth.currentUserId)
            .collection(DESIGNS_COLLECTION)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val designs: Flow<List<Design>>
        get() = auth.currentUser.flatMapLatest { user ->
            firestore.collection(USERS_COLLECTION)
                .document(user.id)
                .collection(DESIGNS_COLLECTION)
                .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
                .dataObjects()
        }

    override suspend fun getDesign(designId: String): Flow<Resource<Design?>>  = flow {
        emit(Resource.Loading())
        val data: Design? = collection
            .document(designId).get().await()
            .toObject()
        emit(Resource.Success(data))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun newDesign(design: Design): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val imageUri = Uri.parse(design.imageURL)
        val compressedImageByteArray = imageCompressorHelper.compressImage(imageUri)
        val imageUrl = submitDesignImage(compressedImageByteArray)
        val updatedDesign = design.copy(userId = auth.currentUserId, imageURL = imageUrl)
        val data: String = collection.add(updatedDesign).await().id
        emit(Resource.Success(data))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun updateDesign(design: Design): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        collection.document(design.id).set(design).await()
        emit(Resource.Success(design.id))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteDesign(designId: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        collection.document(designId).delete()
        emit(Resource.Success(designId))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    private suspend fun submitDesignImage(imageByteArray: ByteArray): String {
        val ref = storage.reference.child("$DESIGNS_COLLECTION/${UUID.randomUUID()}")
        ref.putBytes(imageByteArray).await()
        val wea: Uri = ref.downloadUrl.await()
        return wea.toString()
    }

}