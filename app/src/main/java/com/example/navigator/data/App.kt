package com.example.navigator.data

import android.app.Application
import com.example.navigator.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        const val ADMIN_MODE = "admin"
        const val USER_MODE = "user"
        const val mode = BuildConfig.FLAVOR
        const val isAdmin = mode == ADMIN_MODE
        const val isUser = mode == USER_MODE
    }
}