package com.honeymilk.shop.ui.order

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.honeymilk.shop.R
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
                campaignDetailViewModel.searchQuery.value = ""
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
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(getString(R.string.title_delete_order))
                    setMessage(getString(R.string.body_delete_order, it.customer.name))
                    setPositiveButton(getString(R.string.btn_yes)) { _, _ ->
                        campaignDetailViewModel.deleteOrder(campaignDetailViewModel.campaign.value?.data?.id ?: "", it.id)
                    }
                    setNegativeButton(getString(R.string.btn_no)) { dialog, _ -> dialog.dismiss() }
                    show()
                }
            }
        )

        binding.textFieldSearch.editText?.addTextChangedListener {
            campaignDetailViewModel.searchQuery.value = it.toString()
        }

        binding.recyclerViewOrderList.adapter = orderListAdapter
        campaignDetailViewModel.filteredCampaignOrders.observe(viewLifecycleOwner) {
            val orderList = it ?: return@observe
            orderListAdapter.submitList(orderList)
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