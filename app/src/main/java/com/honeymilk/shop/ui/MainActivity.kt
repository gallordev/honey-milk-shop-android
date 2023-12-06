package com.honeymilk.shop.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.honeymilk.shop.R
import com.honeymilk.shop.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.topAppBar)
        binding.appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(this)


        viewModel.currentUser.observe(this) {
            if (it == null) {
                Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
            }
            if (it?.id?.isNotBlank() == true) {
                Snackbar.make(binding.root, "Account Email: ${it.email}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}