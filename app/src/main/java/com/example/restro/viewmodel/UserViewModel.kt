package com.example.restro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.restro.data.model.Session
import com.example.restro.data.model.User
import com.example.restro.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val getUser: LiveData<User> = userRepository.getUser("").asLiveData()

    // Get session
    val sessionFlow: Flow<Session> = userRepository.getSession()

    val userId: LiveData<String> = userRepository.getUserId().asLiveData()

    var isFirstLaunch = userRepository.getFirstLaunch()

    fun setFirstLaunch(isFirstLaunch: Boolean) =
        viewModelScope.launch {
            userRepository.saveFirstLaunch(isFirstLaunch)
        }

    fun saveUserId(userId: String) = viewModelScope.launch {
        userRepository.saveUserId(userId)
    }

    fun saveUser(user: User) = viewModelScope.launch {
        userRepository.insertUser(user)
    }

    fun saveSession(session: Session) = viewModelScope.launch {
        userRepository.saveSession(session)
    }

    suspend fun updateAccessToken(accessToken: String) {
        val current = sessionFlow.first()
        userRepository.saveSession(current.copy(token = accessToken))
    }

    suspend fun updateRefreshToken(refreshToken: String) {
        val current = sessionFlow.first()
        userRepository.saveSession(current.copy(refreshToken = refreshToken))
    }
}