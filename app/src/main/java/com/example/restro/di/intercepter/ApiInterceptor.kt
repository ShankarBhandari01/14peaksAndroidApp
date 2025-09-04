package com.example.restro.di.intercepter

import com.example.restro.utils.Constants
import com.example.restro.utils.Constants.Companion.session
import kotlinx.coroutines.sync.Mutex
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class ApiInterceptor @Inject constructor() : Interceptor {

    private val lock = Mutex() // coroutine-safe lock

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        // Add proper token
        val token = if (originalRequest.url.toString().endsWith(Constants.API_REFRESH_TOKEN)) session.refreshToken else session.token

        val request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        val response = chain.proceed(request)
        return response
    }
}
