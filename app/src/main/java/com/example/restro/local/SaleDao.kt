package com.example.restro.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.restro.data.model.Customer
import com.example.restro.data.model.ItemsData
import com.example.restro.data.model.OrderItems
import com.example.restro.data.model.Sales
import com.example.restro.data.model.SalesWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    // ------------------- QUERY -------------------
    @Transaction
    @Query("SELECT * FROM sales_table")
    fun getSalesWithDetails(): Flow<List<SalesWithDetails>>

    @Transaction
    @Query("SELECT * FROM sales_table WHERE roomSalesId = :salesId")
    suspend fun getSalesById(salesId: Int): SalesWithDetails?

    // ------------------- INSERT -------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSales(sales: Sales): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItems>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemsData(data: List<ItemsData>)

    @Transaction
    suspend fun insertSalesWithDetails(salesWithDetails: SalesWithDetails) {
        insertCustomer(salesWithDetails.customer)
        insertSales(salesWithDetails.sales.copy(customerId = salesWithDetails.customer._id))

        insertOrderItems(salesWithDetails.items.map { it.copy(salesId = salesWithDetails.sales._id) })
        insertItemsData(salesWithDetails.itemsData.map { it.copy(salesId = salesWithDetails.sales._id) })
    }

    // ------------------- DELETE -------------------
    @Delete
    suspend fun deleteSales(sales: Sales)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Delete
    suspend fun deleteOrderItems(items: List<OrderItems>)

    @Delete
    suspend fun deleteItemsData(data: List<ItemsData>)

    // ------------------- UPDATE -------------------
    @Update
    suspend fun updateSales(sales: Sales)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Update
    suspend fun updateOrderItems(items: List<OrderItems>)

    @Update
    suspend fun updateItemsData(data: List<ItemsData>)
}
