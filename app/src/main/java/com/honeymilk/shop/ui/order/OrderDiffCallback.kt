package com.honeymilk.shop.ui.order

import androidx.recyclerview.widget.DiffUtil
import com.honeymilk.shop.model.Order

class OrderDiffCallback: DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean =
        oldItem.customer == newItem.customer
}