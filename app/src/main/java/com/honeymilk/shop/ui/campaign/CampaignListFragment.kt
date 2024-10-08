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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        setupMenu()

        val dialog = MaterialAlertDialogBuilder(requireActivity())

        adapter = CampaignListAdapter(
            onCampaignClick = {
                findNavController().navigate(
                    CampaignListFragmentDirections.actionCampaignListFragmentToCampaignDetailFragment(
                        it.id
                    )
                )
            },
            onUpdateCampaignClick = {
                findNavController().navigate(
                    CampaignListFragmentDirections.actionCampaignListFragmentToUpdateCampaignFragment(
                        it.id
                    )
                )
            },
            onDeleteCampaignClick = { campaign ->
                dialog.apply {
                    setTitle(getString(R.string.title_delete_campaign, campaign.name))
                    setMessage(getString(R.string.message_delete_campaign))
                    setPositiveButton(getString(R.string.btn_yes)) { dialog, _ ->
                        dialog.dismiss()
                        campaignListViewModel.deleteCampaign(campaign.id)
                    }
                    setNegativeButton(getString(R.string.btn_no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
        )
        binding.recyclerViewCampaignList.adapter = adapter
        campaignListViewModel.campaigns.observe(viewLifecycleOwner) {
            val campaignList = it ?: return@observe
            adapter.submitList(campaignList)
        }
    }

    private fun setupMenu() {
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
    }

}