package com.honeymilk.shop.ui.campaign

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentCampaignListBinding
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampaignListFragment :
    BaseFragment<FragmentCampaignListBinding>(FragmentCampaignListBinding::inflate) {

    private lateinit var adapter: CampaignListAdapter
    private val campaignListViewModel: CampaignListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_campaign_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_campaign -> {
                        findNavController().navigate(
                            CampaignListFragmentDirections.actionCampaignListFragmentToNewCampaignFragment()
                        )
                        true
                    }
                    else -> false
                }
            }

        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
        adapter = CampaignListAdapter {
            findNavController().navigate(
                CampaignListFragmentDirections.actionCampaignListFragmentToCampaignDetailFragment(
                    it.id
                )
            )
        }
        binding.recyclerViewCampaignList.adapter = adapter
        campaignListViewModel.campaigns.observe(viewLifecycleOwner) {
            val campaignList = it ?: return@observe
            adapter.submitList(campaignList)
        }
    }

}