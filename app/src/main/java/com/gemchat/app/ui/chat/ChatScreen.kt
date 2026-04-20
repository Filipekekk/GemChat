package com.gemchat.app.ui.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gemchat.app.ui.theme.*
import kotlinx.coroutines.launch

data class Message(
    val id: Long,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String,
    val imageUri: String? = null
)

val sampleMessages = listOf(
    Message(1, "Analyze the architectural structure of a modern vault", true, "14:30"),
    Message(2, "I've analyzed the blueprints for the neo-modernist vault and identified three key structural elements: reinforced composite walls using a carbon-fiber matrix, a biometric access grid embedded within the entrance facade, and an electromagnetic pulse shield integrated into the ceiling panels.", false, "14:30"),
    Message(3, "What about the security systems?", true, "14:31"),
    Message(4, "The security system operates on three independent layers. The outer perimeter uses quantum-encrypted motion sensors, the middle layer deploys a neural-network camera array for facial recognition, and the inner sanctum relies on DNA-signature verification combined with real-time behavioral analysis.", false, "14:31"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    conversationId: String?,
    onOpenDrawer: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(*sampleMessages.toTypedArray()) }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = SurfaceContainerLowest,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Architectural Analysis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = OnSurface
                        )
                        Text(
                            "Gemini 2.0 Flash",
                            fontSize = 11.sp,
                            color = OnSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = OnSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                value = messageText,
                onValueChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        val userMsg = Message(
                            id = messages.size.toLong() + 1,
                            text = messageText,
                            isFromUser = true,
                            timestamp = "teraz"
                        )
                        messages.add(userMsg)
                        messageText = ""
                        isTyping = true
                        scope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                            // Tutaj wywołasz GeminiRepository.sendMessage()
                            // Po otrzymaniu odpowiedzi: isTyping = false, messages.add(aiMsg)
                        }
                    }
                },
                onImagePick = {
                    // Tutaj odpal imagePickerLauncher.launch("image/*")
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }

            if (isTyping) {
                item { TypingIndicator() }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            // Avatar AI
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(PrimaryContainer, SecondaryContainer))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("💎", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomStart = if (message.isFromUser) 24.dp else 4.dp,
                            bottomEnd = if (message.isFromUser) 4.dp else 24.dp
                        )
                    )
                    .background(
                        brush = if (message.isFromUser)
                            Brush.linearGradient(listOf(SurfaceContainerHigh, SurfaceContainerHigh))
                        else
                            Brush.linearGradient(listOf(PrimaryContainer, SecondaryContainer))
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    color = OnSurface,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message.timestamp,
                fontSize = 10.sp,
                color = OnSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dot1 by infiniteTransition.animateFloat(
        0.3f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "d1"
    )
    val dot2 by infiniteTransition.animateFloat(
        0.3f, 1f, infiniteRepeatable(tween(600, delayMillis = 150), RepeatMode.Reverse), label = "d2"
    )
    val dot3 by infiniteTransition.animateFloat(
        0.3f, 1f, infiniteRepeatable(tween(600, delayMillis = 300), RepeatMode.Reverse), label = "d3"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(PrimaryContainer, SecondaryContainer))),
            contentAlignment = Alignment.Center
        ) { Text("💎", fontSize = 14.sp) }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp, 24.dp, 24.dp, 4.dp))
                .background(SurfaceContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(dot1, dot2, dot3).forEach { alpha ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer.copy(alpha = alpha))
                    )
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onImagePick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image picker button
        IconButton(
            onClick = onImagePick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SurfaceContainerLow)
        ) {
            Icon(Icons.Default.Star, contentDescription = "Wyślij zdjęcie", tint = OnSurfaceVariant)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Text input
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Inquire the intelligence...", color = OnSurfaceVariant.copy(alpha = 0.5f), fontSize = 14.sp)
            },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(50.dp))
                .background(SurfaceContainerLow),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface
            ),
            singleLine = false,
            maxLines = 4
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Send button
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryContainer)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Wyślij",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
