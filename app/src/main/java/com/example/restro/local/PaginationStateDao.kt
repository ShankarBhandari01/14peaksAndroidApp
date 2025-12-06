package com.example.restro.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.restro.data.model.PaginationState

@Dao
interface PaginationStateDao {
    @Query("SELECT * FROM pagination_state WHERE id = 0")
    suspend fun getPaginationState(): PaginationState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveState(state: PaginationState)

    @Query("DELETE FROM pagination_state")
    suspend fun clear()
}
