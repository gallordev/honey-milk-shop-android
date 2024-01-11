package com.honeymilk.shop.di

import android.content.Context
import com.honeymilk.shop.utils.ImageCompressorHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HelpersModule {

    @Singleton
    @Provides
    fun provideImageCompressorHelper(@ApplicationContext context: Context) = ImageCompressorHelper(context)


}