package com.example.restro.viewmodel

import androidx.lifecycle.ViewModel
import com.example.restro.repos.SocketIORepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class SocketIOViewModel @Inject constructor(
    private val repository: SocketIORepository
) : ViewModel() {

    val messages: SharedFlow<String> = repository.messages

    fun connect() = repository.connect()

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}