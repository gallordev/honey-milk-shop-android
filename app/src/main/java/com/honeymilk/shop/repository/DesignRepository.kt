package com.honeymilk.shop.repository

import com.honeymilk.shop.model.Design
import com.honeymilk.shop.model.Preferences
import com.honeymilk.shop.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.util.HashMap

interface DesignRepository {
    val designs: Flow<List<Design>>
    suspend fun getDesign(designId: String): Flow<Resource<Design?>>
    suspend fun newDesign(design: Design): Flow<Resource<String>>
    suspend fun updateDesign(design: Design, updateImage: Boolean): Flow<Resource<String>>
    suspend fun deleteDesign(designId: String): Flow<Resource<String>>
    suspend fun setPreferences(preferences: Preferences) : Flow<Resource<String>>
    suspend fun getPreferences() : Flow<Resource<Preferences>>
}