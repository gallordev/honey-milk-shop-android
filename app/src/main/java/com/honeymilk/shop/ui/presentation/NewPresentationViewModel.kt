package com.honeymilk.shop.ui.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Presentation
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPresentationViewModel @Inject constructor(
    private val designRepository: DesignRepository
): ViewModel() {
    private val _resource = MutableLiveData<Resource<String>>()
    val resource: LiveData<Resource<String>>
        get() = _resource
    fun addDesignPresentations(designId: String, presentations: List<Presentation>) {
        viewModelScope.launch {
            designRepository.addDesignPresentations(designId, presentations).collect {
                _resource.postValue(it)
            }
        }
    }
}