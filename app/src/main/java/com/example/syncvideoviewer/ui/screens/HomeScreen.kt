package com.example.syncvideoviewer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncvideoviewer.data.model.Room

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRoomClick: (String) -> Unit,
    onCreateRoom: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val dummyRooms = listOf(
        Room(id = "1", name = "Movie Night", videoUrl = "", isPrivate = false),
        Room(id = "2", name = "Anime Club", videoUrl = "", isPrivate = true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SyncVideoViewer", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFFE50914),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Room")
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SearchBox(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dummyRooms.filter { it.name.contains(searchQuery, ignoreCase = true) }) { room ->
                    RoomCard(room = room, onClick = { onRoomClick(room.id) })
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateRoomDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, url, isPrivate, code ->
                // Handle room creation
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Пошук кімнат...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF221A1F),
            unfocusedContainerColor = Color(0xFF221A1F),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun RoomCard(
    room: Room,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF221A1F)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(room.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("Учасників: 0", color = Color.Gray, fontSize = 14.sp)
            }
            if (room.isPrivate) {
                Icon(Icons.Default.Lock, contentDescription = "Private", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Boolean, String) -> Unit
) {
    var roomName by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var accessCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Створити нову кімнату") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Назва кімнати") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("URL відео (HLS)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPrivate, onCheckedChange = { isPrivate = it })
                    Text("Приватна кімната")
                }
                if (isPrivate) {
                    OutlinedTextField(
                        value = accessCode,
                        onValueChange = { accessCode = it },
                        label = { Text("Код доступу") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (roomName.isNotEmpty() && videoUrl.isNotEmpty()) {
                    onCreate(roomName, videoUrl, isPrivate, if (isPrivate) accessCode else "")
                }
            }) {
                Text("Створити")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}
