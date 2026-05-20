package com.example.syncvideoviewer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncvideoviewer.data.model.*
import com.example.syncvideoviewer.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadRooms()
    }

    private fun loadRooms() {
        viewModelScope.launch {
            firebaseRepository.getRoomsFlow().collect { roomsList ->
                _rooms.value = roomsList
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun createRoom(name: String, url: String, isPrivate: Boolean, code: String) {
        viewModelScope.launch {
            val room = Room(
                name = name,
                videoUrl = url,
                isPrivate = isPrivate,
                accessCode = if (isPrivate) code else null,
                hostId = firebaseRepository.getCurrentUserId()
            )
            firebaseRepository.createRoom(room)
        }
    }

    fun joinRoom(roomId: String, password: String? = null) {
        viewModelScope.launch {
            val result = firebaseRepository.getRoomById(roomId)
            result.onSuccess { room ->
                if (room.isPrivate && room.accessCode != password) {
                    // Show error
                    return@onSuccess
                }
                val userId = firebaseRepository.getCurrentUserId()
                firebaseRepository.addParticipant(roomId, userId)
            }
        }
    }
}

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _room = MutableStateFlow<Room?>(null)
    val room: StateFlow<Room?> = _room.asStateFlow()

    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _syncStatus = MutableStateFlow("connecting")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private var currentRoomId: String? = null

    fun joinRoom(roomId: String) {
        viewModelScope.launch {
            currentRoomId = roomId
            val result = firebaseRepository.getRoomById(roomId)
            result.onSuccess { room ->
                _room.value = room
                val userId = firebaseRepository.getCurrentUserId()
                firebaseRepository.addParticipant(roomId, userId)
                _syncStatus.value = "connected"
            }
            result.onFailure {
                _syncStatus.value = "error"
            }
        }
    }

    fun leaveRoom() {
        currentRoomId = null
        _room.value = null
        _participants.value = emptyList()
        _chatMessages.value = emptyList()
    }

    fun sendPlayPauseCommand(isPlaying: Boolean) {
        currentRoomId?.let { roomId ->
            viewModelScope.launch {
                val command = SyncCommand(
                    type = if (isPlaying) "play" else "pause",
                    timestamp = System.currentTimeMillis(),
                    userId = firebaseRepository.getCurrentUserId()
                )
                firebaseRepository.sendSyncCommand(roomId, command)
            }
        }
    }

    fun sendSeekCommand(position: Long) {
        currentRoomId?.let { roomId ->
            viewModelScope.launch {
                val command = SyncCommand(
                    type = "seek",
                    position = position,
                    timestamp = System.currentTimeMillis(),
                    userId = firebaseRepository.getCurrentUserId()
                )
                firebaseRepository.sendSyncCommand(roomId, command)
            }
        }
    }

    fun sendMessage(text: String) {
        currentRoomId?.let { roomId ->
            viewModelScope.launch {
                val message = ChatMessage(
                    userId = firebaseRepository.getCurrentUserId(),
                    userName = "User",
                    text = text,
                    timestamp = com.google.firebase.Timestamp.now()
                )
                firebaseRepository.sendChatMessage(roomId, message)
            }
        }
    }
}
