package com.example.restro.repos

import com.example.restro.apis.ApisServicesImpl
import com.example.restro.base.BaseRepository
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.model.LoginResponse
import com.example.restro.model.LoginUser
import com.example.restro.utils.NetWorkResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class LoginRepo @Inject constructor(
    private val apisServicesImpl: ApisServicesImpl,
    private val networkHelper: NetworkHelper
) :
    BaseRepository() {

    suspend fun login(login: LoginUser): Flow<NetWorkResult<LoginResponse>> {
        return baseResponse(networkHelper.isNetworkConnected()) {
            apisServicesImpl.login(login = login)
        }
    }

}