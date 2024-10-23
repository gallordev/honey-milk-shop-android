package com.honeymilk.shop.ui

import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private val authStatusViewModel: AuthStatusViewModel by viewModels()
    private lateinit var navController: NavController

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
        when(destination.id) {
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
        binding.contentMain.bottomNavigation.isGone = !bottomNavDestinations.contains(destination.id)
    }
}