package com.example.restro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.restro.data.model.Notification
import com.example.restro.data.model.SocketNotification
import com.example.restro.repos.SocketIORepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocketIOViewModel @Inject constructor(
    private val repository: SocketIORepository
) : ViewModel() {

    private val TAG = "SocketIOViewModel"

    val notification: Flow<PagingData<Notification>> = repository
        .getApiNotifications()
        .cachedIn(viewModelScope)

    // Expose connection state
    val isConnected: StateFlow<Boolean> = repository.isConnected

    // connect to socket with user id
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun connect(userId: String) {
        try {
            repository.connect(userId)
        } catch (e: Exception) {
            _errorMessage.value = e.message
        }
    }

    fun reconnect(userId: String) {
        repository.reconnect(userId)
    }

    fun disconnect() {
        repository.disconnect()
    }

    private val _latestMessage = MutableSharedFlow<SocketNotification>(
        replay = 0,
        extraBufferCapacity = 64
    )
    val latestMessage: SharedFlow<SocketNotification> = _latestMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.messages.collect { msg ->

                _latestMessage.emit(msg)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}
