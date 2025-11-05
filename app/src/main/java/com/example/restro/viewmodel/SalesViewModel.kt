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
import com.example.restro.repos.SalesRepository
import com.example.restro.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val repository: SalesRepository,
    application: Application
) : BaseViewmodel(application) {


    private val _uiEvents = MutableLiveData<UiEvent>()
    val uiEvents: LiveData<UiEvent> = _uiEvents

    fun loadSalesOrders(sortBy: String = "desc"): Flow<PagingData<Sales>> {
        return repository.getSalesOrdersPaging(sortBy).cachedIn(viewModelScope)
    }

    fun loadReservations(): Flow<PagingData<Reservation>> {
        return repository.getAllReservation().cachedIn(viewModelScope)
    }

}