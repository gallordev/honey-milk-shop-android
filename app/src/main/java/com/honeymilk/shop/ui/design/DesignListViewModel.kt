package com.honeymilk.shop.ui.design

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DesignListViewModel @Inject constructor(
    private val designsRepository: DesignRepository
) : ViewModel() {

    private val _designs = MutableLiveData<List<Design>>(null)
    val designs: LiveData<List<Design>>
        get() = _designs

    init {
        viewModelScope.launch {
            designsRepository.designs.collect {
                _designs.value = it
            }
        }
    }

}