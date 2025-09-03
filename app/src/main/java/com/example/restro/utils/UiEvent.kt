package com.example.restro.utils


sealed class UiEvent {
    data object ShowLoading : UiEvent()
    data object HideLoading : UiEvent()
    data class ShowMessage(val message: String) : UiEvent()
    data class Navigate(
        val data: java.io.Serializable? = null,
        val destinationId: Int? = null,
        val popUpToId: Int? = null
    ) : UiEvent()

    data class NavigateToActivity(
        val activityClass: Class<*>,      // Target Activity
        val finishCurrent: Boolean = false // Optional: finish current Activity
    ) : UiEvent()
}
