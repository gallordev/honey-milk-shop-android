package com.honeymilk.shop.ui.order

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentOrderFormBinding
import com.honeymilk.shop.databinding.LayoutOrderItemFormBinding
import com.honeymilk.shop.model.Customer
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.model.OrderItem
import com.honeymilk.shop.ui.design.DesignListDialogFragment
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.getText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrderFragment : BaseFragment<FragmentOrderFormBinding>(FragmentOrderFormBinding::inflate) {

    private val newOrderViewModel: NewOrderViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = DesignListDialogFragment(newOrderViewModel::addOrderItems)
        newOrderViewModel.orderItems.observe(viewLifecycleOwner) {
            val orderItems = it ?: return@observe
            println(orderItems)
            buildOrderItems(orderItems)
        }
        binding.btnAddOrderItem.setOnClickListener {
            dialog.show(childFragmentManager, dialog.tag)
        }
        binding.btnSave.setOnClickListener {
            println(buildOrder())
        }
    }

    private fun buildOrder(): Order {
        with(binding) {
            return Order(
                customer = Customer(
                    textFieldName.getText(),
                    textFieldEmail.getText(),
                    textFieldAddress.getText(),
                    textFieldPhoneNumber.getText()
                ),
                items = newOrderViewModel.orderItems.value ?: emptyList(),
                extras = textFieldExtras.getText(),
                shippingCompany = textFieldShippingCompany.getText(),
                shippingPrice = textFieldShippingPrice.getText().toFloat(),
                isShippingPaid = switchShippingPaid.isChecked,
                trackingCode = textFieldTrackingCode.getText()
            )
        }
    }

    private fun buildOrderItems(orderItems: List<OrderItem>) {
        orderItems.forEach { item ->
            if (!binding.layoutOrderItems.children.any { it.id == item.id }) {
                buildOrderItem(item)
            }
        }
    }

    private fun buildOrderItem(orderItem: OrderItem) {
        DataBindingUtil.inflate<LayoutOrderItemFormBinding>(
            layoutInflater,
            R.layout.layout_order_item_form,
            binding.layoutOrderItems,
            true
        ).apply {
            root.id = orderItem.id
            this.orderItem = orderItem
            val typeItems: Array<String> = orderItem.design.presentations
                .map { it.name }.toTypedArray()
            (menuType.editText as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(typeItems)
                setOnItemClickListener { _, _, position, _ ->
                    orderItem.type = typeItems[position]
                    println(orderItem.type)
                }
            }

            (menuType.editText as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(typeItems)
                setOnItemClickListener { _, _, position, _ ->
                    orderItem.type = typeItems[position]
                    println(orderItem.type)
                }
            }

            (menuColor.editText as? MaterialAutoCompleteTextView)?.apply {
                setOnItemClickListener { _, view, _, _ ->
                    orderItem.color = (view as TextView).text.toString()
                    println(orderItem.color)
                }
            }

            (menuSize.editText as? MaterialAutoCompleteTextView)?.apply {
                setOnItemClickListener { _, view, _, _ ->
                    orderItem.size = (view as TextView).text.toString()
                    println(orderItem.size)
                }
            }

            btnAdd.setOnClickListener {
                orderItem.increaseQuantity()
                txtQuantity.text = orderItem.quantity.toString()
            }

            btnRemove.setOnClickListener {
                orderItem.decreaseQuantity()
                txtQuantity.text = orderItem.quantity.toString()
            }

            btnDeleteItem.setOnClickListener {
                binding.layoutOrderItems.apply {
                    val view = children.find { it.id == orderItem.id }
                    removeView(view)
                }
                newOrderViewModel.removeOrderItem(orderItem)
                println(newOrderViewModel.orderItems.value)
            }
        }

    }

}
