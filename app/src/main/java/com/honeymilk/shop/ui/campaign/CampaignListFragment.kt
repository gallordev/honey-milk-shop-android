package com.honeymilk.shop.ui.campaign

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.honeymilk.shop.databinding.FragmentCampaignListBinding
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampaignListFragment : BaseFragment<FragmentCampaignListBinding>(FragmentCampaignListBinding::inflate) {

    private lateinit var adapter: CampaignListAdapter
    private val campaignListViewModel: CampaignListViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CampaignListAdapter()
        binding.recyclerViewCampaignList.adapter = adapter
        campaignListViewModel.campaigns.observe(viewLifecycleOwner) {
            val campaignList = it ?: return@observe
            adapter.submitList(campaignList)
        }

    }

}