package com.example.restro.di.intercepter

import android.content.Context
import com.example.restro.utils.Constants
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class ApiInterceptor(var context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var newRequest: Request = chain.request()

        newRequest = newRequest.newBuilder()
            .addHeader(
                "AccessToken",
                Constants.session.token
            )
            .build()

        return chain.proceed(newRequest)
    }
}