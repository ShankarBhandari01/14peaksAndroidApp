package com.example.restro.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.restro.data.model.User

@TypeConverters(Converters::class)
@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class OfflineDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}