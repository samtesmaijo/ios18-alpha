package com.complux.ios18sim.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val Ink = Color(0xFF11131A)
internal val Muted = Color(0xFF8E929D)
internal val Blue = Color(0xFF3E83F7)
internal val PanelLight = Color(0xFFF3F4F8)
val LocalGlassLab = staticCompositionLocalOf { false }
val LocalPerformanceProfile = staticCompositionLocalOf { "Balanced" }

private val wallpaperPalettes = listOf(
    listOf(Color(0xFF17234A), Color(0xFF6E4D9D), Color(0xFFF09D9E)),
    listOf(Color(0xFF122C48), Color(0xFF167C8B), Color(0xFFB5D8C9)),
    listOf(Color(0xFF36214F), Color(0xFFB05C83), Color(0xFFF4B78C)),
    listOf(Color(0xFF172B45), Color(0xFF476F9E), Color(0xFFBED4E7)),
    listOf(Color(0xFF3A204D), Color(0xFF784E8C), Color(0xFFD5A9C4))
)

@Composable
fun IOSWallpaper(index: Int, modifier: Modifier = Modifier) {
    val colors = wallpaperPalettes[index.mod(wallpaperPalettes.size)]
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(0f, size.height),
                end = Offset(size.width, 0f)
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors[2].copy(alpha = 0.76f), colors[2].copy(alpha = 0f)),
                center = Offset(size.width * 0.77f, size.height * 0.26f),
                radius = size.minDimension * 0.72f
            ),
            radius = size.minDimension * 0.72f,
            center = Offset(size.width * 0.77f, size.height * 0.26f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors[0].copy(alpha = 0.82f), colors[0].copy(alpha = 0f)),
                center = Offset(size.width * 0.12f, size.height * 0.72f),
                radius = size.minDimension * 0.75f
            ),
            radius = size.minDimension * 0.75f,
            center = Offset(size.width * 0.12f, size.height * 0.72f)
        )
    }
}

@Composable
fun FakeStatusBar(
    time: String,
    darkIcons: Boolean = false,
    modifier: Modifier = Modifier,
    onControlCenter: (() -> Unit)? = null
) {
    val color = if (darkIcons) Ink else Color.White
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(time, color = color, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("▮▮▮", color = color, fontSize = 10.sp)
            Text("◔", color = color, fontSize = 15.sp)
            Text("▰", color = color, fontSize = 13.sp)
        }
    }
}

@Composable
fun AppIcon(
    title: String,
    symbol: String,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val profile = LocalPerformanceProfile.current
    val iconShadow = when (profile) {
        "Low Resource" -> 2.dp
        "High Quality" -> 9.dp
        else -> 6.dp
    }
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(58.dp)
                .shadow(iconShadow, RoundedCornerShape(17.dp))
                .background(Brush.linearGradient(colors), RoundedCornerShape(17.dp))
                .border(0.6.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(17.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(symbol, color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Medium)
        }
        Text(
            title,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FrostedPanel(
    modifier: Modifier = Modifier,
    radius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val glassLab = LocalGlassLab.current
    val profile = LocalPerformanceProfile.current
    val lowResource = profile == "Low Resource"
    val surfaceAlpha = if (glassLab && !lowResource) 0.23f else 0.16f
    val edgeAlpha = if (glassLab && !lowResource) 0.38f else 0.22f
    val elevation = when (profile) {
        "Low Resource" -> 3.dp
        "High Quality" -> 16.dp
        else -> 10.dp
    }
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(radius))
            .background(Color.White.copy(alpha = surfaceAlpha), RoundedCornerShape(radius))
            .border(1.dp, Color.White.copy(alpha = edgeAlpha), RoundedCornerShape(radius))
            .padding(14.dp),
        content = content
    )
}

@Composable
fun ScreenTitle(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            androidx.compose.material3.TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                Text("‹", color = Blue, fontSize = 34.sp, lineHeight = 34.sp)
            }
            Spacer(Modifier.width(8.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) Text(subtitle, color = Muted, fontSize = 12.sp)
        }
    }
}

