package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.example.restro.base.BaseViewmodel
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.repositories.RoomRepository
import com.example.restro.utils.UiEvent
import com.example.restro.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val repository: RoomRepository,
    application: Application
) : BaseViewmodel(application) {


    private val _uiEvents = MutableLiveData<UiEvent>()
    val uiEvents: LiveData<UiEvent> = _uiEvents
    private val _salesPagingData = MutableSharedFlow<PagingData<Sales>>(replay = 1)
    val salesPagingData: Flow<PagingData<Sales>> = _salesPagingData

    private val _reservationData = MutableSharedFlow<PagingData<Reservation>>(replay = 1)

    val reservationData: Flow<PagingData<Reservation>> = _reservationData

    private val _newReservation = MutableSharedFlow<PagingData<Reservation>>(replay = 1)
    val newReservation: Flow<PagingData<Reservation>> = _newReservation

    private val _oldReservation = MutableSharedFlow<PagingData<Reservation>>(replay = 1)
    val oldReservation: Flow<PagingData<Reservation>> = _oldReservation

    fun loadSalesOrders(sort: String = "desc") {
        viewModelScope.launch {
            repository.getSalesOrdersPaging(sort)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _salesPagingData.emit(pagingData)
                }
        }
    }

    fun loadReservations() {
        viewModelScope.launch {
            repository.getAllReservation(10)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _reservationData.emit(pagingData)
                }
        }
    }

    fun observeReservations() {
        viewModelScope.launch {
            reservationData.collectLatest { pagingData ->

                val newData = pagingData.filter { res ->
                    Utils.isNewReservation(res, 5)
                }

                val oldData = pagingData.filter { res ->
                    !Utils.isNewReservation(res, 5)
                }

                _newReservation.emit(newData)
                _oldReservation.emit(oldData)
            }
        }
    }
}