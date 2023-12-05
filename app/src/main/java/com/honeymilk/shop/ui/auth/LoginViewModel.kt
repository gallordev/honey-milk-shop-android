package com.honeymilk.shop.ui.auth

import androidx.lifecycle.ViewModel
import com.honeymilk.shop.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

}