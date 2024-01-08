package com.honeymilk.shop.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.model.User
import com.honeymilk.shop.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthStatusViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser = authRepository.currentUser.asLiveData(Dispatchers.Main)
    val currentUser: LiveData<User>
        get() = _currentUser

}