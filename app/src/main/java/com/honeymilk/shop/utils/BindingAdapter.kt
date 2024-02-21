package com.honeymilk.shop.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("imageFromUrl")
    fun setImageFromUrl(view: ImageView, url: String?) {
        Glide.with(view)
            .load(url)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(16)))
            .into(view)
    }

}