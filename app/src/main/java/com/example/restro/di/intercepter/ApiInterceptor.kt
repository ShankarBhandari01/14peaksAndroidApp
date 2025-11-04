package com.example.restro.di.intercepter

import com.example.restro.utils.Constants.Companion.session
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class ApiInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = originalRequest.newBuilder()
            .header("Authorization", "Bearer ${session.token}")
            .build()

        val response = chain.proceed(request)
        return response
    }
}
