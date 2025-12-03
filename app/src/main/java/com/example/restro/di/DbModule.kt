package com.example.restro.di

import android.content.Context
import androidx.room.Room
import com.example.restro.data.model.Sales
import com.example.restro.data.model.User
import com.example.restro.local.OfflineDatabase
import com.example.restro.local.SaleDao
import com.example.restro.local.UserDao
import com.example.restro.repositories.Impl.SalesRepositoryImpl
import com.example.restro.repositories.Impl.UserRepositoryImpl
import com.example.restro.repositories.SalesRepository
import com.example.restro.repositories.UserRepository
import com.example.restro.utils.ConstantsValues.Companion.OFFLINE_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, OfflineDatabase::class.java, OFFLINE_DATABASE
    )
        .fallbackToDestructiveMigration(false)
        .build()

    @Provides
    @Singleton
    fun provideUserDao(db: OfflineDatabase) = db.userDao()

    @Provides
    fun provideUserEntity() = User()

    @Provides
    @Singleton
    fun providesSaleDao(db: OfflineDatabase) = db.saleDao()



}