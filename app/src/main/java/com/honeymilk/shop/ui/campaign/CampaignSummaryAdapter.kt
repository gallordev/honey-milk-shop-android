package com.honeymilk.shop.ui.campaign

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.honeymilk.shop.databinding.LayoutCampaignSummaryBinding
import com.honeymilk.shop.model.Order

class CampaignSummaryAdapter(
    private val context: Context,
    private val onClickCallback: ((String) -> Unit)? = null
) : RecyclerView.Adapter<CampaignSummaryAdapter.ViewHolder>() {

    private var orderList: List<Order> = emptyList()
    inner class ViewHolder(val binding: LayoutCampaignSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutCampaignSummaryBinding.inflate(LayoutInflater.from(context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.apply {
                when(position) {
                    0 -> {
                        campaignSummary.text = getCampaignRawItemsSummary(orderList)
                        campaignSummaryExtras.text = getExtrasSummary(orderList)
                        btnClipboard.setOnClickListener {
                            onClickCallback?.invoke("xsxsxs")
                        }
                    }
                    1 -> {

                    }
                    2 -> {

                    }
                }
            }
        }
    }

    override fun getItemCount() = 3

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Order>) {
        orderList = data
        notifyDataSetChanged()
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

    private fun getEarningsSummary(orderList: List<Order>) {
//        val total =
    }

}