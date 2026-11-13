package com.complux.ios18sim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.complux.ios18sim.data.SimulatorPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class HomeApp(
    val name: String,
    val symbol: String,
    val colors: List<Color>
)

private val homeApps = listOf(
    HomeApp("Fotos", "✿", listOf(Color(0xFF72D3DB), Color(0xFFFF9EA7))),
    HomeApp("Câmera", "◉", listOf(Color(0xFF43454B), Color(0xFF17191E))),
    HomeApp("Relógio", "◷", listOf(Color(0xFF252832), Color(0xFF090A0E))),
    HomeApp("Calendário", "17", listOf(Color.White, Color(0xFFE9EBF0))),
    HomeApp("Notas", "✎", listOf(Color(0xFFFFD65C), Color(0xFFFF9D38))),
    HomeApp("Calculadora", "＋", listOf(Color(0xFF414650), Color(0xFF191B20))),
    HomeApp("Clima", "☼", listOf(Color(0xFF75C7F4), Color(0xFF3478D5))),
    HomeApp("App Store", "A", listOf(Color(0xFF65B7F5), Color(0xFF2776D8))),
    HomeApp("Telefone", "☎", listOf(Color(0xFF55D76E), Color(0xFF169B42))),
    HomeApp("Mensagens", "●", listOf(Color(0xFF62E174), Color(0xFF18A648))),
    HomeApp("Safari", "◉", listOf(Color(0xFF6FD7EE), Color(0xFF2779E2))),
    HomeApp("Ajustes", "⚙", listOf(Color(0xFF9BA3AE), Color(0xFF565D68)))
)

@Composable
fun LockScreen(
    time: String,
    wallpaper: Int,
    darkMode: Boolean,
    onUnlock: () -> Unit,
    onCamera: () -> Unit
) {
    var flashlightOn by androidx.compose.runtime.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
    val date = remember { SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR")) }
    var dragDistance = 0f
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, amount ->
                        dragDistance += amount
                        change.consume()
                    },
                    onDragEnd = {
                        if (dragDistance < -55f) onUnlock()
                        dragDistance = 0f
                    }
                )
            }
    ) {
        IOSWallpaper(wallpaper, Modifier.fillMaxSize())
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            FakeStatusBar(time = time)
            Spacer(Modifier.height(42.dp))
            Text(
                date.format(Date()).replaceFirstChar { it.uppercase() },
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                time,
                color = Color.White,
                fontSize = 76.sp,
                lineHeight = 82.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-3).sp
            )
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LockWidget("PRÓXIMO EVENTO", "Agenda livre", Modifier.weight(1f))
                LockWidget("CLIMA", "22°  Parcialmente nublado", Modifier.weight(1.5f))
            }
            Spacer(Modifier.height(18.dp))
            FrostedPanel(modifier = Modifier.fillMaxWidth(), radius = 18.dp) {
                Text("Notificações", color = Color.White.copy(alpha = 0.92f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("Nenhuma notificação nova", color = Color.White.copy(alpha = 0.76f), fontSize = 13.sp)
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LockShortcut("ϟ", if (flashlightOn) "Lanterna simulada: ativa" else "Lanterna simulada") { flashlightOn = !flashlightOn }
                LockShortcut("◉", "Câmera", onCamera)
            }
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .width(132.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.88f))
                    .clickable(onClick = onUnlock)
            )
            Spacer(Modifier.height(12.dp))
            Text("Deslize para cima para abrir", color = Color.White.copy(alpha = 0.86f), fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
        }
        if (darkMode) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.12f)))
        }
    }
}

@Composable
private fun LockWidget(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Text(title, color = Color.White.copy(alpha = 0.78f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun LockShortcut(symbol: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.25f))
                .border(1.dp, Color.White.copy(alpha = 0.28f), CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(symbol, color = Color.White, fontSize = 23.sp)
        }
        Text(label, color = Color.White, fontSize = 10.sp)
    }
}

