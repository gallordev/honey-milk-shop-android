package com.honeymilk.shop.ui.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {


    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signUp(email, password)
        }
    }

}