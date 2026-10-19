package com.example.ui.components

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.entity.SlideItem
import com.example.ui.theme.SignagePalette
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun SignageSlideCarousel(
    slides: List<SlideItem>,
    palette: SignagePalette,
    defaultIntervalSeconds: Int = 7,
    modifier: Modifier = Modifier
) {
    if (slides.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(palette.surfaceColor)
                .border(1.dp, palette.surfaceBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = palette.primaryColor,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Belum Ada Slide Informasi",
                    color = palette.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tambahkan slide lewat HP di browser",
                    color = palette.textSecondary,
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentSlide = slides.getOrElse(currentIndex) { slides[0] }
    val slideDuration = (if (currentSlide.durationSeconds > 0) currentSlide.durationSeconds else defaultIntervalSeconds) * 1000L

    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(currentIndex, slides.size) {
        val startTime = System.currentTimeMillis()
        val totalMs = slideDuration.toFloat()
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            progress = (elapsed / totalMs).coerceIn(0f, 1f)
            if (elapsed >= slideDuration) {
                currentIndex = (currentIndex + 1) % slides.size
                progress = 0f
                break
            }
            delay(50)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surfaceColor)
            .border(1.5.dp, palette.surfaceBorder.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
            .shadow(8.dp, RoundedCornerShape(18.dp))
    ) {
        Crossfade(
            targetState = currentSlide,
            animationSpec = tween(600),
            label = "slideTransition"
        ) { slide ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Background & Image layer
                SlideMediaRenderer(
                    slide = slide,
                    palette = palette,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark Bottom Gradient for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.85f),
                                    Color.Black.copy(alpha = 0.95f)
                                ),
                                startY = 150f
                            )
                        )
                )

                // Slide Caption & Content Box
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // Category Chip
                    val isVideo = slide.mediaType == "VIDEO" ||
                            slide.imageUrl.startsWith("/videos/") ||
                            slide.imageUrl.endsWith(".mp4", ignoreCase = true) ||
                            slide.imageUrl.endsWith(".webm", ignoreCase = true) ||
                            slide.imageUrl.endsWith(".mkv", ignoreCase = true)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isVideo) Color(0xFFEF4444) else palette.primaryColor)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isVideo) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = "Video",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Text(
                                    text = slide.category.uppercase(),
                                    color = if (isVideo) Color.White else (if (palette.isDark) Color(0xFF0F172A) else Color.White),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    Text(
                        text = slide.title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 28.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (slide.caption.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = slide.caption,
                            color = Color(0xFFE2E8F0),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 20.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Top-Right Slide Indicators (e.g. 1/4)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            slides.forEachIndexed { idx, _ ->
                Box(
                    modifier = Modifier
                        .size(if (idx == currentIndex) 20.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (idx == currentIndex) palette.primaryColor else Color.White.copy(alpha = 0.4f)
                        )
                )
            }
        }

        // Bottom Animated Progress Bar for current slide
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.BottomCenter),
            color = palette.primaryColor,
            trackColor = Color.Black.copy(alpha = 0.3f),
        )

        // Manual Navigation Arrows on hover/touch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable {
                        currentIndex = if (currentIndex > 0) currentIndex - 1 else slides.size - 1
                        progress = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Prev",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable {
                        currentIndex = (currentIndex + 1) % slides.size
                        progress = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun SlideMediaRenderer(
    slide: SlideItem,
    palette: SignagePalette,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val src = slide.imageUrl
    val isVideo = slide.mediaType == "VIDEO" ||
            src.startsWith("/videos/") ||
            src.endsWith(".mp4", ignoreCase = true) ||
            src.endsWith(".webm", ignoreCase = true) ||
            src.endsWith(".mkv", ignoreCase = true)

    if (isVideo) {
        SignageVideoPlayer(
            videoSource = src,
            modifier = modifier,
            isMuted = false
        )
        return
    }

    when {
        // 1. Local uploaded image path (/images/...)
        src.startsWith("/images/") -> {
            val filename = src.removePrefix("/images/")
            val primary = File(context.filesDir, "media/images/$filename")
            val fallback = File(context.filesDir, "signage_images/$filename")
            val targetFile = if (primary.exists()) primary else (if (fallback.exists()) fallback else null)

            val modelData: Any = targetFile ?: "http://127.0.0.1:8080/images/$filename"
            FullFitImageRenderer(modelData, slide.title, modifier)
        }

        // 2. Base64 Image Data URI
        src.startsWith("data:image") -> {
            val base64Data = src.substringAfter("base64,")
            val bytes = try {
                android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            } catch (_: Exception) { null }

            if (bytes != null) {
                FullFitImageRenderer(bytes, slide.title, modifier)
            } else {
                PresetVectorSlide(slide, palette, modifier)
            }
        }

        // 3. Absolute File Path on disk
        src.startsWith("/") && File(src).exists() -> {
            FullFitImageRenderer(File(src), slide.title, modifier)
        }

        // 4. Remote Web URL
        src.startsWith("http://") || src.startsWith("https://") -> {
            FullFitImageRenderer(src, slide.title, modifier)
        }

        // Preset artistic vector illustrations
        else -> {
            PresetVectorSlide(slide, palette, modifier)
        }
    }
}

@Composable
private fun FullFitImageRenderer(
    modelData: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient Background Fill (eliminates harsh borders, 100% stable across all TV hardware)
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(modelData)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.28f)
        )

        // 2. Foreground: 100% Full Uncropped Image (ContentScale.Fit)
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(modelData)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PresetVectorSlide(
    slide: SlideItem,
    palette: SignagePalette,
    modifier: Modifier = Modifier
) {
    // Generate an artistic generative poster based on category & preset id
    val (gradient, icon, accentPill) = when {
        slide.imageUrl.contains("school_1") || slide.category == "PRESTASI SISWA" -> {
            Triple(
                Brush.linearGradient(listOf(Color(0xFF065F46), Color(0xFF0F766E), Color(0xFF0E7490))),
                Icons.Default.EmojiEvents,
                "MEDALI EMAS NASIONAL"
            )
        }
        slide.imageUrl.contains("school_2") || slide.category == "INFORMASI PPDB" -> {
            Triple(
                Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0369A1), Color(0xFF0F766E))),
                Icons.Default.School,
                "PENDAFTARAN ONLINE"
            )
        }
        slide.imageUrl.contains("gov_1") || slide.category == "PELAYANAN PUBLIK" -> {
            Triple(
                Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF312E81), Color(0xFF4C1D95))),
                Icons.Default.VerifiedUser,
                "LAYANAN PTSP PRIMA"
            )
        }
        slide.imageUrl.contains("gov_3") || slide.category == "ZONA INTEGRITAS" -> {
            Triple(
                Brush.linearGradient(listOf(Color(0xFF881337), Color(0xFF9F1239), Color(0xFF1E3A8A))),
                Icons.Default.Policy,
                "BEBAS KORUPSI (WBK)"
            )
        }
        else -> {
            Triple(
                Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0369A1))),
                Icons.Default.AutoAwesome,
                "INFORMASI RESMI"
            )
        }
    }

    Box(
        modifier = modifier
            .background(gradient)
    ) {
        // Decorative geometric vector shapes
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.85f, size.height * 0.25f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = size.width * 0.28f,
                center = Offset(size.width * 0.15f, size.height * 0.75f)
            )
        }

        // Center Hero Graphic
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = accentPill,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