@Composable
fun HomeScreen(
    time: String,
    wallpaper: Int,
    page: Int,
    darkMode: Boolean,
    onPageChange: (Int) -> Unit,
    onOpenApp: (String) -> Unit,
    onOpenControlCenter: () -> Unit
) {
    val apps = if (page == 0) homeApps.take(8) else homeApps.drop(8)
    Box(Modifier.fillMaxSize()) {
        IOSWallpaper(wallpaper, Modifier.fillMaxSize())
        Column(
            modifier = Modifier.fillMaxSize()
                .pointerInput(page) {
                    var total = 0f
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, amount ->
                            total += amount
                            change.consume()
                        },
                        onDragEnd = {
                            if (total < -50f) onPageChange(1)
                            if (total > 50f) onPageChange(0)
                            total = 0f
                        }
                    )
                }
                .padding(horizontal = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(42.dp)
                    .pointerInput(Unit) {
                        var total = 0f
                        detectVerticalDragGestures(
                            onVerticalDrag = { change, amount -> total += amount; change.consume() },
                            onDragEnd = { if (total > 20f) onOpenControlCenter(); total = 0f }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(time, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("▮▮▮   ◔   ▰", color = Color.White, fontSize = 13.sp)
            }
            FrostedPanel(modifier = Modifier.fillMaxWidth(), radius = 23.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(SimpleDateFormat("EEEE", Locale("pt", "BR")).format(java.util.Date()).uppercase(Locale("pt", "BR")), color = Color.White.copy(alpha = 0.78f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Seu dia", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                        Text("Tudo em ordem por aqui.", color = Color.White.copy(alpha = 0.78f), fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("☀", color = Color.White, fontSize = 30.sp)
                        Text("24°", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f),
                userScrollEnabled = false,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                items(apps) { app ->
                    AppIcon(
                        title = app.name,
                        symbol = app.symbol,
                        colors = app.colors,
                        modifier = Modifier.fillMaxWidth()
                    ) { onOpenApp(app.name) }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { index ->
                    Box(
                        Modifier.padding(horizontal = 3.dp).size(if (index == page) 7.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (index == page) Color.White else Color.White.copy(alpha = 0.45f))
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(28.dp))
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIcon("Telefone", "☎", listOf(Color(0xFF55D76E), Color(0xFF169B42)), Modifier) { onOpenApp("Telefone") }
                AppIcon("Mensagens", "●", listOf(Color(0xFF62E174), Color(0xFF18A648)), Modifier) { onOpenApp("Mensagens") }
                AppIcon("Safari", "◉", listOf(Color(0xFF6FD7EE), Color(0xFF2779E2)), Modifier) { onOpenApp("Safari") }
                AppIcon("Ajustes", "⚙", listOf(Color(0xFF9BA3AE), Color(0xFF565D68)), Modifier) { onOpenApp("Ajustes") }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun ControlCenterScreen(
    settings: SimulatorPreferences.Snapshot,
    onSettingsChange: ((SimulatorPreferences.Snapshot) -> SimulatorPreferences.Snapshot) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF272B38), Color(0xFF12141D), Color(0xFF373044))))
            .pointerInput(Unit) {
                var total = 0f
                detectVerticalDragGestures(
                    onVerticalDrag = { change, amount -> total += amount; change.consume() },
                    onDragEnd = { if (total < -50f) onClose(); total = 0f }
                )
            }
            .padding(horizontal = 18.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Central de Controle", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text("Fechar  ×", color = Color.White, modifier = Modifier.clickable(onClick = onClose).padding(8.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ControlTile("Wi-Fi", "◔", settings.wifi && !settings.airplane) {
                        onSettingsChange { it.copy(wifi = !it.wifi, airplane = false) }
                    }
                    ControlTile("Bluetooth", "ᛒ", settings.bluetooth) {
                        onSettingsChange { it.copy(bluetooth = !it.bluetooth) }
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ControlTile("Modo avião", "✈", settings.airplane) {
                        onSettingsChange { it.copy(airplane = !it.airplane, wifi = false) }
                    }
                    ControlTile("Foco", "◉", settings.focus) {
                        onSettingsChange { it.copy(focus = !it.focus) }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            FrostedPanel(modifier = Modifier.fillMaxWidth(), radius = 24.dp) {
                Text("Brilho", color = Color.White, fontWeight = FontWeight.SemiBold)
                Slider(value = settings.brightness, onValueChange = { value -> onSettingsChange { it.copy(brightness = value) } })
                Text("Volume", color = Color.White, fontWeight = FontWeight.SemiBold)
                Slider(value = settings.volume, onValueChange = { value -> onSettingsChange { it.copy(volume = value) } })
            }
            Spacer(Modifier.height(12.dp))
            FrostedPanel(modifier = Modifier.fillMaxWidth(), radius = 24.dp) {
                Text("Em reprodução", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(listOf(Color(0xFF8A75BC), Color(0xFFE6A7B6)))))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Experiência iOS", color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Demonstração local", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                    Text("Ⅱ   ▶", color = Color.White, fontSize = 17.sp)
                }
            }
            Spacer(Modifier.weight(1f))
            Text("Deslize para cima para fechar", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 15.dp))
        }
    }
}

@Composable
private fun ControlTile(title: String, symbol: String, active: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().height(94.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (active) Color(0xFF2879E8) else Color.White.copy(alpha = 0.15f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(symbol, color = Color.White, fontSize = 23.sp)
        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
