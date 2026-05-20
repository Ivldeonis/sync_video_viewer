package com.example.syncvideoviewer

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SyncVideoViewerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("SyncVideoViewerApp", "Application created")
        
        // Initialize Firebase (optional - it's done automatically)
        // Initialize any other global configurations
        
        setupCrashHandling()
    }

    private fun setupCrashHandling() {
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e("CrashHandler", "Uncaught exception: ${exception.message}", exception)
            exception.printStackTrace()
        }
    }
}
