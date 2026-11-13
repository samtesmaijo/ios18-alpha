package com.complux.ios18sim.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.complux.ios18sim.data.SimulatorPreferences
import com.complux.ios18sim.data.calculateBasic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppScreenHost(
    appName: String,
    time: String,
    settings: SimulatorPreferences.Snapshot,
    onSettingsChange: ((SimulatorPreferences.Snapshot) -> SimulatorPreferences.Snapshot) -> Unit,
    settingsCategory: String?,
    onSettingsCategory: (String?) -> Unit,
    noteText: String,
    onNoteChange: (String) -> Unit,
    onBack: () -> Unit,
    onOpenApp: (String) -> Unit
) {
    when (appName) {
        "Ajustes" -> SettingsScreen(
            settings = settings,
            onSettingsChange = onSettingsChange,
            category = settingsCategory,
            onCategory = onSettingsCategory,
            onBack = onBack
        )
        "Fotos" -> PhotosScreen(
            savedPhotoUris = settings.photoUris,
            onPhotoUrisChange = { updated -> onSettingsChange { current -> current.copy(photoUris = updated) } },
            onBack = onBack
        )
        "Câmera" -> CameraScreen(onBack = onBack, onOpenPhotos = { onOpenApp("Fotos") })
        "Telefone" -> PhoneScreen(onBack = onBack)
        "Mensagens" -> MessagesScreen(onBack = onBack)
        "Safari" -> SafariScreen(onBack = onBack)
        "Relógio" -> SimpleClockScreen(time = time, onBack = onBack)
        "Calendário" -> CalendarScreen(onBack = onBack)
        "Notas" -> NotesScreen(noteText = noteText, onNoteChange = onNoteChange, onBack = onBack)
        "Calculadora" -> CalculatorScreen(onBack = onBack)
        "Clima" -> WeatherScreen(onBack = onBack)
        "App Store" -> StoreScreen(onBack = onBack)
        else -> PlaceholderScreen(appName, onBack)
    }
}

@Composable
private fun AppPage(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        ScreenTitle(title = title, onBack = onBack)
        content()
    }
}

