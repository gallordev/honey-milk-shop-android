package com.honeymilk.shop.ui.order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.LayoutOrderItemItemBinding
import com.honeymilk.shop.model.OrderItem
import com.honeymilk.shop.ui.common.DataBoundListAdapter
import com.honeymilk.shop.utils.Extensions.toCurrencyFormat

class OrderItemAdapter : DataBoundListAdapter<OrderItem, LayoutOrderItemItemBinding>(OrderItemDiffCallback()) {
    override fun createBinding(parent: ViewGroup): LayoutOrderItemItemBinding = DataBindingUtil
        .inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_order_item_item,
            parent,
            false
        )

    @SuppressLint("SetTextI18n")
    override fun bind(binding: LayoutOrderItemItemBinding, item: OrderItem) {
        with(binding) {
            orderItem = item
            txtComment.isGone = item.comment.isEmpty()
            txtDesignPrice.text = "${item.getTypePrice().toCurrencyFormat()} x ${item.quantity}"
            txtDesignTotal.text = item.getItemTotal().toCurrencyFormat()
        }
    }

}
