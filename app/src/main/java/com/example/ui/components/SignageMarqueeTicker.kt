package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.RunningTickerItem
import com.example.ui.theme.SignagePalette
import kotlin.math.roundToInt

@Composable
fun SignageMarqueeTicker(
    tickers: List<RunningTickerItem>,
    palette: SignagePalette,
    serverUrl: String,
    speedFactor: Int = 1, // 0 = slow (45s), 1 = normal (30s), 2 = fast (18s)
    modifier: Modifier = Modifier
) {
    val durationMs = when (speedFactor) {
        0 -> 45000
        2 -> 18000
        else -> 30000
    }

    val fullTickerText = remember(tickers) {
        if (tickers.isEmpty()) {
            "Selamat datang di Layanan Informasi Digital Signage TV • Gunakan HP pada jaringan yang sama untuk mengedit informasi ini."
        } else {
            tickers.joinToString(separator = "   ★   ") { item ->
                "[${item.badge.uppercase()}] ${item.text}"
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .shadow(10.dp, RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            .background(palette.tickerBg)
            .border(1.dp, palette.surfaceBorder.copy(alpha = 0.5f), RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Ticker Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomEnd = 14.dp))
                    .background(palette.primaryColor)
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = if (palette.isDark) Color(0xFF0F172A) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INFO TERKINI",
                        color = if (palette.isDark) Color(0xFF0F172A) else Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Scrolling text container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clipToBounds()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                var containerWidthPx by remember { mutableFloatStateOf(1000f) }
                var textWidthPx by remember { mutableFloatStateOf(1000f) }

                val infiniteTransition = rememberInfiniteTransition(label = "marqueeTransition")
                val offsetFraction by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMs, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "marqueeOffset"
                )

                val density = LocalDensity.current

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { containerWidthPx = it.size.width.toFloat() }
                ) {
                    val totalDistance = containerWidthPx + textWidthPx
                    val currentX = containerWidthPx - (1f - offsetFraction) * totalDistance

                    Text(
                        text = "$fullTickerText       ★       $fullTickerText",
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .offset { IntOffset(currentX.roundToInt(), 0) }
                            .onGloballyPositioned { textWidthPx = it.size.width.toFloat() }
                    )
                }
            }
        }
    }
}
