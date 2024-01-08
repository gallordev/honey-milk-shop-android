package com.honeymilk.shop.ui.auth.pass_recovery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentPasswordRecoveryBinding
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordRecoveryFragment : BaseFragment<FragmentPasswordRecoveryBinding>(FragmentPasswordRecoveryBinding::inflate) {

    private val viewModel: PasswordRecoveryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.result.observe(viewLifecycleOwner) {
            val result = it ?: return@observe
            when(result) {
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {/* empty */ }
                is Result.Success -> {
                    binding.btnPasswordRecovery.isEnabled = true
                    Toast.makeText(requireContext(), "An email has been sent to ${result.data}", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
            }
        }

        with(binding) {
            btnPasswordRecovery.setOnClickListener {
                it.isEnabled = false
                viewModel.sendPasswordRecoveryEmail(
                    textFieldEmail.editText!!.text.toString()
                )
            }
        }

    }

}