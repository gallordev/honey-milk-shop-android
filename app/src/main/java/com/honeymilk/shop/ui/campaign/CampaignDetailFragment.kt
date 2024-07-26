package com.honeymilk.shop.ui.campaign

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.honeymilk.shop.databinding.FragmentCampaignDetailBinding
import com.honeymilk.shop.ui.order.OrderListAdapter
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampaignDetailFragment :
    BaseFragment<FragmentCampaignDetailBinding>(FragmentCampaignDetailBinding::inflate) {

    private val args: CampaignDetailFragmentArgs by navArgs()
    private lateinit var orderListAdapter: OrderListAdapter
    private lateinit var campaignSummaryAdapter: CampaignSummaryAdapter
    private val campaignDetailViewModel: CampaignDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        campaignDetailViewModel.getCampaign(args.campaignId)
        orderListAdapter = OrderListAdapter(
            onOrderClick = {
                findNavController().navigate(
                    CampaignDetailFragmentDirections.actionCampaignDetailFragmentToOrderDetailFragment(
                        args.campaignId,
                        it.id
                    )
                )
            },
            onEditClick = {
                findNavController().navigate(
                    CampaignDetailFragmentDirections.actionCampaignDetailFragmentToUpdateOrderFragment(
                        args.campaignId,
                        it.id
                    )
                )
            }
        )
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        campaignSummaryAdapter = CampaignSummaryAdapter(requireContext()) { data ->
            val clip = ClipData.newPlainText("text", data)
            clipboard.setPrimaryClip(clip)
        }
        binding.viewPagerSummary.adapter = campaignSummaryAdapter
        TabLayoutMediator(
            binding.intoTabLayout,
            binding.viewPagerSummary
        ) { tab, position -> }.attach()

        binding.recyclerViewOrderList.adapter = orderListAdapter
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
                resource.data?.let { orderList ->
                    orderListAdapter.submitList(orderList)
                    campaignSummaryAdapter.setData(orderList)
                }
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