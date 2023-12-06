package com.honeymilk.shop.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.User
import com.honeymilk.shop.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<User>(null)
    val currentUser: LiveData<User>
        get() = _currentUser

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect{
                _currentUser.postValue(it)
            }
        }
    }


}