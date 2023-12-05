package com.honeymilk.shop.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.shape.MaterialShapeDrawable
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.topAppBar)
        binding.appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(this)
    }
}