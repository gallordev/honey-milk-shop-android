package com.honeymilk.shop.ui.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentLoginBinding
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.result.observe(viewLifecycleOwner) {
            val result = it ?: return@observe
            when(result) {
                is Result.Error -> {
                    binding.btnLogin.isEnabled = true
                    Snackbar.make(binding.root, "Error: ${result.message}", Snackbar.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.btnLogin.isEnabled = false
                }
                is Result.Success -> {
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