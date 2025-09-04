package com.example.restro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restro.repos.SocketIORepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocketIOViewModel @Inject constructor(
    private val repository: SocketIORepository
) : ViewModel() {

    // Expose connection state
    val isConnected: StateFlow<Boolean> = repository.isConnected

    // connect to socket with user id
    fun connect(userId: String) {
        repository.connect(userId)
    }

    fun disconnect() {
        repository.disconnect()
    }

    /**
     * Collect messages in the ViewModel and transform them
     * into LiveData or StateFlow if UI needs direct consumption.
     */
    private val _latestMessage = MutableStateFlow<String?>(null)
    val latestMessage: StateFlow<String?> = _latestMessage.asStateFlow()

    init {
        // Collect incoming messages inside the VM scope
        viewModelScope.launch {
            repository.messages.collect { msg ->
                _latestMessage.value = msg
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}
