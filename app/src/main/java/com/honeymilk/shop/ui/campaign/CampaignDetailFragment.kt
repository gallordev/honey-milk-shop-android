package com.honeymilk.shop.ui.campaign

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentCampaignDetailBinding
import com.honeymilk.shop.ui.order.OrderExportHelper
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
    private lateinit var orderExportHelper: OrderExportHelper
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    campaignDetailViewModel.campaignOrders.value?.let {
                        val orders = it.data ?: emptyList()
                        orderExportHelper.createExcelFile(uri, orders)
                    }
                }

            }
        }
        orderExportHelper = OrderExportHelper(requireActivity())
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.campaign_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.export_orders -> {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/vnd.ms-excel"
                            putExtra(Intent.EXTRA_TITLE, "orders.xls")
                        }
                        launcher.launch(intent)
                        true
                    }
                    else -> false
                }
            }

        }

        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

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