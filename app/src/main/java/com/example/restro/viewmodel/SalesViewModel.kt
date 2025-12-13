package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.restro.base.BaseViewmodel
import com.example.restro.data.model.FilterOption
import com.example.restro.data.model.Sales
import com.example.restro.repositories.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val repository: RoomRepository,
    application: Application
) : BaseViewmodel(application) {

    private val _selectedFilters = MutableStateFlow<Set<FilterOption>>(mutableSetOf())

    val selectedFilters: Flow<Set<FilterOption>> = _selectedFilters

    private val _salesPagingData = MutableSharedFlow<PagingData<Sales>>(replay = 1)
    val salesPagingData: Flow<PagingData<Sales>> = _salesPagingData

    private val _reservationType = MutableStateFlow("new") // default
    val reservationType = _reservationType.asStateFlow()

    fun setReservationType(type: String) {
        _reservationType.value = type
    }

    fun loadSalesOrders(sort: String = "desc") {
        viewModelScope.launch {
            repository.getSalesOrdersPaging(sort)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _salesPagingData.emit(pagingData)
                }
        }
    }


    val reservations = reservationType
        .flatMapLatest { type ->
            repository.getAllReservation(10, 5, type)
        }
        .cachedIn(viewModelScope)


    fun addFilters(filters: FilterOption) {
        viewModelScope.launch {
            _selectedFilters.value = _selectedFilters.value.toMutableSet().apply {
                if (filters.isSelected) {
                    if (contains(filters)) remove(filters) else add(filters)
                } else {
                    remove(filters)
                }
            }
        }
    }

    fun removeFilters(filters: FilterOption) {
        viewModelScope.launch {
            _selectedFilters.value = _selectedFilters.value.toMutableSet().apply {
                if (contains(filters)) remove(filters)
            }
        }
    }


}