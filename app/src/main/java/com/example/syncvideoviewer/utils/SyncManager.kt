package com.example.syncvideoviewer.utils

import android.util.Log
import com.example.syncvideoviewer.data.model.SyncCommand
import com.example.syncvideoviewer.data.repository.FirebaseRepository
import com.example.syncvideoviewer.webrtc.WebRTCManager
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val webRTCManager: WebRTCManager,
    private val firebaseRepository: FirebaseRepository
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private var currentRoomId: String? = null
    private var syncJob: Job? = null

    companion object {
        private const val TAG = "SyncManager"
        private const val SYNC_INTERVAL_MS = 5000L // 5 seconds
        private const val POSITION_THRESHOLD_MS = 500L // 500ms tolerance
    }

    fun startSync(roomId: String) {
        currentRoomId = roomId
        startPeriodicSync()
        Log.d(TAG, "Sync started for room: $roomId")
    }

    fun stopSync() {
        syncJob?.cancel()
        currentRoomId = null
        Log.d(TAG, "Sync stopped")
    }

    fun sendPlayCommand() {
        currentRoomId?.let { roomId ->
            coroutineScope.launch {
                val command = SyncCommand(
                    type = "play",
                    timestamp = System.currentTimeMillis(),
                    userId = firebaseRepository.getCurrentUserId()
                )
                sendCommand(roomId, command)
            }
        }
    }

    fun sendPauseCommand() {
        currentRoomId?.let { roomId ->
            coroutineScope.launch {
                val command = SyncCommand(
                    type = "pause",
                    timestamp = System.currentTimeMillis(),
                    userId = firebaseRepository.getCurrentUserId()
                )
                sendCommand(roomId, command)
            }
        }
    }

    fun sendSeekCommand(position: Long) {
        currentRoomId?.let { roomId ->
            coroutineScope.launch {
                val command = SyncCommand(
                    type = "seek",
                    position = position,
                    timestamp = System.currentTimeMillis(),
                    userId = firebaseRepository.getCurrentUserId()
                )
                sendCommand(roomId, command)
            }
        }
    }

    private suspend fun sendCommand(roomId: String, command: SyncCommand) {
        try {
            // Try WebRTC DataChannel first
            webRTCManager.sendDataChannelMessage(
                "${command.type}|${command.position}|${command.timestamp}"
            )
        } catch (e: Exception) {
            Log.w(TAG, "WebRTC send failed, falling back to Firebase", e)
            // Fallback to Firebase
            firebaseRepository.sendSyncCommand(roomId, command)
        }
    }

    private fun startPeriodicSync() {
        syncJob = coroutineScope.launch {
            while (isActive) {
                delay(SYNC_INTERVAL_MS)
                currentRoomId?.let { roomId ->
                    // Emit periodic sync heartbeat
                    Log.d(TAG, "Periodic sync at: ${System.currentTimeMillis()}")
                }
            }
        }
    }

    fun onDestroy() {
        stopSync()
        coroutineScope.cancel()
    }
}
