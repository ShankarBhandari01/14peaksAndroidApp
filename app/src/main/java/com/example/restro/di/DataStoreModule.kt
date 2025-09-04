package com.example.restro.di

import com.example.restro.local.OfflineStoreImpl
import com.example.restro.repos.OfflineStoreInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Singleton
    @Binds
    abstract fun bindDataStore(dataStoreImpl: OfflineStoreImpl): OfflineStoreInterface
}