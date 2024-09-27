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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentDesignListBinding
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Extensions.toCurrencyFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DesignListFragment :
    BaseFragment<FragmentDesignListBinding>(FragmentDesignListBinding::inflate) {

    private lateinit var adapter: DesignsAdapter
    private val viewModel: DesignListViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()
        val dialog = MaterialAlertDialogBuilder(requireActivity())
        adapter = DesignsAdapter(
            showMenu = true,
            clickCallback = { design: Design, _ ->
                dialog.apply {
                    val body =
                        design.presentations.joinToString("\n") { "${it.name}: ${it.price.toCurrencyFormat()}" }
                    setTitle(design.name + " • " + design.group)
                    setMessage(body)
                    setPositiveButton(null, null)
                    setNegativeButton(getString(R.string.btn_close)) { dialog, _ -> dialog.dismiss() }
                }.show()
            },
            onUpdateDesignClick = { design ->
                findNavController().navigate(
                    DesignListFragmentDirections.actionDesignListFragmentToUpdateDesignFragment(
                        design.id
                    )
                )
            },
            onDeleteDesignClick = { design ->
                dialog.apply {
                    setTitle(
                        getString(
                            R.string.title_delete_design,
                            design.name + " • " + design.group
                        )
                    )
                    setMessage(getString(R.string.message_delete_design))
                    setPositiveButton(getString(R.string.btn_no)) { dialog, _ -> dialog.dismiss() }
                    setNegativeButton(getString(R.string.btn_yes)) { dialog, _ ->
                        viewModel.deleteDesign(design)
                        dialog.dismiss()
                    }
                }.show()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        viewModel.designs.observe(viewLifecycleOwner) {
            val designs = it ?: return@observe
            adapter.submitList(designs)
        }
    }

    private fun setupMenu() {
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