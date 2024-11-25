package com.honeymilk.shop.ui.campaign

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.honeymilk.shop.databinding.FragmentRawOrderListBinding
import com.honeymilk.shop.model.Order
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class RawOrderListFragment : BaseFragment<FragmentRawOrderListBinding>(
    FragmentRawOrderListBinding::inflate
) {

    private val campaignDetailViewModel: CampaignDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        campaignDetailViewModel.campaignOrders.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            resource.data?.let { orderList ->
                val markdownText = getOrderListString(orderList)
                val markwon = Markwon.create(requireContext())
                markwon.setMarkdown(binding.rawOrders, markdownText)
            }
        }

        binding.btnClipboard.setOnClickListener {
            copyTextViewTextToClipboard(binding.rawOrders)
        }

    }

    private fun getOrderListString(orders: List<Order>): String {
        val result = StringBuilder()
        result.append("# Lista de Ordenes").append("\n")
        orders.forEachIndexed { index, order ->
            result.append("### Orden ${index + 1}\n")
            result.append("**Cliente:** ${order.customer.name}\n\n")
            result.append("**Usuario de Instagram:** ${order.customer.instagramUsername}\n\n")
            result.append("**Correo:** ${order.customer.email}\n\n")
            result.append("**Teléfono:** \n${order.customer.phoneNumber}\n\n")
            result.append("**Dirección:** ${order.customer.address}\n\n")
            result.append("**Productos:**\n\n")
            order.items.forEach { item ->
                val comment = if (item.comment.isNotBlank()) "(${item.comment})" else ""
                result.append("- ${item.design.name} ${item.type} ${item.color} ${item.size} Qty: ${item.quantity} $comment\n\n")
            }
            result.append("\n\n")
            if (order.extras.isNotEmpty()) {
                result.append("**Extras:** ${order.extras}\n\n")
            }
            if (order.extrasTotal > 0) {
                result.append("**Extras Total:** $${order.extrasTotal}\n\n")
            }
            if (order.shippingCompany.isNotEmpty()) {
                result.append("**Compañia de envío:** ${order.shippingCompany}\n\n")
            }
            if (order.shippingPrice > 0) {
                result.append("**Precio de envío:** $${order.shippingPrice}\n\n")
            }
            if (order.trackingCode.isNotBlank()) {
                result.append("**Código de seguimiento:** ${order.trackingCode}\n\n")
            }
            result.append("---").append("\n\n")
        }
        return result.toString()
    }

    private fun copyTextViewTextToClipboard(textView: TextView) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", textView.text)
        clipboard.setPrimaryClip(clip)
    }

}