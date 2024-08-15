package com.honeymilk.shop.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentHomeBinding
import com.honeymilk.shop.model.Presentation
import com.honeymilk.shop.ui.AuthStatusViewModel
import com.honeymilk.shop.ui.auth.login.LoginFragment
import com.honeymilk.shop.ui.design.DesignListDialogFragment
import com.honeymilk.shop.ui.design.DesignListFragmentDirections
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.isGone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: AuthStatusViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        viewModel.logout()
                        true
                    }
                    else -> false
                }
            }

        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.currentUser.observe(viewLifecycleOwner) {
            val user = it ?: return@observe
            if (user.id.isBlank()) {
                findNavController().navigate(R.id.loginFragment)
            } else {
                binding.homeView.isGone(false)
            }
        }

        with(binding) {
            btnNewCampaign.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_newCampaignFragment)
            }
            btnDesignsList.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_designListFragment)
            }

            btnCampaignList.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_campaignListFragment)
            }
        }
    }

}