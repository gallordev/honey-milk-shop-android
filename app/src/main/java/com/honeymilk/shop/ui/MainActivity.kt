package com.honeymilk.shop.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private val mAuthViewModel: AuthStatusViewModel by viewModels()
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(mBinding.topAppBar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        mNavController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(mNavController.graph)
        mBinding.topAppBar.setupWithNavController(mNavController, appBarConfiguration)
        mAuthViewModel.currentUser.observe(this) { signedUser ->
            signedUser?.let {
                if (mNavController.currentDestination?.id != R.id.homeFragment) {
                    val startDestination = mNavController.graph.startDestinationId
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    mNavController.navigate(startDestination, null, navOptions)
                }
            } ?: return@observe
        }
    }
}