package com.honeymilk.shop.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

class ImageCompressorHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun compressImage(imageUri: Uri): ByteArray {
        val input = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(input)
        val maxImageSize = MAX_IMAGE_SIZE // Adjust this based on your needs
        val scale = (maxImageSize.toFloat() / bitmap.width)
            .coerceAtMost(maxImageSize.toFloat() / bitmap.height)
        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt(),
            (bitmap.height * scale).toInt(),
            true
        )
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
        return outputStream.toByteArray()
    }

    companion object {
        private const val MAX_IMAGE_SIZE = 1024
        private const val COMPRESSION_QUALITY = 80
    }

}