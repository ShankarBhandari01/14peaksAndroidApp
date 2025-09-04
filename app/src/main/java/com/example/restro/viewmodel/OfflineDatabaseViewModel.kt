package com.example.restro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.restro.model.Session
import com.example.restro.model.User
import com.example.restro.repos.OfflineStoreInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfflineDatabaseViewModel @Inject constructor(
    private val offlineStoreInterface: OfflineStoreInterface
) : ViewModel() {

    val getUser: LiveData<User> = offlineStoreInterface.getUser("").asLiveData()

    // Get session
    val sessionFlow: Flow<Session> = offlineStoreInterface.getSession()

    val userId: LiveData<String> = offlineStoreInterface.getUserId().asLiveData()
    val isFirstLaunch: LiveData<Boolean> = offlineStoreInterface.getFirstLaunch().asLiveData()

    fun setFirstLaunch(isFirstLaunch: Boolean) =
        viewModelScope.launch {
            offlineStoreInterface.saveFirstLaunch(isFirstLaunch)
        }

    fun saveUserId(userId: String) = viewModelScope.launch {
        offlineStoreInterface.saveUserId(userId)
    }

    fun saveUser(user: User) = viewModelScope.launch {
        offlineStoreInterface.insertUser(user)
    }

    fun saveSession(session: Session) = viewModelScope.launch {
        offlineStoreInterface.saveSession(session)
    }

    suspend fun updateAccessToken(accessToken: String) {
        val current = sessionFlow.first()
        offlineStoreInterface.saveSession(current.copy(token = accessToken))
    }

    suspend fun updateRefreshToken(refreshToken: String) {
        val current = sessionFlow.first()
        offlineStoreInterface.saveSession(current.copy(refreshToken = refreshToken))
    }
}