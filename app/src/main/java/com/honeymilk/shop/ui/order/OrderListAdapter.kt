package com.honeymilk.shop.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.LayoutItemOrderBinding
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.ui.common.DataBoundListAdapter
import com.honeymilk.shop.utils.Extensions.toCurrencyFormat

class OrderListAdapter(
    private val onOrderClick: ((Order) -> Unit)? = null,
    private val onEditClick: ((Order) -> Unit)? = null,
    private val onDeleteClick: ((Order) -> Unit)? = null,
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
        with(binding) {
            order = item
            orderTotal.text = item.getTotal().toCurrencyFormat()
            orderCard.setOnClickListener {
                onOrderClick?.invoke(item)
            }
            orderCard.setOnLongClickListener {
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
                true
            }
            btnEdit.setOnClickListener {
                onEditClick?.invoke(item)
            }
            btnDelete.setOnClickListener {
                onDeleteClick?.invoke(item)
            }
        }
    }

}