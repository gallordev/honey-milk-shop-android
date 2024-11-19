package com.honeymilk.shop.ui.campaign

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import com.honeymilk.shop.databinding.FragmentCampaignSummaryBinding
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class CampaignSummaryFragment : BaseFragment<FragmentCampaignSummaryBinding>(
    FragmentCampaignSummaryBinding::inflate
) {

    private val campaignDetailViewModel: CampaignDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            campaignDetailViewModel.campaignOrders.observe(viewLifecycleOwner) {
                val resource = it ?: return@observe
                resource.data?.let { orderList ->
                    campaignSummary.text = getCampaignRawItemsSummary(orderList)
                    campaignSummaryExtras.text = getExtrasSummary(orderList)
                }
            }

            btnClipboardItemsByColor.setOnClickListener {
                copyTextViewTextToClipboard(campaignSummary)
            }

            btnClipboardExtras.setOnClickListener {
                copyTextViewTextToClipboard(campaignSummaryExtras)
            }
        }
    }

    private fun copyTextViewTextToClipboard(textView: TextView) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", textView.text)
        clipboard.setPrimaryClip(clip)
    }

    private fun getCampaignRawItemsSummary(orderList: List<Order>) : String {
        val groupedItems = orderList.flatMap { it.items }
            .groupBy { it.type }
            .mapValues { (type, itemsOfType) ->
                itemsOfType.groupBy { it.color }
                    .mapValues { (color, itemsOfColor) ->
                        itemsOfColor.groupBy { it.size }
                            .mapValues { (size, itemsOfSize) ->
                                itemsOfSize.sumOf { it.quantity }
                            }
                    }
            }

        val result = StringBuilder()
        result.append("=".repeat(30)).append("\n")
        groupedItems.forEach { (type, colors) ->
            result.append("Tipo: $type \n")
            colors.forEach { (color, sizes) ->
                result.append("    Color: $color \n")
                sizes.forEach { (size, quantity)->
                    result.append("        Talla: $size Cantidad: $quantity \n")
                }
            }
            result.append("=".repeat(30)).append("\n")
        }

        return result.toString()
    }

    private fun getExtrasSummary(orderList: List<Order>): String {
        return orderList.filter { it.extras.isNotEmpty() }
            .joinToString("\n") { it.extras }
    }

}