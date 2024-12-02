package com.honeymilk.shop.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import com.google.firebase.messaging.FirebaseMessaging
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private val authStatusViewModel: AuthStatusViewModel by viewModels()
    private lateinit var navController: NavController

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            authStatusViewModel.registerNotificationToken()
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.topAppBar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.campaignListFragment,
                R.id.designListFragment,
                R.id.preferencesFragment
            )
        )
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)

        NavigationUI.setupWithNavController(binding.contentMain.bottomNavigation, navController)

        val authDestinations = setOf(
            R.id.loginFragment,
            R.id.signUpFragment,
            R.id.passwordRecoveryFragment
        )
        authStatusViewModel.currentUser.observe(this) { signedUser ->
            signedUser?.let {
                askNotificationPermission()
                if (authDestinations.contains(navController.currentDestination?.id)) {
                    val startDestination = navController.graph.startDestinationId
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            } ?: return@observe
        }

        navController.addOnDestinationChangedListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val decorView = window.decorView
            decorView.setOnApplyWindowInsetsListener { v, insets ->
                val gestureInsets = insets.systemGestureInsets
                val leftInset = gestureInsets.left
                val rightInset = gestureInsets.right

                // Adjust these values to define the exclusion rectangle
                val exclusionRect = Rect(
                    binding.topAppBar.left - leftInset,
                    binding.topAppBar.top,
                    binding.topAppBar.right + rightInset,
                    binding.topAppBar.bottom
                )

                window.systemGestureExclusionRects = listOf(exclusionRect)
                insets
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.loginFragment,
            R.id.signUpFragment,
            R.id.passwordRecoveryFragment -> supportActionBar?.hide()

            else -> {
                binding.contentMain.bottomNavigation.isGone = true
                supportActionBar?.show()
            }
        }
        val bottomNavDestinations = setOf(
            R.id.homeFragment,
            R.id.campaignListFragment,
            R.id.designListFragment,
            R.id.preferencesFragment
        )
        binding.contentMain.bottomNavigation.isGone =
            !bottomNavDestinations.contains(destination.id)
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}