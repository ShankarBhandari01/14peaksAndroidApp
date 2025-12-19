package com.example.restro.utils

import com.example.restro.data.model.DeviceInfo
import com.example.restro.data.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ConstantsValues {
    companion object {

        /**
         *
         * Session
         */
        val session = Session()

        /****
         * Devive Info
         */
        val deviceInfo: DeviceInfo = DeviceInfo()

        /**
         * Notification channel details
         */
        const val NOTIFICATION_CHANNEL_ID = "MyForegroundServiceChannel"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE = 0x01;


        /**
         *
         * Error Messages
         */
        const val API_INTERNET_MESSAGE = "No Internet Connection"
        const val API_SOMETHING_WENT_WRONG_MESSAGE = "Something went wrong"
        const val API_FAILED_CODE = "500"
        const val API_SUCCESS_CODE = "9999"
        const val API_FAILURE_CODE = "5555"
        const val API_INTERNET_CODE = "500"


        /**
         * Offline Values Lists
         * */
        const val USER_PREFERENCES = "USER_PREFERENCES"
        const val OFFLINE_DATABASE = "app_database"
        const val NOTE_TABLE = "NOTE_TABLE"

        /**
         * View types
         * */
        const val CAMPAIGN_VIEW_TYPE_TYPE = 0
        const val FOLLOW_UP_VIEW_TYPE_TYPE = 1
        const val DASHBOARD_MENU_VIEW_TYPE = 2

        /**
         * API Lists
         */
        const val LIVE_BASE_URL = "https://api.ravintola14peaks.fi/api/v1/"
        const val DEV_BASE_URL = "http://10.0.2.2:8080/api/v1/"
        const val WEB_SOCKET_URL = "https://api.ravintola14peaks.fi/"

        const val DEV_WEB_SOCKET_URL = "http://10.0.2.2:8080/"
        const val API_KEY = ""
        const val API_LOGON = "login"
        const val API_GET_All_ORDERS = "AllOrders"
        const val API_REFRESH_TOKEN = "token/refresh"
        const val API_NOTIFICATION = "notification"
        const val API_GET_ALL_RESERVATION = "getAllReservations"
        const val API_ANALYSE_REPORTS = "analyse_reports"
        const val API_COMPANY_INFO = "getCompanyInfo"
        const val API_UPDATE_RESERVATION_STATUS = "updateReservationStatus/{reservationId}"


        // Structured scope for background work
        val supervisedScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}