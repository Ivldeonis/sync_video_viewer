package com.example.syncvideoviewer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.syncvideoviewer.data.model.ChatMessage
import com.example.syncvideoviewer.data.model.Participant

@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    onPlayPause: (Boolean) -> Unit = {},
    onSeek: (Long) -> Unit = {}
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0L) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for ExoPlayer integration
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Відео: $videoUrl",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    IconButton(
                        onClick = {
                            isPlaying = !isPlaying
                            onPlayPause(isPlaying)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE50914), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Пауза" else "Відтворення",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { onSeek(currentPosition) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE50914), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            Icons.Filled.VolumeUp,
                            contentDescription = "Гучність",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatPanel(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF221A1F)
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatMessageItem(message)
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF141414), shape = MaterialTheme.shapes.small)
            .padding(8.dp)
    ) {
        Text(
            message.userName,
            color = Color(0xFFE50914),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            message.text,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ParticipantsList(
    participants: List<Participant>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF221A1F)
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(participants) { participant ->
                ParticipantItem(participant)
            }
        }
    }
}

@Composable
fun ParticipantItem(participant: Participant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF141414), shape = MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = MaterialTheme.shapes.small,
            color = Color(0xFFE50914)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    participant.name.firstOrNull()?.toString() ?: "?",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                participant.name,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
            if (participant.isHost) {
                Text(
                    "Лідер",
                    color = Color(0xFFE50914),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
