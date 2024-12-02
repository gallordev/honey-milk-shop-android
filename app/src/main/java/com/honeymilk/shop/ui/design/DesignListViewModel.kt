package com.honeymilk.shop.ui.design

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DesignListViewModel @Inject constructor(
    private val designRepository: DesignRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val _designs = MutableLiveData<List<Design>>(emptyList())
    val designs: LiveData<List<Design>>
        get() = _designs

    val filteredDesigns: LiveData<List<Design>> =
        searchQuery.combine(designs.asFlow()) { query, designsResource ->
            designsResource.filter { design ->
                design.name.contains(query, ignoreCase = true)
                        || design.group.contains(query, ignoreCase = true)
            }
        }.asLiveData()

    init {
        loadDesigns()
    }

    private fun loadDesigns() {
        viewModelScope.launch {
            designRepository.designs.collect {
                _designs.value = it
            }
        }
    }

    fun deleteDesign(design: Design) {
        viewModelScope.launch {
            designRepository.deleteDesign(design.id).collect {

                loadDesigns()
            }
        }
    }

}