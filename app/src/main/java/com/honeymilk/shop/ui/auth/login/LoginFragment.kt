package com.honeymilk.shop.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentLoginBinding
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.resource.observe(viewLifecycleOwner) {
            val result = it ?: return@observe
            when(result) {
                is Resource.Error -> {
                    binding.btnLogin.isEnabled = true
                    showErrorMessage(result.message ?: "")
                }
                is Resource.Loading -> {
                    binding.btnLogin.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnLogin.isEnabled = true
                }
            }
        }

        with(binding) {
            btnLogin.setOnClickListener {
                viewModel.login(
                    textFieldEmail.editText!!.text.toString(),
                    textFieldPassword.editText!!.text.toString()
                )
            }
            btnPasswordRecovery.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_passwordRecoveryFragment)
            }
            btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

        }

    }

}