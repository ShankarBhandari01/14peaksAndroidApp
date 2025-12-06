package com.example.restro.di.intercepter


import android.content.Context
import com.example.restro.utils.Utilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isNetworkConnected(): Boolean = Utilities.hasInternetConnection(context) && Utilities.isOnline()
}
