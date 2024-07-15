package com.honeymilk.shop.ui.campaign

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.honeymilk.shop.databinding.FragmentCampaignDetailBinding
import com.honeymilk.shop.ui.order.OrderListAdapter
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampaignDetailFragment :
    BaseFragment<FragmentCampaignDetailBinding>(FragmentCampaignDetailBinding::inflate) {

    private val args: CampaignDetailFragmentArgs by navArgs()
    private lateinit var adapter: OrderListAdapter
    private val campaignDetailViewModel: CampaignDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        campaignDetailViewModel.getCampaign(args.campaignId)
        adapter = OrderListAdapter {
            findNavController().navigate(
                CampaignDetailFragmentDirections.actionCampaignDetailFragmentToOrderDetailFragment(
                    args.campaignId,
                    it.id
                )
            )
        }
        binding.recyclerViewOrderList.adapter = adapter
        campaignDetailViewModel.campaign.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when (resource) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    with(binding) {
                        Glide.with(requireContext())
                            .load(resource.data?.imageURL)
                            .into(binding.imageViewCampaignImage)
                        campaignName.text = resource.data?.name
                        if (resource.data?.description != null && resource.data.description != "") {
                            campaignDescription.text = resource.data.description
                            campaignDescription.visibility = View.VISIBLE
                        }
                    }
                }

                is Resource.Error -> {
                    showErrorMessage(resource.message)
                }
            }
        }

        campaignDetailViewModel.campaignOrders.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            if (resource is Resource.Success) {
                adapter.submitList(resource.data)
            }
        }

        binding.btnNewOrder.setOnClickListener {
            findNavController().navigate(
                CampaignDetailFragmentDirections.actionCampaignDetailFragmentToNewOrderFragment(
                    args.campaignId
                )
            )
        }
    }

}