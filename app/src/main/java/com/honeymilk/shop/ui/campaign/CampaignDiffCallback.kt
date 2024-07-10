package com.honeymilk.shop.ui.campaign

import androidx.recyclerview.widget.DiffUtil
import com.honeymilk.shop.model.Campaign

class CampaignDiffCallback: DiffUtil.ItemCallback<Campaign>() {
    override fun areItemsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
        return oldItem.name == newItem.name && oldItem.isActive == newItem.isActive
    }
}