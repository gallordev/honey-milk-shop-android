package com.honeymilk.shop.ui.order

import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.honeymilk.shop.R
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.ui.design.DesignListViewModel
import com.honeymilk.shop.ui.design.DesignsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrderItemDialogFragment : DialogFragment() {

    private lateinit var adapter: DesignsAdapter
    private val viewModel: DesignListViewModel by viewModels()
    private var currentStep = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_design_list, null)
        builder.setTitle(getString(R.string.title_select_order_products))
        builder.setView(view)
            .setPositiveButton(getString(R.string.btn_next)) { _, _ -> handleNextStep() }
            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ -> dialog.dismiss() }
        adapter = DesignsAdapter(clickCallback = { d: Design, _: MaterialCardView ->

        })
        view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NewOrderItemDialogFragment.adapter
        }
        viewModel.filteredDesigns.observe(requireActivity()) {
            val designs = it ?: return@observe
            adapter.submitList(designs)
        }
        val searchField = view.findViewById<TextInputLayout>(R.id.textField_search)
        searchField.editText?.addTextChangedListener {
            viewModel.searchQuery.value = it.toString()
        }

        return builder.create()
    }

    private fun handleNextStep() {
//        when (currentStep) {
//            1 -> {
//                currentStep++
//                updateContent(R.layout.dialog_wizard_step2)
//            }
//            2 -> {
//                currentStep++
//                updateContent(R.layout.dialog_wizard_step3)
//            }
//            else -> dismiss()
//        }
    }


}