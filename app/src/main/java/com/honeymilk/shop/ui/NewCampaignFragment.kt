package com.honeymilk.shop.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.honeymilk.shop.databinding.FragmentNewCampaignBinding
import com.honeymilk.shop.utils.Util.toDate

class NewCampaignFragment : Fragment() {

    private var _binding: FragmentNewCampaignBinding? = null
    private val binding: FragmentNewCampaignBinding get() = _binding!!

    private val launcher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { Glide.with(requireContext()).load(it).into(binding.imageViewImage) }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewCampaignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .build()

        with(binding) {
            btnAddImage.setOnClickListener {
                launcher.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
                )
            }

            btnDateRange.setOnClickListener {
                dateRangePicker.show(childFragmentManager, tag)
            }

            dateRangePicker.addOnPositiveButtonClickListener {
                textFieldInitialDate.editText?.setText(it.first.toDate()?.toString())
                textFieldFinalDate.editText?.setText(it.second.toDate()?.toString())
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): Fragment = NewCampaignFragment()
    }

}