package com.honeymilk.shop.ui.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.honeymilk.shop.databinding.FragmentOrderFormBinding
import com.honeymilk.shop.ui.design.DesignListDialogFragment
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrderFragment : BaseFragment<FragmentOrderFormBinding>(FragmentOrderFormBinding::inflate) {

    private lateinit var adapter: OrderItemAdapter
    private val newOrderViewModel: NewOrderViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = DesignListDialogFragment(newOrderViewModel::addOrderItems)
        adapter = OrderItemAdapter(newOrderViewModel::updateOrderItem)
        adapter.submitList(emptyList())
        binding.recyclerViewOrderItems.adapter = adapter
        newOrderViewModel.orderItems.observe(viewLifecycleOwner){
            val orderItems = it ?: return@observe
            println(orderItems)
            adapter.submitList(orderItems)
        }
        binding.btnAddOrderItem.setOnClickListener {
            dialog.show(childFragmentManager, dialog.tag)
        }




    }




}