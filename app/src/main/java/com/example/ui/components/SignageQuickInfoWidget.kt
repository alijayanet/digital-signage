package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SignageConfig
import com.example.ui.theme.SignagePalette
import com.example.util.PrayerCalculator
import com.example.util.PrayerTimeItem
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun SignageQuickInfoWidget(
    config: SignageConfig,
    palette: SignagePalette,
    modifier: Modifier = Modifier,
    agendasCount: Int = 0,
    noticesCount: Int = 0,
    slidesCount: Int = 0
) {
    // 1. Pulsing animation for live status indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "livePulse"
    )

    // 2. Real-time prayer times calculated dynamically based on config location & timezone
    var prayerItems by remember { mutableStateOf<List<PrayerTimeItem>>(emptyList()) }

    LaunchedEffect(config.latitude, config.longitude, config.timezone) {
        while (true) {
            val tz = com.example.util.IndonesiaCityData.getTimeZone(config.timezone)
            val nowCal = Calendar.getInstance(tz)
            val schedule = PrayerCalculator.calculatePrayerTimes(
                calendar = nowCal,
                lat = config.latitude,
                lng = config.longitude,
                timezone = config.timezone
            )
            prayerItems = PrayerCalculator.getPrayerTimeItems(nowCal, schedule)
            delay(30_000)
        }
    }

    val tzLabel = com.example.util.IndonesiaCityData.getTimezoneName(config.timezone)
    val operationalHours = when (config.orgType) {
        "Sekolah" -> "07.00 - 15.30 $tzLabel"
        "Pemerintahan" -> "08.00 - 15.30 $tzLabel"
        else -> "08.00 - 17.00 $tzLabel"
    }

    val operationalLabel = when (config.orgType) {
        "Sekolah" -> "JAM KBM"
        "Pemerintahan" -> "JAM LAYANAN"
        else -> "OPERASIONAL"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceColor)
            .border(1.dp, palette.surfaceBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ====== 1. TOP STATUS & OPERATIONAL HOURS ======
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status Live
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .alpha(pulseAlpha)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AKTIF",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Jam Operasional / KBM
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = palette.primaryColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$operationalLabel: $operationalHours",
                        color = palette.textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = palette.surfaceBorder.copy(alpha = 0.35f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ====== 2. KALENDER MASEHI BULAN BERJALAN ======
            MonthlyCalendarSection(palette = palette)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = palette.surfaceBorder.copy(alpha = 0.35f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ====== 3. JADWAL SHOLAT 5 WAKTU (STRIP RAPI & SIMETRIS) ======
            PrayerTimesSection(
                prayerItems = prayerItems,
                palette = palette,
                cityName = config.cityName,
                tzLabel = tzLabel
            )
        }
    }
}

@Composable
private fun MonthlyCalendarSection(
    palette: SignagePalette,
    modifier: Modifier = Modifier
) {
    val today = remember { Calendar.getInstance() }
    val currentDay = today.get(Calendar.DAY_OF_MONTH)
    val currentMonth = today.get(Calendar.MONTH) // 0-based
    val currentYear = today.get(Calendar.YEAR)

    val monthCal = remember(currentMonth, currentYear) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, currentYear)
        cal.set(Calendar.MONTH, currentMonth)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal
    }
    val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, ...
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    val monthName = monthNames.getOrElse(currentMonth) { "Bulan Ini" }
    val dayLabels = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

    Column(modifier = modifier.fillMaxWidth()) {
        // Month Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = palette.primaryColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$monthName $currentYear".uppercase(),
                    color = palette.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "Hari Ini: $currentDay $monthName",
                color = palette.primaryColor,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Days of week header (Min, Sen, Sel, Rab, Kam, Jum, Sab)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(if (palette.isDark) Color(0xFF0F172A).copy(alpha = 0.6f) else Color(0xFFE2E8F0).copy(alpha = 0.6f))
                .padding(vertical = 3.dp)
        ) {
            dayLabels.forEachIndexed { idx, label ->
                Text(
                    text = label,
                    color = if (idx == 0) Color(0xFFEF4444) else palette.textSecondary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar Date Cells
        val offset = firstDayOfWeek - 1 // 0 for Sunday, 1 for Monday, etc.

        for (row in 0 until 6) {
            val startDayOfRow = row * 7 - offset + 1
            if (startDayOfRow > daysInMonth) break

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - offset + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNum in 1..daysInMonth) {
                            val isToday = dayNum == currentDay
                            val isSunday = col == 0

                            if (isToday) {
                                Box(
                                    modifier = Modifier
                                        .size(19.dp)
                                        .clip(CircleShape)
                                        .background(palette.primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dayNum",
                                        color = if (palette.isDark) Color(0xFF0F172A) else Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            } else {
                                Text(
                                    text = "$dayNum",
                                    color = if (isSunday) Color(0xFFEF4444).copy(alpha = 0.85f) else palette.textPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSunday) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrayerTimesSection(
    prayerItems: List<PrayerTimeItem>,
    palette: SignagePalette,
    cityName: String = "Jakarta",
    tzLabel: String = "WIB",
    modifier: Modifier = Modifier
) {
    val items = if (prayerItems.isNotEmpty()) {
        prayerItems
    } else {
        // Fallback default
        listOf(
            PrayerTimeItem("SUBUH", "04:36", false),
            PrayerTimeItem("DZUHUR", "11:58", true),
            PrayerTimeItem("ASHAR", "15:18", false),
            PrayerTimeItem("MAGHRIB", "17:59", false),
            PrayerTimeItem("ISYA", "19:08", false)
        )
    }

    val displayCity = if (cityName.isNotBlank()) cityName.uppercase() else "INDONESIA"

    Column(modifier = modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = null,
                    tint = palette.accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "WAKTU SHOLAT • $displayCity ($tzLabel)",
                    color = palette.textPrimary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1
                )
            }

            val nextItem = items.firstOrNull { it.isNext }
            if (nextItem != null) {
                Text(
                    text = "Berikutnya: ${nextItem.name} ${nextItem.time}",
                    color = palette.accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 5 Symmetrical Boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { prayer ->
                val isNext = prayer.isNext
                val boxBg = if (isNext) {
                    palette.accentColor.copy(alpha = 0.22f)
                } else {
                    if (palette.isDark) Color(0xFF0F172A).copy(alpha = 0.45f) else Color(0xFFF1F5F9).copy(alpha = 0.7f)
                }
                val borderColor = if (isNext) palette.accentColor else palette.surfaceBorder.copy(alpha = 0.4f)
                val textColor = if (isNext) palette.accentColor else palette.textPrimary
                val labelColor = if (isNext) palette.accentColor else palette.textSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(boxBg)
                        .border(if (isNext) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(8.dp))
                        .padding(vertical = 5.dp, horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = prayer.name,
                            color = labelColor,
                            fontSize = 8.5.sp,
                            fontWeight = if (isNext) FontWeight.ExtraBold else FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = prayer.time,
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
