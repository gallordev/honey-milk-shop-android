package com.honeymilk.shop.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentHomeBinding
import com.honeymilk.shop.ui.AuthStatusViewModel
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.isGone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val authStatusViewModel: AuthStatusViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authStatusViewModel.currentUser.observe(viewLifecycleOwner) {
            val user = it ?: return@observe
            if (user.id.isBlank()) {
                findNavController().navigate(R.id.loginFragment)
            } else {
                binding.homeView.isGone(false)
//                homeViewModel.loadLastCampaign()
            }
        }

//        homeViewModel.lastCampaign.observe(viewLifecycleOwner) {
//            val resource = it ?: return@observe
//            when(resource) {
//                is Resource.Loading -> {
//
//                }
//                is Resource.Success -> {
//                    resource.data?.let { campaign ->
//                        binding.campaignName.text = campaign.name
//                        Glide.with(requireContext())
//                            .load(campaign.imageURL)
//                            .into(binding.campaignImage)
//                    }
//
//                }
//                is Resource.Error -> {
//                    showErrorMessage(resource.message)
//                }
//            }
//        }

        setupMenu()

        with(binding) {

        }
    }

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        authStatusViewModel.logout()
                        true
                    }
                    else -> false
                }
            }

        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}