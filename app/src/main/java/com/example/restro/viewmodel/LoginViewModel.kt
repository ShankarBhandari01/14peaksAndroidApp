package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.restro.base.BaseViewmodel
import com.example.restro.data.model.LoginUser
import com.example.restro.repositories.LoginRepository
import com.example.restro.utils.ConstantsValues.Companion.deviceInfo
import com.example.restro.utils.ConstantsValues.Companion.session
import com.example.restro.utils.UiState
import com.example.restro.utils.Utilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    application: Application
) : BaseViewmodel(application) {

    val email = MutableLiveData<String>("shankar123@gmail.com")
    val password = MutableLiveData<String>("Shankar@12345")


    val _LoginuiState = MutableStateFlow<UiState<Any>>(UiState.Loading)
    val LoginuiState: StateFlow<UiState<Any>> = _LoginuiState.asStateFlow()

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError


    val isLoginEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        val update = {
            value =
                _emailError.value == null && _passwordError.value == null && !email.value.isNullOrEmpty() &&
                        !password.value.isNullOrEmpty()
        }

        addSource(email) { update() }
        addSource(password) { update() }
        addSource(_emailError) { update() }
        addSource(_passwordError) { update() }
    }


    fun validateData(): Boolean {
        _emailError.value = null
        _passwordError.value = null

        if (email.value.isNullOrEmpty()) {
            _emailError.value = "Email is required"
            return false
        }
        if (password.value.isNullOrEmpty()) {
            _passwordError.value = "Password is required"
            return false
        }
        if (!Utilities.isValidEmail(email.value ?: "")) {
            _emailError.value = "Invalid Email"
            return false
        }
        return true
    }
    private fun prepareLogin(): LoginUser {
        return LoginUser(
            email = email.value.toString(),
            password = password.value.toString(),
            fcmToken = "",
            deviceInfo = deviceInfo
        )
    }

    fun login() {
        if (!validateData()) return

        viewModelScope.launch {
            try {

                repository.login(prepareLogin()).collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            if (state.data.type == "success") {
                                // Save session data
                                session.token = state.data.data.session.token
                                session.refreshToken =
                                    state.data.data.session.refreshToken
                                _LoginuiState.value = UiState.Success(state.data.data)

                            } else {
                                _LoginuiState.value =
                                    UiState.Error(state.data.message)
                            }
                        }
                        is UiState.Error -> _LoginuiState.value =
                            UiState.Error(state.message ?: "Login failed")

                        is UiState.Loading -> {
                            _LoginuiState.value = UiState.Loading
                        }
                    }

                }
            } catch (e: Exception) {
                _LoginuiState.value = UiState.Error("Login failed: ${e.localizedMessage}")
            }
        }
    }

}