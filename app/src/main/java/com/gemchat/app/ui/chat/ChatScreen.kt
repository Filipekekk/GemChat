package com.gemchat.app.ui.chat
import com.gemchat.app.data.repository.GeminiRepository
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.gemchat.app.GemChatApplication
import com.gemchat.app.data.model.Message
import com.gemchat.app.data.repository.ChatRepository
import com.gemchat.app.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
// ViewModel
class ChatViewModel(
    private val repository: ChatRepository,
    private val conversationId: Long
) : ViewModel() {

    private val geminiRepository = GeminiRepository()

    val messages = if (conversationId > 0)
        repository.getMessagesForConversation(conversationId)
    else
        MutableStateFlow(emptyList<Message>()).asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private fun saveImageLocally(context: android.content.Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sendMessage(text: String, imageUri: Uri? = null, context: android.content.Context? = null) {
        if (conversationId <= 0) return
        viewModelScope.launch {
            var imagePath: String? = null
            var imageBase64: String? = null
            var mimeType: String? = null

            if (imageUri != null && context != null) {
                mimeType = context.contentResolver.getType(imageUri)
                context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    imageBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                }
                // Zapisujemy kopię lokalną, aby była dostępna po restarcie
                imagePath = saveImageLocally(context, imageUri)
            }

            // Jeśli to pierwsza wiadomość — zaktualizuj tytuł
            val currentMessages = repository.getMessagesForConversation(conversationId).first()
            if (currentMessages.isEmpty()) {
                val title = if (text.length > 30) text.take(30) + "..." else text
                repository.updateConversationTitle(conversationId, title)
            }

            repository.insertMessage(conversationId, text, true, imagePath)
            repository.updateLastMessage(conversationId, if (imagePath != null) "Sent an image" else text)

            _isLoading.value = true
            val response = geminiRepository.sendMessage(text, imageBase64, mimeType)
            repository.insertMessage(conversationId, response, false)
            repository.updateLastMessage(conversationId, response)
            _isLoading.value = false
        }
    }
}

class ChatViewModelFactory(
    private val repository: ChatRepository,
    private val conversationId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ChatViewModel(repository, conversationId) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    conversationId: String?,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as GemChatApplication).repository
    val convId = conversationId?.toLongOrNull() ?: 0L

    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(repository, convId)
    )

    val messages by viewModel.messages.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = SurfaceContainerLowest,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "GemChat",
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
                    if (messageText.isNotBlank() || selectedImageUri != null) {
                        viewModel.sendMessage(messageText, selectedImageUri, context)
                        messageText = ""
                        selectedImageUri = null
                    }
                },
                onImagePick = { imagePickerLauncher.launch("image/*") },
                selectedImageUri = selectedImageUri,
                onClearImage = { selectedImageUri = null }
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
            if (isLoading) {
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
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(PrimaryContainer, SecondaryContainer))),
                contentAlignment = Alignment.Center
            ) { Text("💎", fontSize = 14.sp) }
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
                            topStart = 24.dp, topEnd = 24.dp,
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
                Column {
                    if (message.localImagePath != null) {
                        AsyncImage(
                            model = message.localImagePath,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .padding(bottom = 8.dp)
                        )
                    }
                    if (message.text.isNotEmpty()) {
                        Text(text = message.text, color = OnSurface, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(message.timestamp)),
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
    onImagePick: () -> Unit,
    selectedImageUri: Uri? = null,
    onClearImage: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest.copy(alpha = 0.9f))
    ) {
        if (selectedImageUri != null) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onClearImage,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onImagePick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerLow)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Wyślij zdjęcie",
                    tint = OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

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
}
