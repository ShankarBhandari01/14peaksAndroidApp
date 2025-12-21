package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.restro.base.BaseViewmodel
import com.example.restro.data.model.ReservationStatusRequest
import com.example.restro.repositories.ReservationRepository
import com.example.restro.repositories.RoomRepository
import com.example.restro.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewmodel @Inject constructor(
    private val room: RoomRepository,
    private val repository: ReservationRepository,
    application: Application
) : BaseViewmodel(application) {
    private val _reservationType = MutableStateFlow("new") // default
    val reservationType = _reservationType.asStateFlow()


    private val _reservationStatus = MutableSharedFlow<UiState<Any>>(replay = 0)
    val reservationStatus: SharedFlow<UiState<Any>> = _reservationStatus

    fun setReservationType(type: String) {
        _reservationType.value = type
    }


    val reservations = reservationType
        .flatMapLatest { type ->
            room.getAllReservation(10, 5, type)
        }
        .cachedIn(viewModelScope)


    fun updateStateReservation(id: String, status: ReservationStatusRequest) {
        viewModelScope.launch {
            try {
                _reservationStatus.emit(UiState.Loading)
                repository.updateReservationStatus(id, status).collect { state ->
                    when (state) {
                        is UiState.Loading -> _reservationStatus.emit(UiState.Loading)

                        is UiState.Error -> _reservationStatus.emit(
                            UiState.Error(
                                state.message ?: "Status update failed"
                            )
                        )

                        is UiState.Success -> {
                            // insert reservation to database
                            room.insertReservation(state.data.data.data)
                            _reservationStatus.emit(UiState.Success(state.data.data.data))
                        }
                    }
                }
            } catch (e: Exception) {
                _reservationStatus.emit(UiState.Error(e.localizedMessage))
            }
        }
    }
}