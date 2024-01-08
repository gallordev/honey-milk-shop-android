package com.honeymilk.shop.ui.auth.sign_up

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentSignUpBinding
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.result.observe(viewLifecycleOwner) {
            val result = it ?: return@observe
            when(result) {
                is Result.Error -> {
                    binding.btnSignUp.isEnabled = true
                    Snackbar.make(binding.root, "Error: ${result.message}", Snackbar.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.btnSignUp.isEnabled = false
                }
                is Result.Success -> {
                    binding.btnSignUp.isEnabled = true
                    findNavController().popBackStack(R.id.homeFragment, true)
                }
            }
        }

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