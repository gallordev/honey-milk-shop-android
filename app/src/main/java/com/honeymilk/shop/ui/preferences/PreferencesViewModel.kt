package com.honeymilk.shop.ui.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.model.Preferences
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

    var updatedPreferences = Preferences()

    private val _preferences = MutableLiveData<Resource<Preferences>>()
    val preferences: LiveData<Resource<Preferences>>
        get() = _preferences

    fun setPreferences() {
        viewModelScope.launch {
//            designRepository.setPreferences().collect {
//
//            }
        }
    }

    private fun getPreferences() {
        viewModelScope.launch {
            designRepository.getPreferences().collect {
                _preferences.value = it
            }
        }
    }


    fun setNewUpdatedPreferences(preferences: Preferences) {
        updatedPreferences = preferences
    }

}