package com.honeymilk.shop.ui.design

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewDesignViewModel @Inject constructor(
    private val designRepository: DesignRepository
): ViewModel() {
    private val _resource = MutableLiveData<Resource<String>>()
    val resource: LiveData<Resource<String>>
        get() = _resource
    fun newDesign(design: Design) {
        viewModelScope.launch {
            designRepository.newDesign(design).collect {
                _resource.postValue(it)
            }
        }
    }
}