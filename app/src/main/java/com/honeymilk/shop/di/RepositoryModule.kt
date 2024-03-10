package com.honeymilk.shop.di

import com.honeymilk.shop.repository.AuthRepository
import com.honeymilk.shop.repository.CampaignRepository
import com.honeymilk.shop.repository.DesignRepository
import com.honeymilk.shop.repository.OrderRepository
import com.honeymilk.shop.repository.impl.AuthRepositoryImpl
import com.honeymilk.shop.repository.impl.CampaignRepositoryImpl
import com.honeymilk.shop.repository.impl.DesignRepositoryImpl
import com.honeymilk.shop.repository.impl.OrderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun provideCampaignRepository(impl: CampaignRepositoryImpl): CampaignRepository

    @Binds
    abstract fun provideDesignRepository(impl: DesignRepositoryImpl): DesignRepository

    @Binds
    abstract fun provideOrdersRepository(impl: OrderRepositoryImpl): OrderRepository

}