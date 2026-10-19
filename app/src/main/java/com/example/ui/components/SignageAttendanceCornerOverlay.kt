package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.entity.AttendanceScanResult

private data class StatusTheme(
    val badgeBg: Color,
    val badgeText: Color,
    val borderColor: Color,
    val title: String,
    val icon: ImageVector
)

@Composable
fun SignageAttendanceCornerOverlay(
    result: AttendanceScanResult?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 24.dp, bottom = 48.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = result != null,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth + 100 },
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth + 100 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(250))
        ) {
            if (result != null) {
                AttendanceCornerCard(result = result, onDismiss = onDismiss)
            }
        }
    }
}

@Composable
private fun AttendanceCornerCard(
    result: AttendanceScanResult,
    onDismiss: () -> Unit
) {
    val theme = when (result) {
        is AttendanceScanResult.Success -> {
            if (result.isCheckIn) {
                if (result.statusText.contains("TEPAT WAKTU", ignoreCase = true)) {
                    StatusTheme(
                        badgeBg = Color(0xFF065F46),
                        badgeText = Color(0xFF6EE7B7),
                        borderColor = Color(0xFF10B981),
                        title = "✓ MASUK - TEPAT WAKTU",
                        icon = Icons.Default.CheckCircle
                    )
                } else {
                    StatusTheme(
                        badgeBg = Color(0xFF78350F),
                        badgeText = Color(0xFFFCD34D),
                        borderColor = Color(0xFFF59E0B),
                        title = "⚠️ MASUK - TERLAMBAT",
                        icon = Icons.Default.Warning
                    )
                }
            } else {
                StatusTheme(
                    badgeBg = Color(0xFF075985),
                    badgeText = Color(0xFF7DD3FC),
                    borderColor = Color(0xFF0284C7),
                    title = "🏠 PRESENSI PULANG",
                    icon = Icons.Default.CheckCircle
                )
            }
        }
        is AttendanceScanResult.AlreadyRecorded -> {
            StatusTheme(
                badgeBg = Color(0xFF4C1D95),
                badgeText = Color(0xFFC4B5FD),
                borderColor = Color(0xFF8B5CF6),
                title = if (result.isCheckIn) "ℹ️ SUDAH ABSEN MASUK" else "ℹ️ SUDAH ABSEN PULANG",
                icon = Icons.Default.Info
            )
        }
        is AttendanceScanResult.UnknownCard -> {
            StatusTheme(
                badgeBg = Color(0xFF7F1D1D),
                badgeText = Color(0xFFFCA5A5),
                borderColor = Color(0xFFEF4444),
                title = "❌ KARTU TIDAK TERDAFTAR",
                icon = Icons.Default.Warning
            )
        }
    }

    Box(
        modifier = Modifier
            .widthIn(min = 320.dp, max = 380.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F172A).copy(alpha = 0.96f),
                        Color(0xFF1E293B).copy(alpha = 0.96f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                color = theme.borderColor.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: Status Badge + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.badgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = theme.icon,
                        contentDescription = null,
                        tint = theme.badgeText,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = theme.title,
                        color = theme.badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Employee Profile or Unknown Warning
            when (result) {
                is AttendanceScanResult.Success -> {
                    EmployeeInfoRow(
                        name = result.employee.name,
                        department = "${result.employee.department} • ${result.employee.role}",
                        photoUrl = result.employee.photoUrl,
                        timeBadge = result.timeFormatted,
                        accentColor = theme.borderColor
                    )
                }
                is AttendanceScanResult.AlreadyRecorded -> {
                    EmployeeInfoRow(
                        name = result.employee.name,
                        department = "${result.employee.department} • ${result.employee.role}",
                        photoUrl = result.employee.photoUrl,
                        timeBadge = "Tercatat pkl ${result.existingTime}",
                        accentColor = theme.borderColor
                    )
                }
                is AttendanceScanResult.UnknownCard -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF450A0A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "UID: ${result.rfidUid}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Daftarkan kartu ini di Menu Presensi Web Remote (port 8080)",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployeeInfoRow(
    name: String,
    department: String,
    photoUrl: String,
    timeBadge: String,
    accentColor: Color
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageModel: Any? = remember(photoUrl) {
        when {
            photoUrl.isBlank() -> null
            photoUrl.startsWith("/images/") -> {
                val filename = photoUrl.removePrefix("/images/")
                val file = java.io.File(context.filesDir, "media/images/$filename")
                if (file.exists()) file else "http://127.0.0.1:8080/images/$filename"
            }
            photoUrl.startsWith("data:image") -> {
                try {
                    val base64Data = photoUrl.substringAfter("base64,")
                    android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                } catch (_: Exception) { null }
            }
            photoUrl.startsWith("/") && java.io.File(photoUrl).exists() -> java.io.File(photoUrl)
            else -> photoUrl
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar Photo / Fallback Initials
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = name,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, accentColor, CircleShape)
            )
        } else {

            val initials = name.split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .joinToString("")
                .ifBlank { "K" }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF334155))
                        )
                    )
                    .border(1.5.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = department,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = timeBadge,
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
