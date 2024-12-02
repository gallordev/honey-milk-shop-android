package com.honeymilk.shop.ui.design

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.honeymilk.shop.R
import com.honeymilk.shop.model.Design
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DesignListDialogFragment(
    private val callBack: ((List<Design>) -> Unit)? = null
): DialogFragment() {
    private lateinit var adapter: DesignsAdapter
    private val viewModel: DesignListViewModel by viewModels()
    private lateinit var selectedDesigns: List<Design>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selectedDesigns = emptyList()
        val builder = MaterialAlertDialogBuilder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_design_list, null)
        builder.setTitle(getString(R.string.title_select_order_products))
        builder.setView(view)
        val tempSelectedDesigns = mutableListOf<Design>()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        adapter = DesignsAdapter(clickCallback = { d: Design, v: MaterialCardView ->
            v.isChecked = !v.isChecked
            if (v.isChecked) {
                tempSelectedDesigns.add(d)
            } else {
                tempSelectedDesigns.remove(d)
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        viewModel.filteredDesigns.observe(requireActivity()) {
            val designs = it ?: return@observe
            adapter.submitList(designs)
        }

        val searchField = view.findViewById<TextInputLayout>(R.id.textField_search)
        searchField.editText?.addTextChangedListener {
            viewModel.searchQuery.value = it.toString()
        }

        builder.setPositiveButton(getString(R.string.btn_add)) { _, _ ->
            selectedDesigns = tempSelectedDesigns
            callBack?.invoke(selectedDesigns)
        }
        builder.setNegativeButton(getString(R.string.btn_cancel)) { dialog: DialogInterface, _: Int ->
            selectedDesigns = emptyList()
            dialog.dismiss()
        }
        return builder.create()
    }

}