package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.restro.base.BaseViewmodel
import com.example.restro.data.model.Reports
import com.example.restro.repositories.ReportsRepository
import com.example.restro.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ReportsViewmodel @Inject constructor(
    private val repository: ReportsRepository, application: Application
) : BaseViewmodel(application) {

    val _reportsUiState = MutableStateFlow<UiState<Any>>(UiState.Loading)
    val reportsUiState: StateFlow<UiState<Any>> = _reportsUiState.asStateFlow()

    fun generateReports(days: Int = 30) {
        viewModelScope.launch {
            try {
                repository.getCompanyReport().collect { state ->
                    when (state) {

                        is UiState.Loading -> {
                            _reportsUiState.value = UiState.Loading
                        }

                        is UiState.Success -> {
                            val reports: Reports = state.data.data
                            _reportsUiState.value = UiState.Success(reports)
                        }

                        is UiState.Error -> {
                            _reportsUiState.value = UiState.Error(
                                state.message ?: "Failed to load reports"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _reportsUiState.value = UiState.Error(e.message)
            }
        }
    }
}