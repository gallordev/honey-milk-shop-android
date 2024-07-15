package com.honeymilk.shop.ui.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentOrderDetailBinding
import com.honeymilk.shop.utils.Extensions.toCurrencyFormat
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {

    private val args: OrderDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var adapter: OrderItemAdapter
    private val orderDetailViewModel: OrderDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_order_detail,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        orderDetailViewModel.getOrder(args.campaignId, args.orderId)
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            adapter = OrderItemAdapter()
            recyclerViewOrderItems.adapter = adapter
            orderDetailViewModel.order.observe(viewLifecycleOwner) {
                val resource = it ?: return@observe
                if (resource is Resource.Success) {
                    resource.data?.let { order ->
                        this.order = order
                        adapter.submitList(order.items)
                        txtShipmentPrice.text = order.shippingPrice.toCurrencyFormat()
                        txtOrderItemsTotal.text = order.getOrderItemsTotal().toCurrencyFormat()
                        txtOrderExtrasTotal.text = order.extrasTotal.toCurrencyFormat()
                        txtOrderTotal.text = order.getTotal().toCurrencyFormat()
                    }
                }
            }
        }
    }

}