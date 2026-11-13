package com.complux.ios18sim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.complux.ios18sim.data.SimulatorPreferences
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IOS18SimulatorApp() {
    val context = LocalContext.current
    val preferences = remember { SimulatorPreferences(context) }
    var settings by remember { mutableStateOf(preferences.read()) }
    var locked by rememberSaveable { mutableStateOf(true) }
    var selectedAppName by rememberSaveable { mutableStateOf<String?>(null) }
    var controlCenterVisible by rememberSaveable { mutableStateOf(false) }
    var homePage by rememberSaveable { mutableIntStateOf(0) }
    var settingsCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var noteText by rememberSaveable { mutableStateOf(settings.note) }
    var time by remember { mutableStateOf(currentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            time = currentTime()
            delay(15_000)
        }
    }

    fun updateSettings(transform: (SimulatorPreferences.Snapshot) -> SimulatorPreferences.Snapshot) {
        settings = transform(settings)
        preferences.write(settings)
    }

    BackHandler(enabled = controlCenterVisible || selectedAppName != null || !locked) {
        when {
            controlCenterVisible -> controlCenterVisible = false
            selectedAppName != null -> {
                selectedAppName = null
                settingsCategory = null
            }
            !locked -> locked = true
        }
    }

    val scheme = if (settings.darkMode) darkColorScheme(
        primary = Color(0xFF6B9EFF),
        background = Color(0xFF101116),
        surface = Color(0xFF1B1D24),
        onBackground = Color(0xFFF5F5F7),
        onSurface = Color(0xFFF5F5F7)
    ) else lightColorScheme(
        primary = Color(0xFF2775E8),
        background = Color(0xFFF4F5F9),
        surface = Color.White,
        onBackground = Color(0xFF191A20),
        onSurface = Color(0xFF191A20)
    )

    MaterialTheme(colorScheme = scheme) {
        CompositionLocalProvider(
            LocalGlassLab provides settings.glassLab,
            LocalPerformanceProfile provides settings.performance
        ) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            Crossfade(
                targetState = when {
                    locked -> "lock"
                    controlCenterVisible -> "control"
                    selectedAppName != null -> "app:${selectedAppName}"
                    else -> "home"
                },
                animationSpec = tween(
                    durationMillis = if (settings.performance == "Low Resource") 120 else 230,
                    easing = FastOutSlowInEasing
                ),
                label = "simulator-screen-transition"
            ) { route ->
                when {
                    route == "lock" -> LockScreen(
                        time = time,
                        wallpaper = settings.wallpaper,
                        darkMode = settings.darkMode,
                        onUnlock = { locked = false },
                        onCamera = { locked = false; selectedAppName = "Câmera" }
                    )
                    route == "control" -> ControlCenterScreen(
                        settings = settings,
                        onSettingsChange = ::updateSettings,
                        onClose = { controlCenterVisible = false }
                    )
                    route.startsWith("app:") -> {
                        val appName = selectedAppName ?: "Settings"
                        AppScreenHost(
                            appName = appName,
                            time = time,
                            settings = settings,
                            onSettingsChange = ::updateSettings,
                            settingsCategory = settingsCategory,
                            onSettingsCategory = { settingsCategory = it },
                            noteText = noteText,
                            onNoteChange = { noteText = it; updateSettings { old -> old.copy(note = it) } },
                            onBack = { selectedAppName = null; settingsCategory = null },
                            onOpenApp = { selectedAppName = it }
                        )
                    }
                    else -> HomeScreen(
                        time = time,
                        wallpaper = settings.wallpaper,
                        page = homePage,
                        darkMode = settings.darkMode,
                        onPageChange = { homePage = it.coerceIn(0, 1) },
                        onOpenApp = { selectedAppName = it },
                        onOpenControlCenter = { controlCenterVisible = true }
                    )
                }
            }
        }
        }
    }
}

private fun currentTime(): String = SimpleDateFormat("H:mm", Locale.getDefault()).format(Date())
