package com.honeymilk.shop.ui.campaign

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentNewCampaignBinding
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCampaignFragment :
    BaseFragment<FragmentNewCampaignBinding>(FragmentNewCampaignBinding::inflate) {

    private var campaignImageURL: String = ""
    private val newCampaignViewModel: NewCampaignViewModel by viewModels()
    private val launcher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                campaignImageURL = it.toString()
                Glide.with(requireContext()).load(it).into(binding.imageViewCampaignImage)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            newCampaignViewModel.resource.observe(viewLifecycleOwner) {
                val resource = it ?: return@observe
                when (resource) {
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
                val campaign = getFormData()
                newCampaignViewModel.newCampaign(campaign)
            }
        }
    }

    private fun getFormData(): Campaign {
        with(binding) {
            return Campaign(
                name = textFieldName.getText(),
                description = textFieldDescription.getText(),
                imageURL = campaignImageURL,
                isActive = switchCampaignStatus.isChecked
            )
        }
    }

}