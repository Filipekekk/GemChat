package com.gemchat.app.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gemchat.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically

@Composable
fun SplashScreen(navController: NavController) {

    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.5f) }
    val rotation = remember { Animatable(0f) }

    // Pulsowanie logo po załadowaniu
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    // Progress bar animacja
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1800),
        label = "progress"
    )

    var showStartButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val a1 = launch {
            alpha.animateTo(1f, animationSpec = tween(1000))
        }
        val a2 = launch {
            scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        val a3 = launch {
            rotation.animateTo(360f, animationSpec = tween(1500, easing = EaseInOutCubic))
        }
        progress = 1f
        
        // Czekamy na zakończenie animacji i paska postępu
        a1.join()
        a2.join()
        a3.join()
        delay(800) 
        showStartButton = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceContainerLowest),
        contentAlignment = Alignment.Center
    ) {
        // Ambient glow w tle
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.Center)
                .alpha(glowAlpha * 0.15f)
                .blur(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PrimaryContainer, Color.Transparent)
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .offset(y = floatingOffset.dp)
        ) {
            // Logo placeholder — zastąp Image(painterResource(R.drawable.logo)) gdy dodasz plik
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(
                        alpha = alpha.value,
                        scaleX = scale.value,
                        scaleY = scale.value,
                        rotationY = rotation.value
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryContainer, SecondaryContainer)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("💎", fontSize = 56.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "GemChat",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your private AI assistant",
                fontSize = 16.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            AnimatedVisibility(
                visible = !showStartButton,
                exit = androidx.compose.animation.fadeOut()
            ) {
                // Shimmer progress bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .width(200.dp)
                        .height(2.dp),
                    color = PrimaryContainer,
                    trackColor = SurfaceContainerHigh
                )
            }

            AnimatedVisibility(
                visible = showStartButton,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                ) {
                    Text(
                        "Get Started",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
