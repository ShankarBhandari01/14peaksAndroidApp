package com.example.restro.di.intercepter

import com.example.restro.utils.ConstantsValues.Companion.session
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class ApiInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        // Get clean tokens
        val accessToken = session.token.trim()
        val refreshToken = session.refreshToken.trim()

        // Choose which token to send
        val token = if (originalRequest.url.encodedPath.contains("token/refresh")) {
            "Bearer $refreshToken"
        } else {
            "Bearer $accessToken"
        }

        // Build request
        val request = originalRequest.newBuilder()
            .header("Authorization", token)
            .build()

        return chain.proceed(request)
    }
}
