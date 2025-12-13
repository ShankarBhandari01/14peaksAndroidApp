package com.example.restro.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.restro.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
open class BaseViewmodel @Inject constructor(application: Application) :
    AndroidViewModel(application) {
    protected val context
        get() = getApplication<Application>()

}