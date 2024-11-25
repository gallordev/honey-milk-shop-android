package com.honeymilk.shop.ui.campaign

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentCampaignDetailBinding
import com.honeymilk.shop.ui.order.OrderExportHelper
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.PreferencesHelper
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
                        lifecycleScope.launch {
                            orderExportHelper.exportOrdersToExcel(orders, uri, requireContext()).onSuccess {
                                PreferencesHelper.incrementCounter(requireContext())
                                Snackbar.make(
                                    requireActivity().findViewById(android.R.id.content),
                                    "Campaign Orders Exported Correctly",
                                    Snackbar.LENGTH_LONG
                                ).setAction("Open") {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        startActivity(Intent.createChooser(intent, "Open with"))
                                }.show()
                            }.onFailure {
                                Toast.makeText(requireContext(), "Error Exporting Campaign Orders", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            }
        }
        orderExportHelper = OrderExportHelper()
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_campaign_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.export_orders -> {

                        val currentCounter = PreferencesHelper.getCounter(requireContext())

                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                            putExtra(Intent.EXTRA_TITLE, "${campaignDetailViewModel.getCampaignName()}_$currentCounter.xlsx")
                        }
                        launcher.launch(intent)
                        true
                    }
                    R.id.new_order -> {
                        findNavController().navigate(
                            CampaignDetailFragmentDirections.actionCampaignDetailFragmentToNewOrderFragment(
                                args.campaignId
                            )
                        )
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
                    }
                }

                is Resource.Error -> {
                    showErrorMessage(resource.message)
                }
            }
        }
    }

}