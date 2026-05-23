package com.gemchat.app.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gemchat.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        containerColor = SurfaceContainerLowest,
        topBar = {
            TopAppBar(
                title = { Text("About GemChat", color = OnSurface, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz", tint = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLowest)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(PrimaryContainer, SecondaryContainer))),
                contentAlignment = Alignment.Center
            ) {
                Text("💎", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("GemChat", fontSize = 32.sp, fontWeight = FontWeight.Black, color = OnSurface)
            Text("Version 1.0.0", fontSize = 13.sp, color = OnSurfaceVariant)

            Spacer(modifier = Modifier.height(24.dp))

            // Opis
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Text(
                    text = "GemChat is a mobile AI assistant designed with simplicity and privacy in mind. The app combines the power of Google's Gemini language model with an intuitive interface, allowing you to have natural text conversations and analyze images directly from your smartphone.\n\nAll conversations are stored locally on your device — your chat history never leaves your phone without your consent.",
                    modifier = Modifier.padding(20.dp),
                    color = OnSurface,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Technologies
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Technologies", fontWeight = FontWeight.Bold, color = PrimaryContainer, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf("Kotlin", "Jetpack Compose", "Room Database", "Gemini API", "MVVM Architecture").forEach {
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("•  ", color = PrimaryContainer, fontWeight = FontWeight.Bold)
                            Text(it, color = OnSurface, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Authors
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Authors", fontWeight = FontWeight.Bold, color = PrimaryContainer, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Filip Wójcik & Igor Adamowicz", color = OnSurface, fontSize = 14.sp)
                    Text("Index: 279531 & 279397", color = OnSurfaceVariant, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stupid video
            Text("Top secret developer footage", fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            VideoPlayer(videoUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.Builder()
                .setUri(videoUrl)
                .build()
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            volume = 0.5f
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Zwiększona wysokość
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black)
            .border(1.dp, PrimaryContainer, RoundedCornerShape(20.dp))
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    setBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            update = { view ->
                view.player = exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
