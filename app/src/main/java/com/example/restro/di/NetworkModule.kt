package com.example.restro.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.restro.BuildConfig
import com.example.restro.di.intercepter.ApiInterceptor
import com.example.restro.di.intercepter.ApiInterceptorQualifier
import com.example.restro.apis.Apis
import com.example.restro.service.ApiService
import com.example.restro.service.TokenAuthenticator
import com.example.restro.service.impl.ApisServicesImpl
import com.example.restro.utils.ConstantsValues.Companion.DEV_BASE_URL
import com.example.restro.utils.ConstantsValues.Companion.LIVE_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun providesCheckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        return ChuckerInterceptor(context)
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        chuckerInterceptor: ChuckerInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        authenticator: TokenAuthenticator,
        @ApiInterceptorQualifier apiInterceptor: Interceptor
    ): OkHttpClient {
        val dispatcher = Dispatcher(Executors.newFixedThreadPool(10)).apply {
            maxRequests = 10
            maxRequestsPerHost = 5
        }
        val connectionPool = ConnectionPool(100, 30, TimeUnit.SECONDS)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(apiInterceptor)
            .addInterceptor(chuckerInterceptor)
            .dispatcher(dispatcher)
            .connectionPool(connectionPool)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .authenticator(authenticator)
            .build()
    }

    @ApiInterceptorQualifier
    @Singleton
    @Provides
    fun providesApiKeyInterceptor(): Interceptor = ApiInterceptor()

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder().baseUrl(if (BuildConfig.DEBUG) DEV_BASE_URL else LIVE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideService(retrofit: Retrofit): Apis = retrofit.create(Apis::class.java)

    @Singleton
    @Provides
    fun providesApiService(apis: Apis): ApiService = ApisServicesImpl(apis)


}