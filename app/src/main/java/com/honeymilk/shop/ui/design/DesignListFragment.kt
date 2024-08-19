package com.honeymilk.shop.ui.design

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        setupMenu()
        val dialog = MaterialAlertDialogBuilder(requireActivity())

        adapter = DesignsAdapter { d: Design, _ ->
            dialog.apply {
                val body = d.presentations.joinToString("\n") { "${it.name}: ${it.price}" }
                setTitle(d.name + " • " + d.group)
                setMessage(body)
                setPositiveButton(getString(R.string.btn_ok)) { dialog, _ -> dialog.dismiss() }
            }.show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        viewModel.designs.observe(viewLifecycleOwner) {
            val designs = it ?: return@observe
            Timber.d("Designs -> $designs")
            adapter.submitList(designs)
        }
    }

    fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_design_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_design -> {
                        findNavController().navigate(
                            DesignListFragmentDirections.actionDesignListFragmentToNewDesignFragment()
                        )
                        true
                    }
                    else -> false
                }
            }

        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}