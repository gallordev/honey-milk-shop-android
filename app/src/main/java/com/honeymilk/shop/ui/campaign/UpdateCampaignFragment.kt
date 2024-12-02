package com.honeymilk.shop.ui.campaign

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.honeymilk.shop.databinding.FragmentCampaignFormBinding
import com.honeymilk.shop.model.Campaign
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.Resource
import com.honeymilk.shop.utils.getText
import com.honeymilk.shop.utils.hide
import com.honeymilk.shop.utils.setText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateCampaignFragment : BaseFragment<FragmentCampaignFormBinding>(
    FragmentCampaignFormBinding::inflate
) {

    private var campaignImageURL: String = ""
    private val args: UpdateCampaignFragmentArgs by navArgs()
    private val updateCampaignViewModel: UpdateCampaignViewModel by viewModels()
    private val launcher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                campaignImageURL = it.toString()
                Glide.with(requireContext()).load(it).into(binding.imageViewCampaignImage)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        updateCampaignViewModel.getCampaign(args.campaignId)

        updateCampaignViewModel.campaign.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when (resource) {
                is Resource.Error -> {
                    handleLoadingState(isLoading = false)
                    showErrorMessage(resource.message ?: "Unknown Error")
                }

                is Resource.Loading -> {
                    handleLoadingState(isLoading = true)
                }

                is Resource.Success -> {
                    loadCampaign(resource.data ?: Campaign())
                    handleLoadingState(isLoading = false)
                }
            }
        }

        updateCampaignViewModel.resource.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when (resource) {
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

        binding.btnAddImage.setOnClickListener {
            launcher.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
            )
        }

        binding.btnSave.setOnClickListener {
            val campaign = updateCampaignViewModel.campaign.value?.data ?: Campaign()
            val campaignData = getFormData()
            updateCampaignViewModel.updateCampaign(campaignData, campaignImageURL != campaign.imageURL)
        }

    }

    private fun loadCampaign(campaign: Campaign) {
        with(binding) {
            campaignImageURL = campaign.imageURL
            textFieldName.setText(campaign.name)
            textFieldDescription.setText(campaign.description)
            switchCampaignStatus.isChecked = campaign.isActive
            Glide.with(requireContext()).load(campaignImageURL).into(imageViewCampaignImage)
        }
    }

    private fun getFormData(): Campaign {
        val campaign = updateCampaignViewModel.campaign.value?.data ?: return Campaign()
        with(binding) {
            return campaign.copy(
                name = textFieldName.getText(),
                description = textFieldDescription.getText(),
                imageURL = if(campaignImageURL != campaign.imageURL) campaignImageURL else campaign.imageURL,
                isActive = switchCampaignStatus.isChecked
            )
        }
    }

    private fun handleLoadingState(isLoading: Boolean) {
        binding.btnSave.hide(isLoading)
        binding.progressIndicator.hide(!isLoading)
    }

}