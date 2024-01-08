package com.honeymilk.shop.ui.auth.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeymilk.shop.repository.AuthRepository
import com.honeymilk.shop.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _result = MutableLiveData<Result<String>>()
    val result: LiveData<Result<String>>
        get() = _result

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signUp(email, password).collect {
                _result.postValue(it)
            }
        }
    }

}