package com.gemchat.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gemchat.app.ui.theme.*

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryContainer, SecondaryContainer)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("💎", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "GemChat",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create an account",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your email to sign up for this app",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text("email@domain.com", color = OnSurfaceVariant.copy(alpha = 0.5f))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceContainerLow,
                    unfocusedContainerColor = SurfaceContainerLow,
                    focusedBorderColor = PrimaryContainer,
                    unfocusedBorderColor = OutlineVariant.copy(alpha = 0.3f),
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Continue button
            Button(
                onClick = {
                    navController.navigate("conversations") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer
                )
            ) {
                Text(
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = OutlineVariant.copy(alpha = 0.3f)
                )
                Text(
                    text = "  or  ",
                    color = OnSurfaceVariant,
                    fontSize = 13.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = OutlineVariant.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue with Google
            OutlinedButton(
                onClick = {
                    navController.navigate("conversations") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, OutlineVariant.copy(alpha = 0.4f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceContainerLow
                )
            ) {
                Text("G", fontWeight = FontWeight.Bold, color = Color(0xFF4285F4), fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continue with Google", color = OnSurface, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Continue with Apple
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, OutlineVariant.copy(alpha = 0.4f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceContainerLow
                )
            ) {
                Text("🍎", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continue with Apple", color = OnSurface, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "By clicking continue, you agree to our Terms of Service and Privacy Policy",
                fontSize = 11.sp,
                color = OnSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
