package com.honeymilk.shop.ui.design

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.card.MaterialCardView
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.LayoutDesignItemBinding
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.ui.common.DataBoundListAdapter

class DesignsAdapter(
    private val showMenu: Boolean = false,
    private val clickCallback: ((Design, MaterialCardView) -> Unit)?,
    private val onUpdateDesignClick: ((Design) -> Unit)? = null,
    private val onDeleteDesignClick: ((Design) -> Unit)? = null
) : DataBoundListAdapter<Design, LayoutDesignItemBinding>(
    diffCallback = object : DiffUtil.ItemCallback<Design>() {
        override fun areItemsTheSame(oldItem: Design, newItem: Design) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Design, newItem: Design) =
            oldItem.name == newItem.name && oldItem.imageURL == newItem.imageURL
    }
) {

    override fun createBinding(parent: ViewGroup): LayoutDesignItemBinding = DataBindingUtil
        .inflate<LayoutDesignItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_design_item,
            parent,
            false
        ).apply {
            root.setOnClickListener {
                design?.let { design -> clickCallback?.invoke(design, designCard) }
            }
        }

    override fun bind(binding: LayoutDesignItemBinding, item: Design) {
        binding.design = item
        binding.btnMore.isGone = !showMenu
        binding.btnMore.setOnClickListener {
            showMenu(it, R.menu.menu_popup_design, item)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, item: Design) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.update_design -> onUpdateDesignClick?.invoke(item)
                R.id.delete_design -> onDeleteDesignClick?.invoke(item)
            }
            true
        }
        popup.show()
    }

}