@Composable
private fun SettingsScreen(
    settings: SimulatorPreferences.Snapshot,
    onSettingsChange: ((SimulatorPreferences.Snapshot) -> SimulatorPreferences.Snapshot) -> Unit,
    category: String?,
    onCategory: (String?) -> Unit,
    onBack: () -> Unit
) {
    val entries = listOf(
        "Wi-Fi" to "◔",
        "Bluetooth" to "ᛒ",
        "Notificações" to "▤",
        "Geral" to "⚙",
        "Tela e Brilho" to "☼",
        "Papel de Parede" to "▧",
        "Acessibilidade" to "◎",
        "Privacidade e Segurança" to "▣",
        "Bateria" to "▰",
        "Experimental Glass Lab" to "◇",
        "Sobre" to "ⓘ"
    )

    if (category != null) {
        AppPage(title = category, onBack = { onCategory(null) }) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (category) {
                    "Wi-Fi" -> SettingSwitchRow("Wi-Fi simulado", settings.wifi, "Este controle altera apenas o ambiente simulado.") {
                        onSettingsChange { it.copy(wifi = !it.wifi, airplane = false) }
                    }
                    "Bluetooth" -> SettingSwitchRow("Bluetooth simulado", settings.bluetooth, "Não modifica o Bluetooth real do Android.") {
                        onSettingsChange { it.copy(bluetooth = !it.bluetooth) }
                    }
                    "Notificações" -> InfoCard("Central de notificações", "As notificações desta versão são demonstrativas. A integração com notificações reais do Android não está ativada.")
                    "Geral" -> {
                        InfoCard("Sobre o simulador", "Versão 0.1.0-alpha\nAmbiente independente inspirado no iOS 18.")
                        SettingSwitchRow("Modo escuro", settings.darkMode, "Altera as telas internas do simulador.") {
                            onSettingsChange { it.copy(darkMode = !it.darkMode) }
                        }
                        Text("Perfil de desempenho", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                        listOf("High Quality", "Balanced", "Low Resource").forEach { profile ->
                            ChoiceRow(profile, settings.performance == profile) {
                                onSettingsChange { it.copy(performance = profile) }
                            }
                        }
                    }
                    "Tela e Brilho" -> {
                        SettingSwitchRow("Aparência escura", settings.darkMode, "Aplica o tema escuro às telas compatíveis.") {
                            onSettingsChange { it.copy(darkMode = !it.darkMode) }
                        }
                        Text("Brilho simulado", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                        Slider(value = settings.brightness, onValueChange = { value -> onSettingsChange { it.copy(brightness = value) } })
                        InfoCard("Atenção", "O controle de brilho modifica apenas a interface simulada, não o brilho físico do Galaxy A10.")
                    }
                    "Papel de Parede" -> {
                        Text("Escolha um papel de parede", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                        (0..4).forEach { index ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(if (settings.wallpaper == index) 2.dp else 1.dp, if (settings.wallpaper == index) Blue else Color.LightGray.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                    .clickable { onSettingsChange { it.copy(wallpaper = index) } }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.size(74.dp, 94.dp).clip(RoundedCornerShape(13.dp))) {
                                    IOSWallpaper(index, Modifier.fillMaxSize())
                                }
                                Spacer(Modifier.width(14.dp))
                                Text("Papel de parede ${index + 1}", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                Text(if (settings.wallpaper == index) "✓" else "", color = Blue, fontSize = 20.sp)
                            }
                        }
                    }
                    "Acessibilidade" -> InfoCard("Acessibilidade", "Preferências avançadas de acessibilidade serão adicionadas em uma próxima versão. O botão Voltar do Android continua disponível.")
                    "Privacidade e Segurança" -> InfoCard("Privacidade", "As configurações do simulador ficam armazenadas localmente. O aplicativo não solicita Apple ID, root ou permissões de acessibilidade.")
                    "Bateria" -> InfoCard("Bateria", "Esta tela é demonstrativa e não representa uma leitura precisa da bateria do dispositivo.")
                    "Experimental Glass Lab" -> {
                        SettingSwitchRow("Ativar laboratório visual", settings.glassLab, "Permite experimentar superfícies translúcidas. Não substitui o visual padrão do iOS 18.") {
                            onSettingsChange { it.copy(glassLab = !it.glassLab) }
                        }
                        InfoCard("Compatibilidade", "O Android 11 não oferece RenderEffect. Esta versão usa transparência e gradientes leves, sem afirmar que aplica desfoque óptico real.")
                    }
                    else -> InfoCard("Sobre este dispositivo", "iOS 18 Experience Simulator\nVersão 0.1.0-alpha\nAplicativo independente para Android.")
                }
                Spacer(Modifier.height(22.dp))
            }
        }
    } else {
        AppPage(title = "Ajustes", onBack = onBack) {
            Column(Modifier.fillMaxSize()) {
                var query by rememberSaveable { mutableStateOf("") }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp),
                    singleLine = true,
                    placeholder = { Text("Buscar") },
                    shape = RoundedCornerShape(14.dp)
                )
                LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(entries.filter { it.first.contains(query, ignoreCase = true) }) { (name, symbol) ->
                        SettingsEntry(name, symbol) { onCategory(name) }
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SettingsEntry(title: String, symbol: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick).padding(horizontal = 15.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(31.dp).clip(RoundedCornerShape(9.dp)).background(Brush.linearGradient(listOf(Color(0xFF76A8F6), Color(0xFF3B6FC6)))), contentAlignment = Alignment.Center) {
            Text(symbol, color = Color.White, fontSize = 17.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), fontSize = 15.sp)
        Text("›", color = Muted, fontSize = 24.sp)
    }
}

@Composable
private fun SettingSwitchRow(title: String, checked: Boolean, description: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(15.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            Switch(checked = checked, onCheckedChange = { onClick() })
        }
        Text(description, color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun ChoiceRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).clickable(onClick = onClick).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text(if (selected) "✓" else "", color = Blue, fontSize = 19.sp)
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(17.dp)).background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
        Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(5.dp))
        Text(body, color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun PhotosScreen(
    savedPhotoUris: String,
    onPhotoUrisChange: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedDemo by rememberSaveable { mutableIntStateOf(-1) }
    var selectedPhotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    val importedUris = remember(savedPhotoUris) {
        savedPhotoUris.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.distinct().take(30).toList()
    }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(20)
    ) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            val merged = (importedUris + uris.map { it.toString() }).distinct().take(30)
            onPhotoUrisChange(merged.joinToString("\n"))
        }
    }
    val colors = listOf(
        listOf(Color(0xFFEDB6A0), Color(0xFF8C6A8A)),
        listOf(Color(0xFF7DB7C5), Color(0xFF294E76)),
        listOf(Color(0xFFB6C4A1), Color(0xFF52736A)),
        listOf(Color(0xFFE9C47B), Color(0xFFD47E65)),
        listOf(Color(0xFF9CAEE5), Color(0xFF514D7A)),
        listOf(Color(0xFFE2A3B8), Color(0xFF8A547B)),
        listOf(Color(0xFFB7D6D4), Color(0xFF507F82)),
        listOf(Color(0xFFC1A0D7), Color(0xFF654F90))
    )
    AppPage("Fotos", onBack) {
        when {
            selectedPhotoUri != null -> {
                Box(Modifier.fillMaxSize().background(Color.Black)) {
                    ImportedPhoto(uri = Uri.parse(selectedPhotoUri!!), modifier = Modifier.fillMaxSize())
                    Text("‹", color = Color.White, fontSize = 34.sp, modifier = Modifier.align(Alignment.TopStart).clickable { selectedPhotoUri = null }.padding(20.dp))
                    Text("Foto do dispositivo", color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp), fontSize = 13.sp)
                }
            }
            selectedDemo >= 0 -> {
                Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                    Box(Modifier.fillMaxWidth().fillMaxHeight(0.72f).background(Brush.linearGradient(colors[selectedDemo % colors.size])))
                    Text("‹", color = Color.White, fontSize = 34.sp, modifier = Modifier.align(Alignment.TopStart).clickable { selectedDemo = -1 }.padding(20.dp))
                    Text("Imagem de demonstração", color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp), fontSize = 13.sp)
                }
            }
            else -> {
                Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Biblioteca", color = MaterialTheme.colorScheme.onBackground, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                            Text("${importedUris.size + 8} itens", color = Muted, fontSize = 12.sp)
                        }
                        Text(
                            "Adicionar",
                            color = Blue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clip(RoundedCornerShape(18.dp)).clickable {
                                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }.padding(10.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(importedUris, key = { it }) { uriString ->
                            Box(Modifier.fillMaxWidth().aspectRatio(0.88f).clip(RoundedCornerShape(8.dp)).clickable { selectedPhotoUri = uriString }) {
                                ImportedPhoto(uri = Uri.parse(uriString), modifier = Modifier.fillMaxSize())
                            }
                        }
                        items((0..7).toList()) { index ->
                            Box(Modifier.fillMaxWidth().aspectRatio(0.88f).clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(colors[index])).clickable { selectedDemo = index }) {
                                Text("${index + 1}", color = Color.White.copy(alpha = 0.82f), modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp), fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Álbuns", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                    Text("Favoritos     Recentes     Lugares", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun ImportedPhoto(uri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                val sample = calculateInSampleSize(bounds, 512, 512)
                val options = BitmapFactory.Options().apply { inSampleSize = sample.coerceAtLeast(1) }
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
            }.getOrNull()
        }
    }
    if (bitmap != null) {
        Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = "Foto importada", modifier = modifier, contentScale = androidx.compose.ui.layout.ContentScale.Crop)
    } else {
        Box(modifier.background(Color(0xFF343945)), contentAlignment = Alignment.Center) {
            Text("Foto", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        }
    }
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val width = options.outWidth
    val height = options.outHeight
    if (width <= 0 || height <= 0) return 1
    var sample = 1
    while (width / sample > reqWidth || height / sample > reqHeight) {
        sample *= 2
    }
    return sample
}

