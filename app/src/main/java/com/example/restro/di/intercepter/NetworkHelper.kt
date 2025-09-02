package com.example.restro.di.intercepter


import android.content.Context
import com.example.restro.utils.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isNetworkConnected(): Boolean = Utils.hasInternetConnection(context) && Utils.isOnline()
}
