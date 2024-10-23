package com.honeymilk.shop.ui.preferences

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentPreferencesBinding
import com.honeymilk.shop.model.Preferences
import com.honeymilk.shop.model.enum.PreferencesType
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
                    handleLoadingState(false)
                    resource.data?.let { preferences ->
                        binding.colorsChipGroup.removeAllViews()
                        binding.typesChipGroup.removeAllViews()
                        binding.sizesChipGroup.removeAllViews()
                        buildChips(preferences)
                    }
                }
                is Resource.Error -> {
                    showErrorMessage(resource.message ?: "")
                    handleLoadingState(false)
                }
                is Resource.Loading -> {
                    handleLoadingState(true)
                }
            }
        }

        val newColorDialog = createDialog(PreferencesType.COLOR) { newColor ->
            preferencesViewModel.updatePreferences(newColor, PreferencesType.COLOR)
        }

        val newSizeDialog = createDialog(PreferencesType.SIZE) { newSize ->
            preferencesViewModel.updatePreferences(newSize, PreferencesType.SIZE)
        }

        val newTypeDialog = createDialog(PreferencesType.TYPE) { newType ->
            preferencesViewModel.updatePreferences(newType, PreferencesType.TYPE)
        }


        binding.btnAddNewColor.setOnClickListener {
            newColorDialog.show()
        }

        binding.btnAddNewSize.setOnClickListener {
            newSizeDialog.show()
        }

        binding.btnAddNewType.setOnClickListener {
            newTypeDialog.show()
        }
    }

    private fun createDialog(type: PreferencesType, action: (String) -> Unit) : AlertDialog  {
        val title = when(type) {
            PreferencesType.COLOR -> getString(R.string.title_add_new_color)
            PreferencesType.SIZE -> getString(R.string.title_add_new_size)
            PreferencesType.TYPE -> getString(R.string.title_add_new_type)
        }

        val textField = layoutInflater.inflate(
            R.layout.layout_text_field, null, false
        ) as TextInputLayout

        return  MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(textField)
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setPositiveButton(getString(R.string.btn_save)) { dialog, _ ->
                val value = textField.getText()
                if (value.isNotBlank()) {
                    action(value)
                }
                textField.setText("")
                dialog.dismiss()
            }
            .create()
    }


    private fun buildChips(
        list: List<String>,
        viewGroup: ViewGroup,
        onCloseAction: (String) -> Unit
    ): List<Chip> {
        return list.map { value ->
            (layoutInflater.inflate(
                R.layout.layout_chip,
                viewGroup,
                false
            ) as Chip).apply {
                id = View.generateViewId()
                text = value
                isCheckable = false
                isFocusable = false
                isClickable = false
                setOnCloseIconClickListener {
                    onCloseAction(value)
                }
            }
        }
    }

    private fun buildChips(preferences: Preferences) {
        val colorChips = buildChips(preferences.colorList.distinct(), binding.colorsChipGroup) {
            preferencesViewModel.updatePreferences(it, PreferencesType.COLOR, true)
        }
        for (colorChip in colorChips) {
            binding.colorsChipGroup.addView(colorChip)
        }
        val typeChips = buildChips(preferences.typeList.distinct(), binding.typesChipGroup) {
            preferencesViewModel.updatePreferences(it, PreferencesType.TYPE, true)
        }
        for (typeChip in typeChips) {
            binding.typesChipGroup.addView(typeChip)
        }
        val sizeChips = buildChips(preferences.sizeList.distinct(), binding.sizesChipGroup) {
            preferencesViewModel.updatePreferences(it, PreferencesType.SIZE, true)
        }
        for (sizeChip in sizeChips) {
            binding.sizesChipGroup.addView(sizeChip)
        }
    }

    private fun handleLoadingState(isLoading: Boolean) {
        with(binding) {
            colorsChipGroup.isGone = isLoading
            typesChipGroup.isGone = isLoading
            sizesChipGroup.isGone = isLoading
            btnAddNewSize.isEnabled = !isLoading
            btnAddNewColor.isEnabled = !isLoading
            btnAddNewType.isEnabled = !isLoading
            progressIndicator.isGone = !isLoading
        }
    }

}