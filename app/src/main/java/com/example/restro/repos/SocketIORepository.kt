package com.example.restro.repos

import com.example.restro.utils.Constants
import com.example.restro.utils.Constants.Companion.supervisedScope
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketIORepository @Inject constructor() {

    private var socket: Socket? = null
    private val gson = Gson()

    // Message flow with buffer
    private val _messages = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 64
    )

    val messages: SharedFlow<String> = _messages.asSharedFlow()

    // Connection state as StateFlow
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    @Synchronized
    fun connect(userId: String) {
        if (socket?.connected() == true) {
            Timber.tag("SocketIO").d("Already connected")
            return
        }

        val opts = IO.Options().apply {
            transports = arrayOf("websocket")
            reconnection = true
            reconnectionAttempts = Int.MAX_VALUE
            reconnectionDelay = 1000
        }

        socket = IO.socket(Constants.WEB_SOCKET_URL, opts).apply {
            on(Socket.EVENT_CONNECT) {
                Timber.tag("SocketIO").d("Connected: %s (userId=%s)", id(), userId)
                _isConnected.value = true

                // Join user room
                emit("joinUserRoom", mapOf("userId" to userId))
            }

            on(Socket.EVENT_DISCONNECT) {
                Timber.tag("SocketIO").w(" Disconnected")
                _isConnected.value = false
            }

            on(Socket.EVENT_CONNECT_ERROR) { args ->
                Timber.tag("SocketIO").e("️ Connection error: ${args.firstOrNull()}")
            }

            on("notification") { args ->
                if (args.isNotEmpty()) {
                    val raw = args[0].toString()
                    Timber.tag("SocketIO").d(" Notification: $raw")

                    supervisedScope.launch {
                        // TODO: Replace with proper model if structured
                        _messages.emit(raw)
                    }
                }
            }
        }

        socket?.connect()
    }

    @Synchronized
    fun disconnect() {
        socket?.let {
            Timber.tag("SocketIO").d("Disconnecting socket")
            it.off() // remove all listeners
            it.disconnect()
        }
        _isConnected.value = false
    }

    /**
     * Reconnect with new userId (e.g. after login/logout)
     */
    @Synchronized
    fun reconnect(userId: String) {
        disconnect()
        connect(userId)
    }
}
