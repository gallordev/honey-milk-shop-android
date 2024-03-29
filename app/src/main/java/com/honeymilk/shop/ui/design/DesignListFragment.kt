package com.honeymilk.shop.ui.design

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.card.MaterialCardView
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentDesignListBinding
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DesignListFragment : BaseFragment<FragmentDesignListBinding>(FragmentDesignListBinding::inflate) {

    private lateinit var adapter: DesignsAdapter
    private val viewModel: DesignListViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val designs = mutableListOf<Design>()
        adapter = DesignsAdapter { d: Design, v: MaterialCardView ->
            v.isChecked = !v.isChecked
            if (v.isChecked) {
                designs.add(d)
            } else {
                designs.remove(d)
            }
            println(designs)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding.recyclerView.adapter = adapter
        viewModel.designs.observe(viewLifecycleOwner) {
            val designs = it ?: return@observe
            Timber.d("Designs -> $designs")
            adapter.submitList(designs)
        }
    }

}