@Composable
private fun CameraScreen(onBack: () -> Unit, onOpenPhotos: () -> Unit) {
    var captured by remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap -> captured = bitmap }
    Column(Modifier.fillMaxSize().background(Color(0xFF090A0D))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = Color.White, fontSize = 34.sp, modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(12.dp))
            Text("Câmera", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text("Fotos", color = Color.White, modifier = Modifier.clickable(onClick = onOpenPhotos))
        }
        Box(Modifier.weight(1f).fillMaxWidth().padding(14.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xFF20232B)), contentAlignment = Alignment.Center) {
            if (captured != null) {
                androidx.compose.foundation.Image(bitmap = captured!!.asImageBitmap(), contentDescription = "Foto capturada", modifier = Modifier.fillMaxSize())
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("◉", color = Color.White.copy(alpha = 0.7f), fontSize = 64.sp)
                    Text("A câmera será aberta pelo Android", color = Color.White, fontSize = 13.sp)
                    Text("A captura retornará para esta tela.", color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("1×", color = Color.White, fontSize = 14.sp)
            Box(Modifier.size(72.dp).clip(CircleShape).border(5.dp, Color.White, CircleShape).padding(6.dp).clip(CircleShape).background(Color.White).clickable { launcher.launch(null) })
            Text("HDR", color = Color.White, fontSize = 14.sp)
        }
        Text("Foto e vídeo são capturados pelo aplicativo de câmera do Android nesta versão.", color = Color.White.copy(alpha = 0.62f), fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp, start = 18.dp, end = 18.dp))
    }
}

