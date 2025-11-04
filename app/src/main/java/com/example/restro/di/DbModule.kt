package com.example.restro.di

import android.content.Context
import androidx.room.Room
import com.example.restro.local.OfflineDatabase
import com.example.restro.data.model.User
import com.example.restro.utils.Constants.Companion.OFFLINE_DATABASE
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
        context, OfflineDatabase::class.java, OFFLINE_DATABASE)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration(false)
        .build()

    @Provides
    @Singleton
    fun provideDao(db: OfflineDatabase) = db.userDao()

    @Provides
    fun provideEntity() = User()
}