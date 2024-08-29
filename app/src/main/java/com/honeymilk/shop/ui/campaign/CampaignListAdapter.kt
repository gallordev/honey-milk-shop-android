package com.honeymilk.shop.ui.campaign

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.databinding.DataBindingUtil
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.LayoutCampaignItemBinding
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.ui.common.DataBoundListAdapter

class CampaignListAdapter(
    private val onCampaignClick: ((Campaign) -> Unit)? = null,
    private val onUpdateCampaignClick: ((Campaign) -> Unit)? = null,
    private val onDeleteCampaignClick: ((Campaign) -> Unit)? = null
) : DataBoundListAdapter<Campaign, LayoutCampaignItemBinding>(
    CampaignDiffCallback()
) {
    override fun createBinding(parent: ViewGroup): LayoutCampaignItemBinding = DataBindingUtil
        .inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_campaign_item,
            parent,
            false
        )

    override fun bind(binding: LayoutCampaignItemBinding, item: Campaign) {
        binding.campaign = item
        binding.campaignCard.setOnClickListener {
            onCampaignClick?.invoke(item)
        }
        binding.btnMore.setOnClickListener {
            showMenu(it, R.menu.menu_popup_campaign)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Respond to menu item click.
            true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}