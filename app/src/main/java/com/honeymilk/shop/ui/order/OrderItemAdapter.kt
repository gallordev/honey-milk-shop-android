package com.honeymilk.shop.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.LayoutOrderItemFormBinding
import com.honeymilk.shop.model.OrderItem
import com.honeymilk.shop.ui.common.DataBoundListAdapter

class OrderItemAdapter(
    private val updateOrderItem: ((OrderItem, Boolean) -> Unit)? = null
) : DataBoundListAdapter<OrderItem, LayoutOrderItemFormBinding>(
    diffCallback = object : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem) =
            oldItem.design.id == newItem.design.id

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem) =
            oldItem.quantity == newItem.quantity
                    && oldItem.size == newItem.size
                    && oldItem.type == newItem.type
                    && oldItem.color == newItem.color
    }
) {
    override fun createBinding(parent: ViewGroup): LayoutOrderItemFormBinding = DataBindingUtil
        .inflate<LayoutOrderItemFormBinding?>(
            LayoutInflater.from(parent.context),
            R.layout.layout_order_item_form,
            parent,
            false
        ).apply {
            orderItem?.let { item ->

            }
        }

    override fun bind(binding: LayoutOrderItemFormBinding, item: OrderItem) {
        with(binding) {
            orderItem = item
            val typeItems: Array<String> = item.design.presentations
                .map { it.name }.toTypedArray()
            (menuType.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(typeItems)
            btnAdd.setOnClickListener {
                updateOrderItem?.invoke(item, true)
            }
            btnRemove.setOnClickListener {
                updateOrderItem?.invoke(item, false)
            }
        }
    }

}
