package com.example.restro.repos

import com.example.restro.utils.Constants
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketIORepository @Inject constructor() {

    private lateinit var socket: Socket
    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun connect(userId: String) {
        if (::socket.isInitialized && socket.connected()) return

        val opts = IO.Options().apply {
            transports = arrayOf("websocket")
            reconnection = true
            reconnectionAttempts = Int.MAX_VALUE
            reconnectionDelay = 1000
        }
        socket = IO.socket(Constants.WEB_SOCKET_URL, opts)

        socket.on(Socket.EVENT_CONNECT) {
            Timber.tag("SocketIO").d("Connected: %s", socket.id())

            // Join user room dynamically
            socket.emit("joinUserRoom", mapOf("userId" to userId))
        }

        socket.on("notification") { args ->
            if (args.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    _messages.emit(args[0].toString())
                }
            }
        }

        socket.connect()
    }

    fun disconnect() {
        if (::socket.isInitialized) socket.disconnect()
    }
}
