package com.honeymilk.shop.ui.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.model.Preferences
import com.honeymilk.shop.model.enum.PreferencesType
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val designRepository: DesignRepository
) : ViewModel() {

    init {
        getPreferences()
    }

    private val _preferences = MutableLiveData<Resource<Preferences>>()
    val preferences: LiveData<Resource<Preferences>>
        get() = _preferences


    fun updatePreferences(value: String, type: PreferencesType, remove: Boolean = false) {
        val preferences = _preferences.value?.data ?: Preferences()
        when (type) {
            PreferencesType.COLOR -> {
                if (remove) preferences.colorList.remove(value)
                else preferences.colorList.add(value)
            }
            PreferencesType.SIZE -> {
                if (remove) preferences.sizeList.remove(value)
                else preferences.sizeList.add(value)
            }
            PreferencesType.TYPE -> {
                if (remove) preferences.typeList.remove(value)
                else preferences.typeList.add(value)
            }
        }
        viewModelScope.launch {
            designRepository.setPreferences(preferences).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _preferences.value = Resource.Success(preferences)
                    }
                    is Resource.Error -> {
                        _preferences.value = Resource.Error(result.message ?: "")
                    }
                    is Resource.Loading -> {
                        _preferences.value = Resource.Loading()
                    }
                }
            }
        }
    }

    fun getPreferences() {
        viewModelScope.launch {
            designRepository.getPreferences().collect {
                _preferences.value = it
            }
        }
    }

}