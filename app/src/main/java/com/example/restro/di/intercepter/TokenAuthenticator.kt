package com.example.restro.di.intercepter

import com.example.restro.repositories.UserRepository
import com.example.restro.service.ApiService
import com.example.restro.utils.AuthEvent
import com.example.restro.utils.AuthEventBus
import com.example.restro.utils.ConstantsValues.Companion.session
import dagger.Lazy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: UserRepository,
    private val apiService: Lazy<ApiService>
) : Authenticator {

    companion object {
        private val refreshMutex = Mutex()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        // Avoid infinite loops
        if (responseCount(response) >= 2) return null

        // Don't try to refresh on refresh endpoint
        if (response.request.url.encodedPath.contains("refresh")) {
            return null
        }

        return runBlocking {
            refreshMutex.withLock {
                val requestToken =
                    response.request.header("Authorization")?.removePrefix("Bearer ")

                // Token already refreshed by another request
                if (requestToken != session.token) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer ${session.token}")
                        .build()
                }

                Timber.tag("token").d(session.refreshToken)
                // Perform refresh ONCE
                val refreshResponse = apiService.get()
                    .refreshToken()

                if (!refreshResponse.isSuccessful) {
                    // logout
                    logout()
                    return@withLock null
                }

                val newSession = refreshResponse.body()?.data
                    ?: return@withLock null

                // Save refreshed token
                session.token = newSession.token.trim()
                session.refreshToken = newSession.refreshToken.trim()

                tokenManager.saveSession(newSession)

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newSession.token}")
                    .build()
            }
        }
    }

    private suspend fun logout() {
        tokenManager.clearSession()
        AuthEventBus.emit(AuthEvent.Logout)
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
