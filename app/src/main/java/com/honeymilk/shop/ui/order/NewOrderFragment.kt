package com.honeymilk.shop.ui.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.children
import androidx.core.view.isGone
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
import com.honeymilk.shop.ui.preferences.PreferencesViewModel
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import com.honeymilk.shop.utils.hide
import com.honeymilk.shop.utils.setText
import com.honeymilk.shop.utils.toValidFloat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrderFragment : BaseFragment<FragmentOrderFormBinding>(FragmentOrderFormBinding::inflate) {

    private val args: NewOrderFragmentArgs by navArgs()
    private val newOrderViewModel: NewOrderViewModel by viewModels()
    private val preferencesViewModel: PreferencesViewModel by viewModels()
    private lateinit var launcher: ActivityResultLauncher<ScanOptions>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launcher = registerForActivityResult(
           ScanContract()
        ) { result  ->
            if(result.contents == null) {
                Toast.makeText(requireContext(),
                    getString(R.string.txt_cancelled), Toast.LENGTH_LONG).show()
            } else {
                binding.textFieldTrackingCode.setText(result.contents)
            }
        }
        val dialog = DesignListDialogFragment(newOrderViewModel::addOrderItems)
        newOrderViewModel.orderItems.observe(viewLifecycleOwner) {
            val orderItems = it ?: return@observe
            println(orderItems)
            buildOrderItems(orderItems)
        }

        preferencesViewModel.preferences.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when(resource) {
                is Resource.Error -> {
                    handleLoadingState(isLoading = false)
                    showErrorMessage(resource.message ?: "Unknown Error")
                }
                is Resource.Loading -> {
                    handleLoadingState(isLoading = true)
                }
                is Resource.Success -> {
                    handleLoadingState(isLoading = false)
                }
            }
        }

        newOrderViewModel.resource.observe(viewLifecycleOwner) {
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
        binding.textFieldTrackingCode.setEndIconOnClickListener {
            launcher.launch(ScanOptions())
        }
        binding.btnAddOrderItem.setOnClickListener {
            dialog.show(childFragmentManager, dialog.tag)
        }
        binding.btnSave.setOnClickListener {
            val order = buildOrder()
            newOrderViewModel.newOrder(args.campaignId, order)
        }
    }

    private fun buildOrder(): Order {
        with(binding) {
            return Order(
                customer = Customer(
                    textFieldName.getText(),
                    textFieldInstagramUser.getText(),
                    textFieldEmail.getText(),
                    textFieldAddress.getText(),
                    textFieldPhoneNumber.getText()
                ),
                items = newOrderViewModel.orderItems.value ?: emptyList(),
                extras = textFieldExtras.getText(),
                extrasTotal = textFieldExtrasTotal.getText().toValidFloat(),
                shippingCompany = textFieldShippingCompany.getText(),
                shippingPrice = textFieldShippingPrice.getText().toValidFloat(),
                shippingPaid = switchShippingPaid.isChecked,
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

            btnAddComment.setOnClickListener {
                textFieldComment.isGone = !textFieldComment.isGone
            }

            textFieldComment.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(s: Editable?) {
                    val comment = s.toString()
                    orderItem.comment = comment
                }
            })

            (menuType.editText as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(typeItems)
                setOnItemClickListener { _, _, position, _ ->
                    orderItem.type = typeItems[position]
                }
            }

            val colorItems: Array<String> = preferencesViewModel
                .preferences.value?.data?.colorList?.toTypedArray() ?: emptyArray()

            (menuColor.editText as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(colorItems)
                setOnItemClickListener { _, view, _, _ ->
                    orderItem.color = (view as TextView).text.toString()
                }
            }

            val sizeItems: Array<String> = preferencesViewModel
                .preferences.value?.data?.sizeList?.toTypedArray() ?: emptyArray()

            (menuSize.editText as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(sizeItems)
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
                binding.layoutOrderItems.apply {
                    val view = children.find { it.id == orderItem.id }
                    removeView(view)
                }
                newOrderViewModel.removeOrderItem(orderItem)
                println(newOrderViewModel.orderItems.value)
            }
        }
    }

    private fun handleLoadingState(isLoading: Boolean) {
        binding.btnSave.hide(isLoading)
        binding.progressIndicator.hide(!isLoading)
        binding.btnAddOrderItem.isEnabled = !isLoading
    }

}
