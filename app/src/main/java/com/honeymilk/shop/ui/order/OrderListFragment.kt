package com.honeymilk.shop.ui.order

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.honeymilk.shop.databinding.FragmentOrderListBinding
import com.honeymilk.shop.ui.campaign.CampaignDetailFragmentDirections
import com.honeymilk.shop.ui.campaign.CampaignDetailViewModel
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderListFragment : BaseFragment<FragmentOrderListBinding>(FragmentOrderListBinding::inflate) {

    private lateinit var orderListAdapter: OrderListAdapter
    private val campaignDetailViewModel: CampaignDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        orderListAdapter = OrderListAdapter(
            onOrderClick = {
                findNavController().navigate(
                    CampaignDetailFragmentDirections.actionCampaignDetailFragmentToOrderDetailFragment(
                        campaignDetailViewModel.campaign.value?.data?.id ?: "",
                        it.id
                    )
                )
            },
            onEditClick = {
                findNavController().navigate(
                    CampaignDetailFragmentDirections.actionCampaignDetailFragmentToUpdateOrderFragment(
                        campaignDetailViewModel.campaign.value?.data?.id ?: "",
                        it.id
                    )
                )
            },
            onDeleteClick = {
                campaignDetailViewModel.deleteOrder(campaignDetailViewModel.campaign.value?.data?.id ?: "", it)
            }
        )
        binding.recyclerViewOrderList.adapter = orderListAdapter
        campaignDetailViewModel.campaignOrders.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when(resource) {
                is Resource.Success -> {
                    orderListAdapter.submitList(resource.data)
                    println(resource.data)
                }
                else -> {}
            }
        }
        campaignDetailViewModel.result.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when(resource) {
                is Resource.Success -> {
//                    campaignDetailViewModel.getCampaign(campaignDetailViewModel.campaign.value?.data?.id ?: "")
                    Toast.makeText(requireContext(), "Order Deleted Successfully", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

}