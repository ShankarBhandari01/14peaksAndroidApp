package com.example.restro.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.restro.R
import com.example.restro.base.BaseViewmodel
import com.example.restro.model.LoginUser
import com.example.restro.repos.LoginRepo
import com.example.restro.utils.Constants
import com.example.restro.utils.NetWorkResult
import com.example.restro.utils.UiEvent
import com.example.restro.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepo,
    application: Application
) : BaseViewmodel(application) {

    val email = MutableLiveData<String>("Waiter@gmail.com")
    val password = MutableLiveData<String>("Waiter@12345")

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _uiEvents = MutableLiveData<UiEvent>()
    val uiEvents: LiveData<UiEvent> = _uiEvents

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
        if (!Utils.isValidEmail(email.value ?: "")) {
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
            deviceInfo = Constants.deviceInfo
        )
    }

    fun login() {
        if (!validateData()) return

        viewModelScope.launch {
            _uiEvents.value = UiEvent.ShowLoading
            try {
                val result = repository.login(prepareLogin()).last()
                _uiEvents.value = UiEvent.HideLoading

                when (result) {
                    is NetWorkResult.Success -> {
                        if (result.data.type == "success") {
                            // Save session data
                            Constants.session.token = result.data.data.session.token
                            Constants.session.refreshToken = result.data.data.session.refreshToken

                            // Navigate to Fragment
                            _uiEvents.value = UiEvent.Navigate(
                                data = result.data.data,
                                destinationId = R.id.dashboardFragment,
                                popUpToId = R.id.loginFragment
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
                _uiEvents.value = UiEvent.ShowMessage("Login failed: ${e.localizedMessage}")
            }
        }
    }


}