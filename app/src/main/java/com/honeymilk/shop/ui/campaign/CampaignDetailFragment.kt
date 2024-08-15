package com.honeymilk.shop.ui.campaign

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.honeymilk.shop.databinding.FragmentCampaignDetailBinding
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.isGone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampaignDetailFragment : BaseFragment<FragmentCampaignDetailBinding>(
    FragmentCampaignDetailBinding::inflate
) {

    private val args: CampaignDetailFragmentArgs by navArgs()
    private lateinit var campaignSummaryAdapter: CampaignSummaryAdapter
    private val campaignDetailViewModel: CampaignDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        campaignDetailViewModel.getCampaign(args.campaignId)

        campaignSummaryAdapter = CampaignSummaryAdapter(childFragmentManager, lifecycle)

        binding.viewPagerSummary.adapter = campaignSummaryAdapter

        TabLayoutMediator(
            binding.csTabLayout,
            binding.viewPagerSummary
        ) { tab, position -> }.attach()

        campaignDetailViewModel.campaign.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when (resource) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    with(binding) {
                        Glide.with(requireContext())
                            .load(resource.data?.imageURL)
                            .into(binding.campaignImage)
                        campaignName.text = resource.data?.name
                        campaignDescription.text = resource.data?.description
                        campaignDescription.isGone(resource.data?.description.isNullOrEmpty())
                    }
                }

                is Resource.Error -> {
                    showErrorMessage(resource.message)
                }
            }
        }
    }

}