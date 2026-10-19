package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.NoticeItem
import com.example.ui.theme.SignagePalette
import kotlinx.coroutines.delay

@Composable
fun SignageNoticeWidget(
    notices: List<NoticeItem>,
    palette: SignagePalette,
    modifier: Modifier = Modifier
) {
    // Auto-cycle through notices if there are multiple
    var currentIndex by remember { mutableIntStateOf(0) }
    var progressTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(notices.size) {
        if (notices.isNotEmpty()) {
            currentIndex = 0
            while (true) {
                progressTrigger = false
                delay(100)
                progressTrigger = true
                delay(8000)
                if (notices.isNotEmpty()) {
                    currentIndex = (currentIndex + 1) % notices.size
                }
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (progressTrigger) 1f else 0f,
        animationSpec = if (progressTrigger) tween(8000, easing = LinearEasing) else tween(0),
        label = "noticeProgress"
    )

    val safeIndex = if (notices.isNotEmpty()) currentIndex.coerceIn(0, notices.lastIndex) else 0
    val activeNotice = notices.getOrNull(safeIndex)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceColor)
            .border(1.dp, palette.surfaceBorder.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = palette.accentColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "PENGUMUMAN RESMI",
                            color = palette.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Informasi & Maklumat",
                            color = palette.textSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Page indicator or count
                if (notices.size > 1) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pager dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            notices.take(5).forEachIndexed { index, _ ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == safeIndex) 6.dp else 4.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == safeIndex) palette.accentColor
                                            else palette.textSecondary.copy(alpha = 0.35f)
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.accentColor.copy(alpha = 0.15f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${safeIndex + 1}/${notices.size}",
                                color = palette.accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (notices.size == 1) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "1 Info",
                            color = palette.accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = palette.surfaceBorder.copy(alpha = 0.35f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Body Area
            if (activeNotice == null) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = palette.textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Belum ada pengumuman resmi baru",
                            color = palette.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Active Announcement with Crossfade transition
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Crossfade(
                        targetState = activeNotice,
                        animationSpec = tween(400),
                        label = "noticeCrossfade"
                    ) { notice ->
                        val (priorityBg, priorityColor, icon) = when (notice.priority) {
                            "URGENT" -> Triple(Color(0xFFEF4444).copy(alpha = 0.2f), Color(0xFFEF4444), Icons.Default.PriorityHigh)
                            "IMPORTANT" -> Triple(Color(0xFFF59E0B).copy(alpha = 0.2f), Color(0xFFF59E0B), Icons.Default.Warning)
                            else -> Triple(Color(0xFF38BDF8).copy(alpha = 0.2f), Color(0xFF38BDF8), Icons.Default.Info)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Priority badge & date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(priorityBg)
                                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = priorityColor,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = notice.priority,
                                            color = priorityColor,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                Text(
                                    text = notice.dateText,
                                    color = palette.textSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Large Title
                            Text(
                                text = notice.title,
                                color = palette.textPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (notice.content.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = notice.content,
                                    color = palette.textSecondary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Bottom Progress Bar if cycling multiple notices
                if (notices.size > 1) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = palette.accentColor,
                        trackColor = palette.surfaceBorder.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}
