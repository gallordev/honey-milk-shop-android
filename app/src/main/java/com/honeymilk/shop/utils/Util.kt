package com.honeymilk.shop.utils

import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.util.Date

object Util {

    fun String.getFile(): File? {
        if (this.isEmpty()) return null
        val uri: Uri = Uri.parse(this)
        return uri.path?.let {
            File(it)
        }
    }

    fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {
            if (source.height >= source.width) {
                if (source.height <= maxLength) { // if image height already smaller than the required height
                    return source
                }

                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
            } else {
                if (source.width <= maxLength) { // if image width already smaller than the required width
                    return source
                }

                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()

                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: Exception) {
            return source
        }
    }

}