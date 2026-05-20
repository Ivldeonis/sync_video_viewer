package com.example.syncvideoviewer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.syncvideoviewer.presentation.RoomViewModel
import com.example.syncvideoviewer.ui.components.VideoPlayer
import com.example.syncvideoviewer.ui.components.ChatPanel
import com.example.syncvideoviewer.ui.components.ParticipantsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    roomId: String,
    onBack: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val room by viewModel.room.collectAsState()
    val participants by viewModel.participants.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    
    var showParticipants by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(roomId) {
        viewModel.joinRoom(roomId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.leaveRoom()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(room?.name ?: "Завантаження...")
                        SyncStatusIndicator(syncStatus)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.Close, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE50914)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF141414))
        ) {
            // Video Player
            VideoPlayer(
                videoUrl = room?.videoUrl ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                onPlayPause = { isPlaying ->
                    viewModel.sendPlayPauseCommand(isPlaying)
                },
                onSeek = { position ->
                    viewModel.sendSeekCommand(position)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chat and Participants
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Chat
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    ChatPanel(
                        messages = messages,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            placeholder = { Text("Повідомлення...") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFF404040),
                                focusedBorderColor = Color(0xFFE50914)
                            )
                        )

                        IconButton(
                            onClick = {
                                if (messageText.isNotEmpty()) {
                                    viewModel.sendMessage(messageText)
                                    messageText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE50914), shape = MaterialTheme.shapes.small)
                        ) {
                            Icon(
                                Icons.Filled.Send,
                                contentDescription = "Надіслати",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Participants list (for landscape)
                if (participants.isNotEmpty()) {
                    ParticipantsList(
                        participants = participants,
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
fun SyncStatusIndicator(status: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(
                color = when (status) {
                    "connected" -> Color.Green
                    "fallback" -> Color.Yellow
                    else -> Color.Red
                },
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            status,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
