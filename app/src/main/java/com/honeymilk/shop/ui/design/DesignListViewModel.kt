package com.honeymilk.shop.ui.design

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.repository.DesignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DesignListViewModel @Inject constructor(
    private val designRepository: DesignRepository
) : ViewModel() {

    private val _designs = MutableLiveData<List<Design>>(null)
    val designs: LiveData<List<Design>>
        get() = _designs

    init {
        viewModelScope.launch {
            designRepository.designs.collect {
                _designs.value = it
            }
        }
    }

}