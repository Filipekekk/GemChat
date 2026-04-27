package com.gemchat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gemchat.app.ui.about.AboutScreen
import com.gemchat.app.ui.chat.ChatScreen
import com.gemchat.app.ui.conversations.ConversationsScreen
import com.gemchat.app.ui.drawer.AppDrawer
import com.gemchat.app.ui.login.LoginScreen
import com.gemchat.app.ui.splash.SplashScreen
import com.gemchat.app.ui.theme.GemChatTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.gemchat.app.ui.theme.SurfaceContainerLowest
import com.gemchat.app.ui.theme.OnSurface
import com.gemchat.app.ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("gemchat", android.content.Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", true)
        android.util.Log.d("GemChat", "isDark = $isDark")
        setContent {
            GemChatTheme(darkTheme = isDark) {
                GemChatApp()
            }
        }
    }
}

@Composable
fun GemChatApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute by remember {
        derivedStateOf {
            navController.currentBackStackEntry?.destination?.route ?: ""
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                currentRoute = currentRoute,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        MainNavHost(navController, scope, drawerState)
    }
}

@Composable
fun MainNavHost(
    navController: androidx.navigation.NavHostController,
    scope: kotlinx.coroutines.CoroutineScope,
    drawerState: androidx.compose.material3.DrawerState
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("conversations") {
            ConversationsScreen(
                navController = navController,
                onOpenDrawer = { scope.launch { drawerState.open() } }
            )
        }
        composable("chat/{conversationId}") { backStack ->
            ChatScreen(
                navController = navController,
                conversationId = backStack.arguments?.getString("conversationId"),
                onOpenDrawer = { scope.launch { drawerState.open() } }
            )
        }
        composable("about") { AboutScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}