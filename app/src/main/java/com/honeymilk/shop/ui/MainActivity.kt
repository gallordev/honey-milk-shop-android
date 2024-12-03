package com.honeymilk.shop.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.ActivityMainBinding
import com.honeymilk.shop.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private val authStatusViewModel: AuthStatusViewModel by viewModels()
    private lateinit var navController: NavController
    private val bottomNavDestinations = setOf(
        R.id.homeFragment,
        R.id.campaignListFragment,
        R.id.designListFragment,
        R.id.preferencesFragment
    )
    private val authDestinations = setOf(
        R.id.loginFragment,
        R.id.signUpFragment,
        R.id.passwordRecoveryFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.topAppBar)
        setupNavigation()
        setupBottomNavigation()
        authStatusViewModel.currentUser.observe(this) { signedUser ->
            signedUser?.let {
                if (authDestinations.contains(navController.currentDestination?.id)) {
                    val startDestination = navController.graph.startDestinationId
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            } ?: return@observe
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        updateUIForDestination(destination.id)
    }

    private fun updateUIForDestination(destinationId: Int) {
        val isAuthScreen = authDestinations.contains(destinationId)
        val isBottomNavDestination = bottomNavDestinations.contains(destinationId)
        supportActionBar?.apply {
            if (isAuthScreen) hide() else show()
        }
        binding.contentMain.bottomNavigation.isGone = !isBottomNavDestination
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.popBackStack()) {
                    finish()
                }
            }
        })
    }

    private fun setupBottomNavigation() {
        val appBarConfiguration = AppBarConfiguration(bottomNavDestinations)
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.contentMain.bottomNavigation, navController)
    }

}
