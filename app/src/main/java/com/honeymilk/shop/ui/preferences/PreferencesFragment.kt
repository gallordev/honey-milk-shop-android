package com.honeymilk.shop.ui.preferences

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.View
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentPreferencesBinding
import com.honeymilk.shop.model.Preferences
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import com.honeymilk.shop.utils.setText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencesFragment : BaseFragment<FragmentPreferencesBinding>(FragmentPreferencesBinding::inflate) {

    private val preferencesViewModel: PreferencesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesViewModel.preferences.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let { preferences ->
                        buildChips(preferences)
                        preferencesViewModel.setNewUpdatedPreferences(preferences.copy())
                    }
                }
                is Resource.Error -> {
                    showErrorMessage(resource.message ?: "")
                }
                is Resource.Loading -> {
                }
            }
        }

        buildNewColorDialog()

    }

    private fun buildNewColorDialog() {
        val newColorTextField = layoutInflater.inflate(
            R.layout.layout_new_color, null, false
        ) as TextInputLayout

        val newColorDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_add_new_color))
            .setView(newColorTextField)
            .setPositiveButton(getString(R.string.btn_save)) { dialog, _ ->

                val newColor = newColorTextField.getText()

                preferencesViewModel.updatedPreferences.preferences["colorList"]?.add(
                    newColor
                )

                val colorChip = buildColorChip(newColor)

                binding.colorsChipGroup.addView(colorChip)

                newColorTextField.setText("")

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
            }
            .create()

        binding.btnAddNewColor.setOnClickListener {
            newColorDialog.show()
        }
    }

    private fun buildColorChip(color: String): Chip {
        val chip = layoutInflater.inflate(
            R.layout.layout_chip,
            binding.colorsChipGroup,
            false
        ) as Chip

        with(chip) {
            id = View.generateViewId()
            text = color
            isCheckable = false
            isFocusable = false
            isClickable = false
            setOnCloseIconClickListener {
                binding.colorsChipGroup.removeView(chip)
            }
        }

        return chip
    }

    private fun buildChips(preferences: Preferences) {
        val colorList: List<String> = preferences.preferences["colorList"] ?: emptyList()
        for (color in colorList) {
            val chip = buildColorChip(color)
            binding.colorsChipGroup.addView(chip)
        }
    }

}