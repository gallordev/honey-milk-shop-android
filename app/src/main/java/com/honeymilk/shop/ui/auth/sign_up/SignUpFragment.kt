package com.honeymilk.shop.ui.auth.sign_up

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentSignUpBinding
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.resource.observe(viewLifecycleOwner) {
            val result = it ?: return@observe
            when(result) {
                is Resource.Error -> {
                    binding.btnSignUp.isEnabled = true
                    Snackbar.make(binding.root, "Error: ${result.message}", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.btnSignUp.isEnabled = false
                }
                is Resource.Success -> {
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