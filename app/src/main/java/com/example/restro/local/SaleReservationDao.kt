package com.example.restro.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.restro.data.model.Customer
import com.example.restro.data.model.ItemsData
import com.example.restro.data.model.OrderItems
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.model.SalesWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleReservationDao {

    // ------------------- Sales QUERY -------------------
    @Transaction
    @Query("SELECT * FROM sales_table")
    fun getSalesWithDetails(): Flow<List<SalesWithDetails>>

    @Transaction
    @Query("SELECT * FROM sales_table WHERE roomSalesId = :salesId")
    suspend fun getSalesById(salesId: Int): SalesWithDetails?

    @Transaction
    @Query("SELECT * FROM sales_table ORDER BY createdDate DESC")
    fun getSalesPaging(): PagingSource<Int, Sales>


    @Transaction
    @Query("SELECT * FROM reservation_table ORDER BY reservation_date ASC")
    fun getReservationPaging(): PagingSource<Int, Reservation>


    // ------------------- INSERT -------------------
    @Upsert
    suspend fun insertCustomer(customer: Customer)

    @Upsert
    suspend fun insertSales(sales: Sales): Long

    @Upsert
    fun insertSales(sales: List<Sales>)

    @Upsert
    suspend fun insertOrderItems(items: List<OrderItems>)

    @Upsert
    suspend fun insertItemsData(data: List<ItemsData>)

    @Transaction
    suspend fun insertSalesWithDetails(salesWithDetails: SalesWithDetails) {
        insertCustomer(salesWithDetails.customer)
        insertSales(salesWithDetails.sales.copy(customerId = salesWithDetails.customer._id))

        insertOrderItems(salesWithDetails.items.map { it.copy(salesId = salesWithDetails.sales._id) })
        insertItemsData(salesWithDetails.itemsData.map { it.copy(salesId = salesWithDetails.sales._id) })
    }


    @Transaction
    suspend fun insertSalesList(salesList: List<Sales>) {

        //  Insert ALL customers first
        salesList.forEach { sales ->
            sales.customer?.let { customer ->
                insertCustomer(customer)
            }
        }

        // Insert Sales with FK (customerId)
        salesList.forEach { sales ->
            val updatedSales = sales.copy(customerId = sales.customer!!._id)
            insertSales(updatedSales)
        }

        // Insert ALL OrderItems with FK
        val allOrderItems = salesList
            .flatMap { sales ->
                sales.items?.map { item ->
                    item.copy(salesId = sales._id)
                } ?: emptyList()
            }

        insertOrderItems(allOrderItems)

        //  Insert ALL ItemsData with FK
        val allItemsData = salesList
            .flatMap { sales ->
                sales.itemsData?.map { itemData ->
                    itemData.copy(salesId = sales._id)
                } ?: emptyList()
            }

        insertItemsData(allItemsData)
    }


    // ------------------- RESERVATION QUERY -------------------

    @Query("SELECT COUNT(*) FROM reservation_table")
    suspend fun getCount(): Int

    @Transaction
    @Upsert
    suspend fun upsertReservation(reservation: Reservation)

    @Query("Delete from reservation_table")
    suspend fun clearReservation()

    @Query("Delete from sales_table")
    suspend fun clearSales()

    @Insert(onConflict = REPLACE)
    suspend fun upsertReservation(reservation: List<Reservation>)

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
