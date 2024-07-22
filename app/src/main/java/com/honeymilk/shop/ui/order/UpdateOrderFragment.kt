package com.honeymilk.shop.ui.order

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentOrderFormBinding
import com.honeymilk.shop.databinding.LayoutOrderItemFormBinding
import com.honeymilk.shop.model.Customer
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.model.OrderItem
import com.honeymilk.shop.ui.design.DesignListDialogFragment
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import com.honeymilk.shop.utils.hide
import com.honeymilk.shop.utils.setText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateOrderFragment :
    BaseFragment<FragmentOrderFormBinding>(FragmentOrderFormBinding::inflate) {

    private val args: UpdateOrderFragmentArgs by navArgs()
    private val orderDetailViewModel: OrderDetailViewModel by viewModels()
    private val updateOrderViewModel: UpdateOrderViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dialog = DesignListDialogFragment(updateOrderViewModel::addOrderItems)
        orderDetailViewModel.getOrder(args.campaignId, args.orderId)
        orderDetailViewModel.order.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            if (resource is Resource.Success) {
                resource.data?.let { order ->
                    loadForm(order)
                    updateOrderViewModel.setOrderItems(order.items)
                }
            }
        }
        updateOrderViewModel.orderItems.observe(viewLifecycleOwner) {
            val orderItems = it ?: return@observe
            buildOrderItems(orderItems)
        }
        binding.btnAddOrderItem.setOnClickListener {
            dialog.show(childFragmentManager, dialog.tag)
        }
        binding.btnSave.setOnClickListener {
            updateOrderViewModel.updateOrder(args.campaignId, buildOrder())
        }
        updateOrderViewModel.resource.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when (resource) {
                is Resource.Loading -> {
                    handleLoadingState(isLoading = true)
                }

                is Resource.Error -> {
                    handleLoadingState(isLoading = false)
                    showErrorMessage(resource.message)
                }

                is Resource.Success -> {
                    handleLoadingState(isLoading = false)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun buildOrderItems(orderItems: List<OrderItem>) {
        orderItems.forEach { item ->
            if (!binding.layoutOrderItems.children.any { it.id == item.id }) {
                buildOrderItem(item)
            }
        }
    }

    private fun loadForm(order: Order) {
        with(binding) {
            textFieldName.setText(order.customer.name)
            textFieldEmail.setText(order.customer.email)
            textFieldPhoneNumber.setText(order.customer.phoneNumber)
            textFieldAddress.setText(order.customer.address)
            textFieldShippingCompany.setText(order.shippingCompany)
            textFieldShippingPrice.setText(order.shippingPrice)
            switchShippingPaid.isChecked
            textFieldTrackingCode.setText(order.trackingCode)
            textFieldExtras.setText(order.extras)
            textFieldExtrasTotal.setText(order.extrasTotal)
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
                setText(orderItem.type, false)
                setOnItemClickListener { _, _, position, _ ->
                    orderItem.type = typeItems[position]
                }
            }

            (menuColor.editText as? MaterialAutoCompleteTextView)?.apply {
                setText(orderItem.color, false)
                setOnItemClickListener { _, view, _, _ ->
                    orderItem.color = (view as TextView).text.toString()
                }
            }

            (menuSize.editText as? MaterialAutoCompleteTextView)?.apply {
                setText(orderItem.size, false)
                setOnItemClickListener { _, view, _, _ ->
                    orderItem.size = (view as TextView).text.toString()
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
                println("root.id -> ${root.id}, item.id -> ${orderItem.id}")
                binding.layoutOrderItems.apply {
                    val view = children.find { it.id == orderItem.id }
                    removeView(view)
                }
                updateOrderViewModel.removeOrderItem(orderItem)
            }
        }
    }

    private fun buildOrder(): Order {
        with(binding) {
            return Order(
                id = args.orderId,
                customer = Customer(
                    textFieldName.getText(),
                    textFieldEmail.getText(),
                    textFieldAddress.getText(),
                    textFieldPhoneNumber.getText()
                ),
                items = updateOrderViewModel.orderItems.value ?: emptyList(),
                extras = textFieldExtras.getText(),
                extrasTotal = textFieldExtrasTotal.getText().toFloat(),
                shippingCompany = textFieldShippingCompany.getText(),
                shippingPrice = textFieldShippingPrice.getText().toFloat(),
                isShippingPaid = switchShippingPaid.isChecked,
                trackingCode = textFieldTrackingCode.getText()
            )
        }
    }

    private fun handleLoadingState(isLoading: Boolean) {
        binding.btnSave.hide(isLoading)
        binding.progressIndicator.hide(!isLoading)
    }

}