package com.example.restro.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.restro.data.model.RemoteKeys

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getKey(id: String): RemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeys>)

    @Query("DELETE FROM remote_keys")
    suspend fun clearKeys()
}