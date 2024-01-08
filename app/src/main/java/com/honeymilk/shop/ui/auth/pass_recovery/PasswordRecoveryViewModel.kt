package com.honeymilk.shop.ui.auth.pass_recovery

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
class PasswordRecoveryViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _result = MutableLiveData<Result<String>>()
    val result: LiveData<Result<String>>
        get() = _result

    fun sendPasswordRecoveryEmail(email: String) {
        viewModelScope.launch {
            authRepository.sendRecoveryEmail(email).collect {
                _result.value = it
            }
        }
    }


}