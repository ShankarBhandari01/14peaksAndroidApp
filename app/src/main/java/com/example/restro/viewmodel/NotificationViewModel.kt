package com.example.restro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.restro.data.model.Notification
import com.example.restro.repos.SocketIORepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    repository: SocketIORepository
) : ViewModel() {

    private val TAG = "NotificationViewModel"

    val notification: Flow<PagingData<Notification>> = repository
        .getApiNotifications()
        .cachedIn(viewModelScope)

}
