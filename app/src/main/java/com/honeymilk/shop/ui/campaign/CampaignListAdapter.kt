package com.honeymilk.shop.ui.campaign

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.LayoutCampaignItemBinding
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.ui.common.DataBoundListAdapter

class CampaignListAdapter(
    private val onCampaignClick: ((Campaign) -> Unit)? = null
) : DataBoundListAdapter<Campaign, LayoutCampaignItemBinding>(
    CampaignDiffCallback()
) {
    override fun createBinding(parent: ViewGroup): LayoutCampaignItemBinding = DataBindingUtil
        .inflate<LayoutCampaignItemBinding?>(
            LayoutInflater.from(parent.context),
            R.layout.layout_campaign_item,
            parent,
            false
        ).apply {
            campaign?.let { campaign ->

            }
        }

    override fun bind(binding: LayoutCampaignItemBinding, item: Campaign) {
        binding.campaign = item
    }
}