package com.example.restro.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.restro.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUser(id: Int): User
}