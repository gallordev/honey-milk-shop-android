package com.honeymilk.shop.ui.design

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateDesignViewModel @Inject constructor(
    private val designRepository: DesignRepository
): ViewModel() {

    private val _design = MutableLiveData<Resource<Design?>>(null)
    val design: LiveData<Resource<Design?>>
        get() = _design

    private val _resource = MutableLiveData<Resource<String>>(null)
    val resource: LiveData<Resource<String>>
        get() = _resource

    fun getDesign(designId: String) {
        viewModelScope.launch {
            designRepository.getDesign(designId).collect {
                _design.value = it
            }
        }
    }

    fun updateDesign(design: Design, updateImage: Boolean) {
        viewModelScope.launch {
            designRepository.updateDesign(design, updateImage).collect {
                _resource.value = it
            }
        }
    }

}