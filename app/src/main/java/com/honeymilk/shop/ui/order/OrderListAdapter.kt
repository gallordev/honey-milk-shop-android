package com.honeymilk.shop.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.LayoutItemOrderBinding
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.ui.common.DataBoundListAdapter

class OrderListAdapter(
    private val onCampaignClick: ((Order) -> Unit)? = null
) : DataBoundListAdapter<Order, LayoutItemOrderBinding>(
    OrderDiffCallback()
) {
    override fun createBinding(parent: ViewGroup): LayoutItemOrderBinding = DataBindingUtil
        .inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_order,
            parent,
            false
        )

    override fun bind(binding: LayoutItemOrderBinding, item: Order) {
        binding.order = item
        binding.orderCard.setOnClickListener {
            onCampaignClick?.invoke(item)
        }
    }

}