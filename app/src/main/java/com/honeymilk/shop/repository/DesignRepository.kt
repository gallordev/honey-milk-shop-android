package com.honeymilk.shop.repository

import com.honeymilk.shop.model.Design
import com.honeymilk.shop.model.Presentation
import com.honeymilk.shop.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DesignRepository {
    val designs: Flow<List<Design>>
    suspend fun getDesign(designId: String): Flow<Resource<Design?>>
    suspend fun newDesign(design: Design): Flow<Resource<String>>
    suspend fun updateDesign(design: Design): Flow<Resource<String>>
    suspend fun deleteDesign(designId: String): Flow<Resource<String>>
    suspend fun addDesignPresentations(designId: String, presentations: List<Presentation>): Flow<Resource<String>>
}