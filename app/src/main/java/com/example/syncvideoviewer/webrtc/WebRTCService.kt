package com.example.syncvideoviewer.webrtc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WebRTCService : Service() {

    @Inject
    lateinit var webRTCManager: WebRTCManager

    companion object {
        private const val TAG = "WebRTCService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "WebRTC Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "WebRTC Service started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        webRTCManager.close()
        Log.d(TAG, "WebRTC Service destroyed")
    }
}
