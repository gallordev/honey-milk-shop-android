package com.honeymilk.shop.ui.home

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.FragmentHomeBinding
import com.honeymilk.shop.ui.AuthStatusViewModel
import com.honeymilk.shop.utils.BaseFragment
import com.honeymilk.shop.utils.PermissionHelper
import com.honeymilk.shop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val authStatusViewModel: AuthStatusViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            authStatusViewModel.registerNotificationToken()
            Toast.makeText(
                requireContext(),
                "¡Gracias! Ahora recibirás notificaciones importantes.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                "Permiso denegado. No recibirás notificaciones importantes.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authStatusViewModel.currentUser.observe(viewLifecycleOwner) {
            val user = it ?: return@observe
            if (user.id.isBlank()) {
                findNavController().navigate(R.id.loginFragment)
            } else {
                homeViewModel.loadLastCampaign()
                askNotificationPermission()
            }
        }

        homeViewModel.lastCampaign.observe(viewLifecycleOwner) {
            val resource = it ?: return@observe
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {

                    binding.homeView.isGone = false

                    val campaign = resource.data
                    campaign?.let {
                        binding.campaignName.text = it.name
                        Glide.with(requireContext())
                            .load(it.imageURL)
                            .into(binding.campaignImage)

                        binding.campaignCard.setOnClickListener {
                            val bundle = Bundle().apply { putString("campaignId", campaign.id) }
                            findNavController().navigate(R.id.campaignDetailFragment, bundle)
                        }
                        binding.newOrderCard.setOnClickListener {
                            val bundle = Bundle().apply { putString("campaignId", campaign.id) }
                            findNavController().navigate(R.id.newOrderFragment, bundle)
                        }
                    } ?: run {
                        binding.campaignName.text = getString(R.string.btn_new_campaign)

                        binding.campaignCard.setOnClickListener {
                            findNavController().navigate(R.id.newCampaignFragment)
                        }
                        binding.newOrderCard.setOnClickListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_create_campaign),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error -> {
                    showErrorMessage(resource.message)
                }
            }
        }

        setupMenu()

        binding.newDesignCard.setOnClickListener {
            findNavController().navigate(R.id.newDesignFragment)
        }
    }

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        authStatusViewModel.logout()
                        true
                    }

                    else -> false
                }
            }

        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (PermissionHelper.isPermissionGranted(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                authStatusViewModel.registerNotificationToken()
            } else if (PermissionHelper.shouldShowRationale(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                showPermissionRationale()
            } else {
                PermissionHelper.requestPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS,
                    requestPermissionLauncher
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationale() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permiso necesario")
            .setMessage("Para recibir notificaciones importantes, otorga el permiso.")
            .setPositiveButton("Aceptar") { _, _ ->
                PermissionHelper.requestPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS,
                    requestPermissionLauncher
                )
            }
            .setNegativeButton("Cancelar") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "No recibirás notificaciones importantes.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

}