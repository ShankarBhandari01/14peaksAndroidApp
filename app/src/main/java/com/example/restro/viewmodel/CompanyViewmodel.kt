package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.restro.base.BaseViewmodel
import com.example.restro.repositories.CompanyRepository
import com.example.restro.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyViewmodel @Inject constructor(
    private val repository: CompanyRepository,
    application: Application
) : BaseViewmodel(application) {

    private val _companyUiState = MutableStateFlow<UiState<Any>>(UiState.Loading)
    val companyUiState: StateFlow<UiState<Any>> = _companyUiState.asStateFlow()


    fun getCompanyInfo() {
        viewModelScope.launch {
            try {
                repository.getCompanyInfo().collect { state ->
                    when (state) {
                        is UiState.Loading -> _companyUiState.value = UiState.Loading
                        is UiState.Error -> _companyUiState.value =
                            UiState.Error(state.message ?: "Error !")

                        is UiState.Success -> {
                            if (state.data.type == "success") {
                                _companyUiState.value = UiState.Success(state.data.data.data)
                            } else {
                                _companyUiState.value =
                                    UiState.Error(state.data.message)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _companyUiState.value =
                    UiState.Error(e.localizedMessage)
            }

        }
    }


}