@Composable
private fun PhoneScreen(onBack: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf("Favoritos") }
    var dial by rememberSaveable { mutableStateOf("") }
    var calling by rememberSaveable { mutableStateOf<String?>(null) }
    val contacts = listOf("Mariana Costa", "Lucas Almeida", "Ana Beatriz", "Rafael Souza")
    AppPage("Telefone", onBack) {
        if (calling != null) {
            Column(Modifier.fillMaxSize().background(Color(0xFF101914)).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(calling!!, color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.SemiBold)
                Text("Chamada simulada", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(top = 8.dp))
                Spacer(Modifier.height(55.dp))
                Box(Modifier.size(66.dp).clip(CircleShape).background(Color(0xFFE74A4A)).clickable { calling = null }, contentAlignment = Alignment.Center) { Text("×", color = Color.White, fontSize = 34.sp) }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Favoritos", "Recentes", "Contatos", "Teclado").forEach { item ->
                        Text(item, color = if (tab == item) Blue else Muted, fontSize = 12.sp, modifier = Modifier.clickable { tab = item }.padding(7.dp))
                    }
                }
                when (tab) {
                    "Teclado" -> {
                        Text(dial, modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp), textAlign = TextAlign.Center, fontSize = 30.sp, color = MaterialTheme.colorScheme.onBackground)
                        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
                        keys.chunked(3).forEach { row ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                row.forEach { key ->
                                    Box(Modifier.padding(6.dp).size(65.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface).clickable { dial += key }, contentAlignment = Alignment.Center) {
                                        Text(key, color = MaterialTheme.colorScheme.onSurface, fontSize = 23.sp)
                                    }
                                }
                            }
                        }
                        Button(onClick = { calling = if (dial.isBlank()) "Número de demonstração" else dial }) { Text("Ligar (simulação)") }
                    }
                    else -> {
                        contacts.forEach { name ->
                            Row(Modifier.fillMaxWidth().clickable { calling = name }.padding(vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE2E8F5)), contentAlignment = Alignment.Center) { Text(name.take(1), color = Color(0xFF45618C), fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.width(12.dp))
                                Text(name, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                                Text("☎", color = Blue)
                            }
                            HorizontalDivider(color = Muted.copy(alpha = 0.18f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessagesScreen(onBack: () -> Unit) {
    var conversation by rememberSaveable { mutableStateOf(false) }
    var input by rememberSaveable { mutableStateOf("") }
    val messages = remember { mutableStateListOf("Oi! Esta é uma conversa de demonstração.", "Legal! O simulador está funcionando.") }
    AppPage("Mensagens", onBack) {
        if (!conversation) {
            Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
                Text("Buscar", color = Muted, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).padding(12.dp))
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().clickable { conversation = true }.padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF84A8E8)), contentAlignment = Alignment.Center) { Text("M", color = Color.White, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Mariana Costa", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                        Text(messages.lastOrNull() ?: "Nenhuma mensagem", color = Muted, fontSize = 12.sp, maxLines = 1)
                    }
                    Text("Hoje", color = Muted, fontSize = 10.sp)
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
                Text("‹ Conversas", color = Blue, modifier = Modifier.clickable { conversation = false }.padding(vertical = 8.dp))
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    messages.forEachIndexed { index, message ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End) {
                            Text(message, color = if (index % 2 == 0) Color(0xFF1A1B20) else Color.White, modifier = Modifier.widthIn(max = 280.dp).clip(RoundedCornerShape(18.dp)).background(if (index % 2 == 0) Color(0xFFE9EAF0) else Color(0xFF2D83E8)).padding(12.dp), fontSize = 14.sp)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("iMessage") }, shape = RoundedCornerShape(22.dp), singleLine = true)
                    Text("↑", color = Blue, fontSize = 26.sp, modifier = Modifier.clickable { if (input.isNotBlank()) { messages.add(input.trim()); input = "" } }.padding(10.dp))
                }
            }
        }
    }
}

