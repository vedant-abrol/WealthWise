package com.example.wealthwise

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WealthWiseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code here if needed
    }
} 