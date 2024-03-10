package com.honeymilk.shop.ui.design

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.card.MaterialCardView
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.LayoutDesignItemBinding
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.ui.common.DataBoundListAdapter
import com.honeymilk.shop.ui.common.DataBoundViewHolder
import java.util.concurrent.Executor

class DesignsAdapter(
    private val clickCallback: ((Design, MaterialCardView) -> Unit)?
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
    }
}
