package com.example.restro.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.restro.base.BaseRepository
import com.example.restro.data.model.Session
import com.example.restro.data.model.User
import com.example.restro.repos.OfflineStoreInterface
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val userDao: UserDao
) : OfflineStoreInterface, BaseRepository() {
    private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    private val USER_ID = stringPreferencesKey("user_id")
    private val SESSION = stringPreferencesKey("session")


    override fun getFirstLaunch(): Flow<Boolean> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map {
            val firstLaunch = it[FIRST_LAUNCH] ?: true
            firstLaunch
        }
    }

    override suspend fun saveFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit {
            it[FIRST_LAUNCH] = isFirstLaunch
        }
    }

    override suspend fun saveUserId(userId: String) {
        dataStore.edit {
            it[USER_ID] = userId
        }
    }

    override fun getUserId(): Flow<String> {
        return dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map {
            val userId = it[USER_ID] ?: ""
            userId
        }
    }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    override fun getUser(id: String): Flow<User> {
        return userDao.getUser()
    }

    override suspend fun saveSession(session: Session) {
        dataStore.edit { prefs ->
            prefs[SESSION] = Gson().toJson(session)
        }
    }

    override fun getSession(): Flow<Session> {
        return dataStore.data
            .map { prefs ->
                prefs[SESSION]?.let { Gson().fromJson(it, Session::class.java) } ?: Session()
            }
    }



}