package com.honeymilk.shop.ui.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentNewPresentationBinding
import com.honeymilk.shop.model.Presentation
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import com.honeymilk.shop.utils.hide
import com.honeymilk.shop.utils.isGone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPresentationFragment : BaseFragment<FragmentNewPresentationBinding>(FragmentNewPresentationBinding::inflate) {

   private var isUpdatingChildren = false
   private val args: NewPresentationFragmentArgs by navArgs()
   private lateinit var childrenCheckBoxes : List<CheckBox>
   private lateinit var childrenTextFields: List<TextInputLayout>
   private val viewModel: NewPresentationViewModel by viewModels()
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      childrenCheckBoxes = listOf(
         binding.checkboxCrewneck,
         binding.checkboxHoodie,
         binding.checkboxTShirt,
         binding.checkboxCropTop
      )

      childrenTextFields = listOf(
         binding.textFieldCrewneckPrice,
         binding.textFieldHoodiePrice,
         binding.textFieldTShirtPrice,
         binding.textFieldCropTopPrice
      )

      val parentOnCheckedStateChangedListener =
         MaterialCheckBox.OnCheckedStateChangedListener { checkBox: MaterialCheckBox, state: Int ->
            val isChecked = checkBox.isChecked
            if (state != MaterialCheckBox.STATE_INDETERMINATE) {
               isUpdatingChildren = true
               for (child in childrenCheckBoxes) {
                  child.isChecked = isChecked
               }
               for (child in childrenTextFields) {
                  child.isGone(!isChecked)
               }
               isUpdatingChildren = false
            }
         }

      // Checked state changed listener for each child
      val childOnCheckedStateChangedListener =
         MaterialCheckBox.OnCheckedStateChangedListener { _: MaterialCheckBox?, _: Int ->
            childrenCheckBoxes.forEachIndexed { index, checkBox ->
               childrenTextFields[index].isGone(!checkBox.isChecked)
            }
            if (!isUpdatingChildren) {
               setParentState(
                  binding.checkboxParent as MaterialCheckBox,
                  childrenCheckBoxes,
                  parentOnCheckedStateChangedListener
               )
            }
         }

      for (child in childrenCheckBoxes) {
         (child as MaterialCheckBox).addOnCheckedStateChangedListener(
            childOnCheckedStateChangedListener
         )
      }

      // Set first child to be checked
      binding.checkboxCrewneck.isChecked = true

      // Set parent's state
      setParentState(
         binding.checkboxParent as MaterialCheckBox,
         childrenCheckBoxes,
         parentOnCheckedStateChangedListener
      )

      binding.btnSave.setOnClickListener {
         val data = getFormData()
         if (data.isNotEmpty()) {
            viewModel.addDesignPresentations(args.designId, data)
         }
      }

      viewModel.resource.observe(viewLifecycleOwner) {
         val resource = it ?: return@observe
         when(resource) {
            is Resource.Error -> {
               showErrorMessage(resource.message ?: "Unknown Error")
            }
            is Resource.Loading -> {
               binding.btnSave.isEnabled = false
            }
            is Resource.Success -> {
               Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            }
         }
      }

   }

   private fun setParentState(
      checkBoxParent: MaterialCheckBox,
      childrenCheckBoxes: List<CheckBox>,
      parentOnCheckedStateChangedListener: MaterialCheckBox.OnCheckedStateChangedListener
   ) {
      val checkedCount = childrenCheckBoxes.stream().filter { obj: CheckBox -> obj.isChecked }
         .count()
         .toInt()
      val allChecked = checkedCount == childrenCheckBoxes.size
      val noneChecked = checkedCount == 0
      checkBoxParent.removeOnCheckedStateChangedListener(parentOnCheckedStateChangedListener)
      if (allChecked) {
         checkBoxParent.isChecked = true
      } else if (noneChecked) {
         checkBoxParent.isChecked = false
      } else {
         checkBoxParent.checkedState = MaterialCheckBox.STATE_INDETERMINATE
      }
      checkBoxParent.addOnCheckedStateChangedListener(parentOnCheckedStateChangedListener)
   }

   private fun getFormData() : List<Presentation> {
      val vaina = listOf(
         "Crewneck",
         "Hoodie",
         "T-Shirt",
         "Crop Top"
      )
      val data = mutableListOf<Presentation>()
      vaina.forEachIndexed { index, name ->
         if (childrenCheckBoxes[index].isChecked) {
            data.add(
               Presentation(name, childrenTextFields[index].getText().toFloat())
            )
         }
      }
      return data
   }

}