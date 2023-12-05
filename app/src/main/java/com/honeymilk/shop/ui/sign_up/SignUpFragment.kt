package com.honeymilk.shop.ui.sign_up

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentSignUpBinding
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnSignUp.setOnClickListener {
                signUp(
                    textFieldEmail.editText!!.text.toString(),
                    textFieldPassword.editText!!.text.toString()
                )
            }
        }
    }

    private fun signUp(email: String, password: String) {
        viewModel.signUp(email, password)
    }

}