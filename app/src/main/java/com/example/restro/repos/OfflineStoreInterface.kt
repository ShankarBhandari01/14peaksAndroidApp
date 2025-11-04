package com.example.restro.repos

import com.example.restro.data.model.Session
import com.example.restro.data.model.User
import kotlinx.coroutines.flow.Flow

interface OfflineStoreInterface {
    fun getFirstLaunch(): Flow<Boolean>
    suspend fun saveFirstLaunch(isFirstLaunch: Boolean)
    suspend fun saveUserId(userId: String)
    fun getUserId(): Flow<String>
    suspend fun insertUser(user: User)
    fun getUser(id: String): Flow<User>
    suspend fun saveSession(session: Session)
    fun getSession(): Flow<Session>

}