@Composable
private fun SafariScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var address by rememberSaveable { mutableStateOf("https://example.com") }
    var currentUrl by rememberSaveable { mutableStateOf("https://example.com") }
    var webView by remember { mutableStateOf<WebView?>(null) }
    DisposableEffect(Unit) {
        onDispose { webView?.stopLoading(); webView?.destroy(); webView = null }
    }
    BackHandler(enabled = webView?.canGoBack() == true) { webView?.goBack() }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = Blue, fontSize = 30.sp, modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(18.dp), textStyle = LocalTextStyle.current.copy(fontSize = 12.sp))
            Text("Ir", color = Blue, modifier = Modifier.clickable {
                val normalized = if (address.startsWith("http://") || address.startsWith("https://")) address else "https://$address"
                currentUrl = normalized
                webView?.loadUrl(normalized)
            }.padding(start = 9.dp))
        }
        AndroidView(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            factory = {
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.javaScriptCanOpenWindowsAutomatically = false
                    settings.setSupportMultipleWindows(false)
                    settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    loadUrl(currentUrl)
                    webView = this
                }
            },
            update = { if (it.url != currentUrl) it.loadUrl(currentUrl) }
        )
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("‹", color = Blue, fontSize = 24.sp, modifier = Modifier.clickable { if (webView?.canGoBack() == true) webView?.goBack() })
            Text("›", color = Blue, fontSize = 24.sp, modifier = Modifier.clickable { if (webView?.canGoForward() == true) webView?.goForward() })
            Text("↻", color = Blue, fontSize = 22.sp, modifier = Modifier.clickable { webView?.reload() })
            Text("Compartilhar", color = Blue, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SimpleClockScreen(time: String, onBack: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf("Relógio") }
    AppPage("Relógio", onBack) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Relógio", "Alarmes", "Cronômetro", "Timer").forEach { label ->
                    Text(label, color = if (tab == label) Blue else Muted, fontSize = 11.sp, modifier = Modifier.clickable { tab = label })
                }
            }
            Spacer(Modifier.height(55.dp))
            Text(time, color = MaterialTheme.colorScheme.onBackground, fontSize = 66.sp, fontWeight = FontWeight.Light)
            Text(SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR")).format(Date()), color = Muted)
            Spacer(Modifier.height(40.dp))
            InfoCard(tab, when (tab) {
                "Alarmes" -> "Nenhum alarme configurado."
                "Cronômetro" -> "Cronômetro demonstrativo."
                "Timer" -> "Temporizador demonstrativo."
                else -> "Horário sincronizado com o dispositivo Android."
            })
        }
    }
}

