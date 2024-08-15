package com.honeymilk.shop.ui.campaign

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.honeymilk.shop.databinding.FragmentCampaignSummaryBinding
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampaignSummaryFragment : BaseFragment<FragmentCampaignSummaryBinding>(
    FragmentCampaignSummaryBinding::inflate
) {

    private val campaignDetailViewModel: CampaignDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        campaignDetailViewModel.campaignOrders.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            resource.data?.let { orderList ->
                binding.campaignSummary.text = getCampaignRawItemsSummary(orderList)
                binding.campaignSummaryExtras.text = getExtrasSummary(orderList)
                binding.campaignSummaryByGroup.text = getItemsByDesign(orderList)
            }
        }
    }

    private fun getCampaignRawItemsSummary(orderList: List<Order>) : String {
        val itemsMap = orderList.flatMap { it.items }
            .groupBy { it.color }
            .mapValues {
                it.value.groupBy { orderItem -> orderItem.size }
                    .mapValues { entry ->
                        entry.value.sumOf { orderItem -> orderItem.quantity }
                    }
            }

        val summary = itemsMap.map { (color, sizes) ->
            "$color: ${sizes.map { (size, quantity) -> "$size: $quantity" }.joinToString(", ")}"
        }.joinToString("\n")

        return summary
    }

    private fun getExtrasSummary(orderList: List<Order>): String {
        return orderList.filter { it.extras.isNotEmpty() }
            .joinToString("\n") { it.extras }
    }

    private fun getItemsByDesign(orders: List<Order>): String {
        val designMap = mutableMapOf<String, MutableMap<String, MutableMap<String, Int>>>()

        for(order in orders) {
            for (item in order.items) {
                val designName = item.design.name
                val color = item.color
                val size = item.size
                val quantity = item.quantity

                designMap.getOrPut(designName) { mutableMapOf() }
                    .getOrPut(color) { mutableMapOf() }
                    .merge(size, quantity) { old, new -> old + new }
            }
        }

        val summary = StringBuilder()
        for ((designName, colorMap) in designMap){
            summary.append("$designName:\n")
            for ((color, sizeMap) in colorMap) {
                summary.append("  $color:\n")
                for ((size, quantity) in sizeMap) {
                    summary.append("    $size: $quantity\n")
                }
            }
        }

        return summary.toString()
    }


}