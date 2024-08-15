package com.honeymilk.shop.ui.campaign

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.honeymilk.shop.ui.order.OrderListFragment

class CampaignSummaryAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> OrderListFragment()
        1 -> CampaignSummaryFragment()
        else -> throw IllegalStateException("Invalid position: $position")
    }

}
