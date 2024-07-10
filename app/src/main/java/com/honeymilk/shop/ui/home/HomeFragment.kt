package com.honeymilk.shop.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentHomeBinding
import com.honeymilk.shop.model.Presentation
import com.honeymilk.shop.ui.AuthStatusViewModel
import com.honeymilk.shop.ui.auth.login.LoginFragment
import com.honeymilk.shop.ui.design.DesignListDialogFragment
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: AuthStatusViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentUser.observe(viewLifecycleOwner) {
            val user = it ?: return@observe
            if (user.id.isBlank()) {
                findNavController().navigate(R.id.loginFragment)
            }
        }

        with(binding) {
            btnNewCampaign.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_newCampaignFragment)
            }
            btnNewDesign.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_newDesignFragment)
            }
            btnDesignsList.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_designListFragment)
            }
            btnDesignsListDialog.setOnClickListener {
                val dialog = DesignListDialogFragment()

                val transaction = childFragmentManager.beginTransaction()

                dialog.show(transaction, dialog.tag)

            }
            btnNewOrder.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_newOrderFragment)
            }
            btnCampaignList.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_campaignListFragment)
            }
        }
    }

}