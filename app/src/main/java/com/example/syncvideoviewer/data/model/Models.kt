package com.example.syncvideoviewer.data.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Room(
    val id: String = "",
    val name: String = "",
    val hostId: String = "",
    val videoUrl: String = "",
    val isPrivate: Boolean = false,
    val accessCode: String? = null,
    val participants: List<String> = emptyList(),
    val createdAt: Timestamp? = null
) : Serializable

data class Participant(
    val id: String = "",
    val name: String = "",
    val isHost: Boolean = false,
    val joinedAt: Timestamp? = null
) : Serializable

data class ChatMessage(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null
) : Serializable

data class SyncCommand(
    val type: String = "", // "play", "pause", "seek"
    val position: Long = 0,
    val timestamp: Long = 0,
    val userId: String = ""
) : Serializable

data class WebRTCSignal(
    val type: String = "", // "offer", "answer", "ice"
    val data: String = "",
    val fromUserId: String = ""
) : Serializable

data class ICECandidate(
    val candidate: String = "",
    val sdpMLineIndex: Int = 0,
    val sdpMid: String? = null
) : Serializable
