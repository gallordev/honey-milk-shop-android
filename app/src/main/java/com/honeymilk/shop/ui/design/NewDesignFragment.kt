package com.honeymilk.shop.ui.design

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.honeymilk.shop.databinding.FragmentDesignFormBinding
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import com.honeymilk.shop.utils.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewDesignFragment : BaseFragment<FragmentDesignFormBinding>(FragmentDesignFormBinding::inflate) {

    private var designImageURL: String = ""
    private var designId: String = ""
    private val newDesignViewModel: NewDesignViewModel by viewModels()
    private val launcher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                designImageURL = it.toString()
                Glide.with(requireContext())
                    .load(it)
                    .into(binding.imageViewDesignImage)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            newDesignViewModel.resource.observe(viewLifecycleOwner) {
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
                        designId = resource.data ?: ""
                        findNavController().popBackStack()
                    }
                }
            }
            btnAddImage.setOnClickListener {
                launcher.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
                )
            }
            btnSave.setOnClickListener {
                val designData = getFormData()
                newDesignViewModel.newDesign(designData)
            }
        }
    }

    private fun getFormData(): Design {
        with(binding) {
            return Design(
                name = textFieldName.getText(),
                description = textFieldDescription.getText(),
                group = menuGroup.getText(),
                imageURL = designImageURL,
                presentations = presentationsGroup.getPresentationsData()
            )
        }
    }

    private fun handleLoadingState(isLoading: Boolean) {
        binding.btnSave.hide(isLoading)
        binding.progressIndicator.hide(!isLoading)
    }

}