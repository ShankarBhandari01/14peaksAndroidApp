package com.example.restro.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.restro.service.ApiService
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.local.SaleDao
import com.example.restro.local.UserDao
import com.example.restro.repositories.Impl.LoginRepositoryImpl
import com.example.restro.repositories.Impl.SalesRepositoryImpl
import com.example.restro.service.impl.SocketIOServiceImpl
import com.example.restro.repositories.Impl.UserRepositoryImpl
import com.example.restro.repositories.LoginRepository
import com.example.restro.repositories.SalesRepository
import com.example.restro.service.SocketIOService
import com.example.restro.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // -------------------- Provide Repository --------------------
    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        dataStore: DataStore<Preferences>
    ): UserRepository {
        return UserRepositoryImpl(dataStore, userDao)
    }

    @Provides
    @Singleton
    fun provideSalesRepository(
        salesDao: SaleDao,
        apiService: ApiService,
    ): SalesRepository {
        return SalesRepositoryImpl(apiService, salesDao)
    }

    @Provides
    @Singleton
    fun providesLoginRepository(
        apiService: ApiService,
        networkHelper: NetworkHelper
    ): LoginRepository {
        return LoginRepositoryImpl(apiService, networkHelper)
    }

    @Provides
    @Singleton
    fun providesNotificationRepository(
        apiService: ApiService,
        networkHelper: NetworkHelper,
        salesRepository: SalesRepository
    ): SocketIOService {
        return SocketIOServiceImpl(apiService, networkHelper, salesRepository)
    }


}