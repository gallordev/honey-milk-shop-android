package com.honeymilk.shop.ui.design

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.honeymilk.shop.databinding.FragmentDesignFormBinding
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import com.honeymilk.shop.utils.hide
import com.honeymilk.shop.utils.setText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateDesignFragment : BaseFragment<FragmentDesignFormBinding>(FragmentDesignFormBinding::inflate) {

    private var designImageURL: String = ""
    private val args: UpdateDesignFragmentArgs by navArgs()
    private val updateDesignViewModel: UpdateDesignViewModel by viewModels()
    private val launcher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                designImageURL = it.toString()
                loadDesignImage(designImageURL)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateDesignViewModel.getDesign(args.designId)
        updateDesignViewModel.design.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            if (resource is Resource.Success) {
                resource.data?.let { design ->
                    loadDesign(design)
                }
            }
        }
        updateDesignViewModel.resource.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when(resource) {
                is Resource.Error -> {
                    handleLoadingState(isLoading = false)
                    showErrorMessage(resource.message ?: "Unknown Error")
                }
                is Resource.Loading -> {
                    handleLoadingState(isLoading = true)
                }
                is Resource.Success -> {
                    handleLoadingState(isLoading = false)
                    findNavController().popBackStack()
                }
            }
        }
        with(binding) {
            btnAddImage.setOnClickListener {
                launcher.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
                )
            }
            btnSave.setOnClickListener {
                val design: Design = updateDesignViewModel.design.value?.data ?: Design()
                val designData = getFormData()
                if(designData.id.isNotBlank()) {
                    updateDesignViewModel.updateDesign(getFormData(), designImageURL != design.imageURL)
                } else {
                    Toast.makeText(requireContext(), "Unknown Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadDesign(design: Design) {
        loadDesignImage(design.imageURL)
        with(binding) {
            textFieldName.setText(design.name)
            textFieldDescription.setText(design.description)
            (menuGroup.editText as? MaterialAutoCompleteTextView)?.apply {
                this.setText(design.group, false)
            }
            presentationsGroup.setPresentationsData(design.presentations)
        }
    }

    private fun loadDesignImage(imageURL: String) {
        designImageURL = imageURL
        Glide.with(requireContext())
            .load(imageURL)
            .into(binding.imageViewDesignImage)
    }

    private fun getFormData(): Design {
        val design: Design = updateDesignViewModel.design.value?.data ?: return Design()
        with(binding) {
            return design.copy(
                name = textFieldName.getText(),
                description = textFieldDescription.getText(),
                group = menuGroup.getText(),
                imageURL = if(designImageURL != design.imageURL) designImageURL else design.imageURL,
                presentations = presentationsGroup.getPresentationsData()
            )
        }
    }

    private fun handleLoadingState(isLoading: Boolean) {
        binding.btnSave.hide(isLoading)
        binding.progressIndicator.hide(!isLoading)
    }

}