package com.gemchat.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gemchat.app.GemChatApplication
import com.gemchat.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("gemchat", android.content.Context.MODE_PRIVATE)
    val repository = (context.applicationContext as GemChatApplication).repository
    val scope = rememberCoroutineScope()

    var selectedModel by remember {
        mutableStateOf(prefs.getString("model", "gemini-flash-latest") ?: "gemini-flash-latest")
    }
    var darkMode by remember {
        mutableStateOf(prefs.getBoolean("dark_mode", true))
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeletedSnackbar by remember { mutableStateOf(false) }

    val models = listOf(
        "gemini-flash-latest" to "Gemini Flash (domyślny)",
        "gemini-2.0-flash" to "Gemini 2.0 Flash",
        "gemini-1.5-flash" to "Gemini 1.5 Flash"
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Wyczyść historię", color = OnSurface) },
            text = { Text("Czy na pewno chcesz usunąć wszystkie konwersacje? Tej operacji nie można cofnąć.", color = OnSurfaceVariant) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.deleteAllData()
                            showDeleteDialog = false
                            showDeletedSnackbar = true
                        }
                    }
                ) {
                    Text("Usuń", color = Color(0xFFE24B4A))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anuluj", color = OnSurfaceVariant)
                }
            },
            containerColor = SurfaceContainer
        )
    }

    Scaffold(
        containerColor = SurfaceContainerLowest,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = OnSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz", tint = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLowest)
            )
        },
        snackbarHost = {
            if (showDeletedSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { showDeletedSnackbar = false }) {
                            Text("OK", color = PrimaryContainer)
                        }
                    },
                    containerColor = SurfaceContainerHigh
                ) {
                    Text("Historia czatów wyczyszczona", color = OnSurface)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Model AI
            SettingsSection(title = "Model AI") {
                models.forEach { (modelId, modelName) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(modelName, color = OnSurface, fontSize = 14.sp)
                        RadioButton(
                            selected = selectedModel == modelId,
                            onClick = {
                                selectedModel = modelId
                                prefs.edit().putString("model", modelId).apply()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryContainer)
                        )
                    }
                }
            }

            // Wygląd
            SettingsSection(title = "Wygląd") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Ciemny motyw", color = OnSurface, fontSize = 14.sp)
                        Text(
                            if (darkMode) "Włączony" else "Wyłączony",
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = darkMode,
                        onCheckedChange = {
                            darkMode = it
                            prefs.edit().putBoolean("dark_mode", it).apply()
                            (context as android.app.Activity).recreate()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryContainer
                        )
                    )
                }
            }

            // Dane
            SettingsSection(title = "Dane") {
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B1A1A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Wyczyść historię czatów", color = Color.White)
                }
            }

            // O aplikacji
            SettingsSection(title = "O aplikacji") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Wersja", color = OnSurfaceVariant, fontSize = 14.sp)
                    Text("1.0.0", color = OnSurface, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Aktywny model", color = OnSurfaceVariant, fontSize = 14.sp)
                    Text(selectedModel, color = OnSurface, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = PrimaryContainer,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}