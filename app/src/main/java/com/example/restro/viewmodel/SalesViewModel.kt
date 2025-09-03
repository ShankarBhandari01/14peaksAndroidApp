package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.restro.base.BaseViewmodel
import com.example.restro.repos.SalesRepository
import com.example.restro.utils.NetWorkResult
import com.example.restro.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val repository: SalesRepository,
    application: Application
) : BaseViewmodel(application) {

    private val _uiEvents = MutableLiveData<UiEvent>()
    val uiEvents: LiveData<UiEvent> = _uiEvents

    fun loadSalesOrders(sortBy: String = "desc", page: Int = 1, limit: Int = 10) {
        viewModelScope.launch {
            _uiEvents.value = UiEvent.ShowLoading
            try {
                val result = repository.getSalesOrders(sortBy, page, limit).last()
                _uiEvents.value = UiEvent.HideLoading

                when (result) {
                    is NetWorkResult.Success -> {
                        if (result.data.type == "success") {
                            _uiEvents.value = UiEvent.Navigate(
                                data = result.data.data
                            )
                        } else {
                            _uiEvents.value =
                                UiEvent.ShowMessage(result.data.message)
                        }
                    }

                    is NetWorkResult.Error -> _uiEvents.value =
                        UiEvent.ShowMessage(result.message ?: "Login failed")

                    is NetWorkResult.Loading -> {}
                }

            } catch (e: Exception) {
                _uiEvents.value = UiEvent.HideLoading
                _uiEvents.value = UiEvent.ShowMessage("Loading failed: ${e.localizedMessage}")
            }
        }
    }


}