package com.gemchat.app.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gemchat.app.ui.theme.*

data class DrawerItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

val drawerItems = listOf(
    DrawerItem(Icons.Default.Menu, label = "Conversations", route = "conversations"),
    DrawerItem(Icons.Default.Settings, "Settings",       "settings"),
    DrawerItem(Icons.Default.Info,     "About App",      "about"),
)

@Composable
fun AppDrawer(
    navController: NavController,
    currentRoute: String,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = SurfaceContainerLow
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Header z logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(listOf(PrimaryContainer, SecondaryContainer))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("💎", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "GemChat",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = PrimaryContainer
                )
                Text(
                    "Your private AI assistant",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.2f))

        Spacer(modifier = Modifier.height(16.dp))

        drawerItems.forEach { item ->
            val isActive = currentRoute == item.route

            NavigationDrawerItem(
                icon = {
                    Icon(item.icon, contentDescription = item.label,
                        tint = if (isActive) PrimaryContainer else OnSurfaceVariant)
                },
                label = {
                    Text(
                        item.label,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) OnSurface else OnSurfaceVariant
                    )
                },
                selected = isActive,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = PrimaryContainer.copy(alpha = 0.15f),
                    unselectedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
