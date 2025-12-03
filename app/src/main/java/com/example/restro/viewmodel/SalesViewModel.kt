package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.restro.base.BaseViewmodel
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.repositories.Impl.SalesRepositoryImpl
import com.example.restro.repositories.SalesRepository
import com.example.restro.utils.UiEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val repository: SalesRepository,
    application: Application
) : BaseViewmodel(application) {


    private val _uiEvents = MutableLiveData<UiEvent>()
    val uiEvents: LiveData<UiEvent> = _uiEvents
    private val _salesPagingData = MutableSharedFlow<PagingData<Sales>>(replay = 1)
    val salesPagingData: Flow<PagingData<Sales>> = _salesPagingData

    private val _reservationData = MutableSharedFlow<PagingData<Reservation>>(replay = 1)

    val reservationData: Flow<PagingData<Reservation>> = _reservationData

    fun loadSalesOrders(sort: String = "desc") {
        viewModelScope.launch {
            repository.getSalesOrdersPaging(sort)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _salesPagingData.emit(pagingData)
                }


            repository.getLocalData().collectLatest { data ->
                Timber.tag("local").d(Gson().toJson(data))
            }
        }


    }

    fun loadReservations() {
        viewModelScope.launch {
            repository.getAllReservation()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _reservationData.emit(pagingData)
                }
        }
    }


}