package com.example.restro.application

import android.app.Application
import androidx.startup.AppInitializer
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.example.restro.BuildConfig
import com.example.restro.utils.ConstantsValues
import com.example.restro.utils.ConstantsValues.Companion.supervisedScope
import com.example.restro.utils.Utils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import net.danlew.android.joda.JodaTimeInitializer
import timber.log.Timber

@HiltAndroidApp
class RestroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initJodaTime()
        manageCrashingActivity()

        // assign device id to static variable
        supervisedScope.launch {
            val deviceInfo = ConstantsValues.deviceInfo
            deviceInfo.platform = "android"
            deviceInfo.deviceId = Utils.extractDeviceId(this@RestroApplication)
        }
    }


    private fun initJodaTime() {
        AppInitializer.getInstance(this)
            .initializeComponent(JodaTimeInitializer::class.java)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun manageCrashingActivity() {
        CaocConfig.Builder.create()
            .logErrorOnRestart(false) //default: true
            .trackActivities(true) //default: false
            .minTimeBetweenCrashesMs(2000) //default: 3000
            .errorActivity(DefaultErrorActivity::class.java)
            .apply()
    }

}