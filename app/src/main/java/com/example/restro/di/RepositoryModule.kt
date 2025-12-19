package com.example.restro.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.restro.service.ApiService
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.local.OfflineDatabase
import com.example.restro.local.UserDao
import com.example.restro.repositories.CompanyRepository
import com.example.restro.repositories.Impl.CompanyRepositoryImpl
import com.example.restro.repositories.Impl.LoginRepositoryImpl
import com.example.restro.repositories.Impl.ReportsRepositoryImpl
import com.example.restro.repositories.Impl.ReservationRepositoryImpl
import com.example.restro.repositories.Impl.RoomRepositoryImpl
import com.example.restro.service.impl.SocketIOServiceImpl
import com.example.restro.repositories.Impl.UserRepositoryImpl
import com.example.restro.repositories.LoginRepository
import com.example.restro.repositories.ReportsRepository
import com.example.restro.repositories.ReservationRepository
import com.example.restro.repositories.RoomRepository
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
        db: OfflineDatabase,
        apiService: ApiService,
    ): RoomRepository {
        return RoomRepositoryImpl(apiService, db)
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
    fun providesReportsRepository(
        apiService: ApiService,
        networkHelper: NetworkHelper
    ): ReportsRepository {
        return ReportsRepositoryImpl(apiService, networkHelper)
    }

    @Provides
    @Singleton
    fun providesNotificationRepository(
        apiService: ApiService,
        networkHelper: NetworkHelper,
        salesRepository: RoomRepository
    ): SocketIOService {
        return SocketIOServiceImpl(apiService, networkHelper, salesRepository)
    }

    @Provides
    @Singleton
    fun providesCompanyRepository(
        apiService: ApiService,
        networkHelper: NetworkHelper,
    ): CompanyRepository {
        return CompanyRepositoryImpl(apiService, networkHelper)
    }

    @Provides
    @Singleton
    fun providesReservationRepository(
        apiService: ApiService,
        networkHelper: NetworkHelper,
    ): ReservationRepository {
        return ReservationRepositoryImpl(apiService, networkHelper)
    }
}