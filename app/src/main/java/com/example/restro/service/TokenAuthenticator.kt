package com.example.restro.service


import com.example.restro.apis.Apis
import com.example.restro.data.model.Session
import com.example.restro.repositories.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: UserRepository,
    private val apis: dagger.Lazy<ApiService>
) : Authenticator {
    private val TAG = "TokenAuthenticator"

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            // Get latest session
            val session = tokenManager.getSession().first()
            // old tokens
            val oldAccessToken = session.token
            val refreshToken = session.refreshToken

            // Prevent infinite retry if same token is used
            if (response.request.header("Authorization") == "Bearer $oldAccessToken") {
                try {
                    Timber.tag(TAG).d("Request isRetrievable")

                    val refreshResponse = apis.get().refreshToken()
                    if (refreshResponse.isSuccessful) {
                        val newSession: Session? = refreshResponse.body()?.data
                        // Save new Tokens
                        if (newSession != null) {
                            tokenManager.saveSession(newSession)
                        }
                        // Retry the failed request with new token
                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer ${newSession?.token}")
                            .build()

                    } else {
                        Timber.tag(TAG).e(refreshResponse.errorBody()?.string())
                        return@runBlocking null
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e)
                    null
                }
            } else {
                Timber.tag(TAG).d("Request not retractable")
                null
            }
        }
    }
}
