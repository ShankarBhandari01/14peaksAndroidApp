package com.example.restro.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.restro.data.model.Customer
import com.example.restro.data.model.ItemsData
import com.example.restro.data.model.OrderItems
import com.example.restro.data.model.PaginationState
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.model.User

@TypeConverters(Converters::class)
@Database(
    entities = [User::class, Customer::class, ItemsData::class, OrderItems::class, Sales::class, Reservation::class, PaginationState::class],
    version = 7,
    exportSchema = false
)
abstract class OfflineDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun saleReservationDao(): SaleReservationDao
    abstract fun paginationStateDao(): PaginationStateDao

}