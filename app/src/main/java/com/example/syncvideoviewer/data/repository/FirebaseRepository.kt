package com.example.syncvideoviewer.data.repository

import com.example.syncvideoviewer.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val realtimeDb: FirebaseDatabase,
    private val auth: FirebaseAuth
) {

    suspend fun createRoom(room: Room): Result<String> = try {
        val roomRef = firestore.collection("rooms").document()
        roomRef.set(room.copy(id = roomRef.id)).await()
        Result.success(roomRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getRoomById(roomId: String): Result<Room> = try {
        val room = firestore.collection("rooms")
            .document(roomId)
            .get()
            .await()
            .toObject(Room::class.java)
        if (room != null) {
            Result.success(room)
        } else {
            Result.failure(Exception("Room not found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getRoomsFlow(): Flow<List<Room>> = flow {
        try {
            firestore.collection("rooms")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    val rooms = snapshot?.documents?.mapNotNull {
                        it.toObject(Room::class.java)
                    } ?: emptyList()
                }
        } catch (e: Exception) {
            // Error handling
        }
    }

    suspend fun addParticipant(roomId: String, participantId: String): Result<Unit> = try {
        firestore.collection("rooms").document(roomId).update(
            "participants", com.google.firebase.firestore.FieldValue.arrayUnion(participantId)
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveOffer(roomId: String, userId: String, offer: String): Result<Unit> = try {
        realtimeDb.reference
            .child("signaling/offers/$roomId/$userId")
            .setValue(offer)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveAnswer(roomId: String, userId: String, answer: String): Result<Unit> = try {
        realtimeDb.reference
            .child("signaling/answers/$roomId/$userId")
            .setValue(answer)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addICECandidate(
        roomId: String,
        userId: String,
        candidate: ICECandidate
    ): Result<Unit> = try {
        realtimeDb.reference
            .child("signaling/ice/$roomId/$userId")
            .push()
            .setValue(candidate)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendSyncCommand(roomId: String, command: SyncCommand): Result<Unit> = try {
        realtimeDb.reference
            .child("fallback_commands/$roomId")
            .push()
            .setValue(command)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendChatMessage(roomId: String, message: ChatMessage): Result<Unit> = try {
        realtimeDb.reference
            .child("chat/$roomId")
            .push()
            .setValue(message)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getChatMessagesFlow(roomId: String): Flow<List<ChatMessage>> = flow {
        // Implementation for real-time chat updates
    }

    suspend fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: generateGuestId()
    }

    private fun generateGuestId(): String {
        return "guest_${System.currentTimeMillis()}"
    }
}
