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
import com.honeymilk.shop.databinding.FragmentNewDesignBinding
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.model.Design
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewDesignFragment : BaseFragment<FragmentNewDesignBinding>(FragmentNewDesignBinding::inflate) {

    private var designImageURL: String = ""
    private val newDesignViewModel: NewDesignViewModel by viewModels()
    private val launcher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                designImageURL = it.toString()
                Glide.with(requireContext()).load(it).into(binding.imageViewDesignImage)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            newDesignViewModel.resource.observe(viewLifecycleOwner) {
                val resource = it ?: return@observe
                when(resource) {
                    is Resource.Error -> {
                        btnSave.isEnabled = true
                        showErrorMessage(resource.message ?: "Unknown Error")
                    }
                    is Resource.Loading -> {
                        btnSave.isEnabled = false
                    }
                    is Resource.Success -> {
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
                imageURL = designImageURL
            )
        }
    }

}