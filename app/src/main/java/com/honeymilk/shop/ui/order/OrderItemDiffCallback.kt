package com.honeymilk.shop.ui.order

import androidx.recyclerview.widget.DiffUtil
import com.honeymilk.shop.model.OrderItem

class OrderItemDiffCallback: DiffUtil.ItemCallback<OrderItem>() {
    override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem) =
        oldItem.design.id == newItem.design.id

    override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem) =
        oldItem.quantity === newItem.quantity

}