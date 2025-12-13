package com.example.restro.repositories.Impl

import com.example.restro.service.ApiService
import com.example.restro.base.BaseRepository
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.LoginUser
import com.example.restro.data.model.Session
import com.example.restro.data.model.UserResponse
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.repositories.LoginRepository
import com.example.restro.utils.UiState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class LoginRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkHelper: NetworkHelper
) :
    LoginRepository, BaseRepository() {

    override suspend fun login(login: LoginUser): Flow<UiState<ApiWrapper<UserResponse>>> {
        return baseResponse(networkHelper.isNetworkConnected()) {
            apiService.login(login = login)
        }
    }

    override suspend fun refreshToken(): Flow<UiState<ApiWrapper<Session>>> {
        return baseResponse(networkHelper.isNetworkConnected()) {
            apiService.refreshToken()
        }
    }

}