package com.example.restro.service.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.restro.BuildConfig
import com.example.restro.base.BaseRepository
import com.example.restro.data.model.Notification
import com.example.restro.data.model.SocketNotification
import com.example.restro.data.paging.ApiPagingSource
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.repositories.RoomRepository
import com.example.restro.service.ApiService
import com.example.restro.service.SocketIOService
import com.example.restro.utils.ConstantsValues
import com.example.restro.utils.Utils.to
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketIOServiceImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkHelper: NetworkHelper,
    private val roomRepository: RoomRepository
) : SocketIOService, BaseRepository() {

    private var socket: Socket? = null

    // Message flow with buffer
    private val _messages = MutableSharedFlow<SocketNotification<Any>>(
        replay = 0, extraBufferCapacity = 64
    )

    val messages: SharedFlow<SocketNotification<Any>> = _messages.asSharedFlow()

    // Connection state as StateFlow
    private val _isConnected = MutableStateFlow(false)
    val isConnect: StateFlow<Boolean> = _isConnected.asStateFlow()

    override fun getMessage(): SharedFlow<SocketNotification<Any>> {
        return messages
    }

    override fun isConnected(): StateFlow<Boolean> {
        return isConnect
    }


    // load notifications from api call
    override fun getApiNotifications(
        limit: Int
    ): Flow<PagingData<Notification>> {
        return Pager(
            config = PagingConfig(pageSize = limit, enablePlaceholders = true),

            pagingSourceFactory = {
                ApiPagingSource { page, limit ->
                    apiService.getNotifications(page, limit).data
                }
            }).flow
    }

    @Synchronized
    override fun connect(userId: String) {
        if (socket?.connected() == true) {
            Timber.Forest.tag("SocketIO").d("Already connected")
            return
        }

        val opts = IO.Options().apply {
            transports = arrayOf("websocket")
            reconnection = true
            reconnectionAttempts = Int.MAX_VALUE
            reconnectionDelay = 1000
        }

        socket = IO.socket(
            if (BuildConfig.DEBUG) ConstantsValues.DEV_WEB_SOCKET_URL else ConstantsValues.WEB_SOCKET_URL,
            opts
        ).apply {
            on(Socket.EVENT_CONNECT) {
                Timber.tag("SocketIO").d("Connected: %s (userId=%s)", id(), userId)
                _isConnected.value = true

                val userJson = JSONObject().apply { put("userId", userId) }
                // Join user room
                emit("joinUserRoom", userJson)
            }

            on(Socket.EVENT_DISCONNECT) {
                Timber.tag("SocketIO").w(" Disconnected")
                _isConnected.value = false
            }

            on(Socket.EVENT_CONNECT_ERROR) { args ->
                Timber.Forest.tag("SocketIO").e("️ Connection error: ${args.firstOrNull()}")
            }

            on("notification") { args ->
                if (args.isNotEmpty()) {
                    ConstantsValues.supervisedScope.launch {
                        val raw = args.firstOrNull()?.toString() ?: return@launch
                        runCatching {
                            Timber.d(raw)

                            val notification: SocketNotification<Any> =
                                raw.to<SocketNotification<Any>>()

                            roomRepository.syncDataSalesReservation(raw, notification)

                            _messages.emit(notification)

                        }.onFailure {
                            Timber.tag("SocketIO").e(it, "Failed to emit socket message")
                        }
                    }
                }
            }
        }

        socket?.connect()
    }

    @Synchronized
    override fun disconnect() {
        socket?.let { s ->
            Timber.Forest.tag("SocketIO").d("Disconnecting socket")
            s.off(Socket.EVENT_CONNECT)
            s.off(Socket.EVENT_DISCONNECT)
            s.off(Socket.EVENT_CONNECT_ERROR)
            s.off("notification")
            s.disconnect()
        }
        _isConnected.value = false
    }

    /**
     * Reconnect with new userId (e.g. after login/logout)
     */
    @Synchronized
    override fun reconnect(userId: String) {
        disconnect()
        connect(userId)
    }


}