@Composable
private fun CalendarScreen(onBack: () -> Unit) {
    val cal = java.util.Calendar.getInstance()
    val month = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(Date()).replaceFirstChar { it.uppercase() }
    AppPage("Calendário", onBack) {
        Column(Modifier.fillMaxSize().padding(18.dp)) {
            Text(month, color = MaterialTheme.colorScheme.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { Text(it, color = Muted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center) }
            }
            Spacer(Modifier.height(8.dp))
            val firstDay = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1
            val days = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
            val cells = List(firstDay) { 0 } + (1..days).toList()
            cells.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    week.forEach { day ->
                        Box(Modifier.weight(1f).padding(vertical = 7.dp).height(40.dp), contentAlignment = Alignment.Center) {
                            if (day == cal.get(java.util.Calendar.DAY_OF_MONTH)) {
                                Box(Modifier.size(34.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) { Text("$day", color = Color.White) }
                            } else if (day > 0) Text("$day", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            InfoCard("Hoje", "Nenhum evento cadastrado.")
        }
    }
}

@Composable
private fun NotesScreen(noteText: String, onNoteChange: (String) -> Unit, onBack: () -> Unit) {
    AppPage("Notas", onBack) {
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            Text("Minha nota", color = Muted, fontSize = 12.sp)
            OutlinedTextField(value = noteText, onValueChange = onNoteChange, modifier = Modifier.fillMaxWidth().weight(1f), placeholder = { Text("Comece a escrever...") }, shape = RoundedCornerShape(17.dp))
            Text("Salvo automaticamente neste dispositivo", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(vertical = 10.dp))
        }
    }
}

@Composable
private fun CalculatorScreen(onBack: () -> Unit) {
    var input by rememberSaveable { mutableStateOf("0") }
    var result by rememberSaveable { mutableStateOf("") }
    val keys = listOf("AC", "±", "%", "÷", "7", "8", "9", "×", "4", "5", "6", "−", "1", "2", "3", "+", "0", ".", "⌫", "=")
    AppPage("Calculadora", onBack) {
        Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.Bottom) {
            Text(input, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, color = MaterialTheme.colorScheme.onBackground, fontSize = 46.sp, maxLines = 1)
            Text(result, modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp), textAlign = TextAlign.End, color = Muted, fontSize = 18.sp)
            keys.chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    row.forEach { key ->
                        Box(Modifier.weight(1f).padding(vertical = 4.dp).aspectRatio(1f).clip(CircleShape)
                            .background(if (key in listOf("÷", "×", "−", "+", "=")) Color(0xFFEA9E36) else if (key in listOf("AC", "±", "%", "⌫")) Color(0xFFD5D8DF) else Color(0xFF555963))
                            .clickable {
                                when (key) {
                                    "AC" -> { input = "0"; result = "" }
                                    "⌫" -> input = input.dropLast(1).ifBlank { "0" }
                                    "=" -> result = calculateBasic(input)
                                    "±" -> {
                                        if (input.matches(Regex("-?\\d+(?:\\.\\d+)?"))) {
                                            input = if (input.startsWith("-")) input.drop(1) else "-$input"
                                        }
                                    }
                                    "%" -> input.toDoubleOrNull()?.let { input = (it / 100.0).toString() }
                                    "." -> {
                                        val lastOperand = input.substringAfterLast(Regex("[+*/−-]"))
                                        if (!lastOperand.contains('.')) {
                                            input = if (lastOperand.isEmpty()) input + "0." else input + "."
                                        }
                                    }
                                    "÷", "×", "−", "+" -> {
                                        input = if (input.lastOrNull()?.let { it == '+' || it == '-' || it == '*' || it == '/' || it == '÷' || it == '×' || it == '−' } == true) {
                                            input.dropLast(1) + key
                                        } else if (input == "0") {
                                            "0$key"
                                        } else {
                                            input + key
                                        }
                                    }
                                    else -> input = if (input == "0") key else input + key
                                }
                            }, contentAlignment = Alignment.Center
                        ) { Text(key, color = if (key in listOf("AC", "±", "%", "⌫")) Color(0xFF252832) else Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium) }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherScreen(onBack: () -> Unit) {
    AppPage("Clima", onBack) {
        Column(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF3C8DD9), Color(0xFF8CC9E8)))).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sua localização", color = Color.White, fontSize = 19.sp)
            Spacer(Modifier.height(25.dp))
            Text("☀", color = Color.White, fontSize = 82.sp)
            Text("24°", color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.Light)
            Text("Parcialmente ensolarado", color = Color.White, fontSize = 16.sp)
            Spacer(Modifier.height(36.dp))
            Text("Dados demonstrativos — não é uma previsão em tempo real", color = Color.White.copy(alpha = 0.82f), fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun StoreScreen(onBack: () -> Unit) {
    val apps = listOf("Mapas", "Música", "Podcasts", "Traduzir", "Casa", "Atalhos")
    AppPage("App Store", onBack) {
        Column(Modifier.fillMaxSize().padding(18.dp)) {
            Text("Descubra", color = MaterialTheme.colorScheme.onBackground, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Catálogo demonstrativo", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            apps.forEachIndexed { index, name ->
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(Color(0xFF79B9F2), Color(0xFF4668C9)))), contentAlignment = Alignment.Center) { Text(name.take(1), color = Color.White, fontSize = 21.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                        Text("Aplicativo de demonstração", color = Muted, fontSize = 11.sp)
                    }
                    Text("OBTER", color = Blue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                if (index != apps.lastIndex) HorizontalDivider(color = Muted.copy(alpha = 0.18f))
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String, onBack: () -> Unit) {
    AppPage(name, onBack) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Este aplicativo será desenvolvido em uma próxima etapa.", color = Muted, textAlign = TextAlign.Center, modifier = Modifier.padding(30.dp))
        }
    }
}
