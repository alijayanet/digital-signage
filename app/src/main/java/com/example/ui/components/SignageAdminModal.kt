package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.entity.*
import com.example.ui.theme.SignagePalette

// High-Contrast Input Field specifically designed for TV Screens & Remotes
@Composable
private fun SignageInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    palette: SignagePalette,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE2E8F0)
            )
        },
        placeholder = if (placeholder.isNotBlank()) {
            {
                Text(
                    text = placeholder,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        } else null,
        modifier = modifier,
        singleLine = singleLine,
        minLines = minLines,
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF0F172A),
            unfocusedContainerColor = Color(0xFF0F172A),
            focusedBorderColor = palette.primaryColor,
            unfocusedBorderColor = Color(0xFF64748B),
            focusedLabelColor = palette.primaryColor,
            unfocusedLabelColor = Color(0xFFCBD5E1),
            focusedPlaceholderColor = Color(0xFF94A3B8),
            unfocusedPlaceholderColor = Color(0xFF94A3B8),
            cursorColor = palette.primaryColor
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignageAdminModal(
    config: SignageConfig,
    palette: SignagePalette,
    serverUrl: String,
    slides: List<SlideItem> = emptyList(),
    agendas: List<AgendaItem> = emptyList(),
    notices: List<NoticeItem> = emptyList(),
    tickers: List<RunningTickerItem> = emptyList(),
    onDismiss: () -> Unit,
    onApplyPreset: (String) -> Unit,
    onUpdateTheme: (String) -> Unit,
    onSaveConfig: (SignageConfig) -> Unit,
    onAddSlide: (SlideItem) -> Unit = {},
    onUpdateSlide: (SlideItem) -> Unit = {},
    onDeleteSlide: (Long) -> Unit = {},
    onAddAgenda: (AgendaItem) -> Unit = {},
    onUpdateAgenda: (AgendaItem) -> Unit = {},
    onDeleteAgenda: (Long) -> Unit = {},
    onAddNotice: (NoticeItem) -> Unit = {},
    onUpdateNotice: (NoticeItem) -> Unit = {},
    onDeleteNotice: (Long) -> Unit = {},
    onAddTicker: (RunningTickerItem) -> Unit = {},
    onUpdateTicker: (RunningTickerItem) -> Unit = {},
    onDeleteTicker: (Long) -> Unit = {},
    onSendFlashAlert: (String) -> Unit = {},
    onDismissFlashAlert: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, palette.primaryColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF090E1A))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF030712))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(palette.primaryColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = palette.primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Pengaturan & Manajemen Digital Signage TV",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Kelola profil, media slide/video, jadwal, pengumuman, dan kontrol HP",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Close Button
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_close_settings_top")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Tutup (✕)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0x33FFFFFF), thickness = 1.dp)

                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left Sidebar Navigation Tabs
                        Column(
                            modifier = Modifier
                                .width(200.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF030712))
                                .verticalScroll(rememberScrollState())
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val navItems = listOf(
                                "📲 Sambung HP" to 0,
                                "🏢 Profil & Instansi" to 1,
                                "🎨 Tema & Tata Letak" to 2,
                                "🤖 Bot Telegram" to 9,
                                "🖼️ Slide & Video TV" to 3,
                                "📅 Agenda & Jadwal" to 4,
                                "📢 Papan Pengumuman" to 5,
                                "📜 Running Text" to 6,
                                "🚨 Pesan Kilat TV" to 7,
                                "⚙️ Widget & Fitur" to 8
                            )

                            navItems.forEach { (label, idx) ->
                                val isSel = selectedTab == idx
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSel) palette.primaryColor
                                            else Color.Transparent
                                        )
                                        .clickable { selectedTab = idx }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isSel) (if (palette.isDark) Color(0xFF0F172A) else Color.White) else Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_close_settings"),
                                contentPadding = PaddingValues(vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x26FFFFFF)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("✕ Tutup Layar", color = Color.White, fontSize = 11.sp)
                            }
                        }

                        // Right Content Area
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFF090E1A))
                                .padding(12.dp)
                        ) {
                            when (selectedTab) {
                                0 -> TabMobileConnectSignage(serverUrl, palette)
                                1 -> TabProfileSignage(config, palette, onSaveConfig)
                                2 -> TabThemeAndLayoutSignage(config, palette, onSaveConfig, onApplyPreset, onUpdateTheme)
                                3 -> TabSlideMediaSignage(slides, palette, onAddSlide, onUpdateSlide, onDeleteSlide)
                                4 -> TabAgendaSignage(agendas, palette, onAddAgenda, onUpdateAgenda, onDeleteAgenda)
                                5 -> TabNoticeSignage(notices, palette, onAddNotice, onUpdateNotice, onDeleteNotice)
                                6 -> TabTickerSignage(tickers, config, palette, onSaveConfig, onAddTicker, onUpdateTicker, onDeleteTicker)
                                7 -> TabFlashAlertSignage(config, palette, onSendFlashAlert, onDismissFlashAlert)
                                8 -> TabWidgetsToggleSignage(config, palette, onSaveConfig)
                                9 -> TabTelegramSignage(config, palette, onSaveConfig)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- TAB IMPLEMENTATIONS ----------------

@Composable
private fun TabMobileConnectSignage(serverUrl: String, palette: SignagePalette) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp)
        ) {
            QrCodeView(
                data = serverUrl,
                size = 180.dp
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📱 Kelola TV Langsung Lewat HP (Jaringan Wi-Fi Sama)",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.primaryColor
            )
            Text(
                text = "1. Hubungkan HP Anda ke Wi-Fi / jaringan yang sama dengan TV ini.\n" +
                        "2. Buka Kamera HP dan scan QR Code di samping, atau buka Google Chrome / Browser HP:",
                fontSize = 12.sp,
                color = Color(0xFFE2E8F0),
                lineHeight = 18.sp
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF030712))
                    .border(1.5.dp, palette.primaryColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = palette.primaryColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = serverUrl,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Text(
                text = "✨ Web Dashboard di HP memudahkan Anda mengedit data, mengunggah video MP4, foto, teks berjalan, agenda, dan pengumuman secara real-time!",
                fontSize = 11.sp,
                color = Color(0xFF6EE7B7),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun TabProfileSignage(
    config: SignageConfig,
    palette: SignagePalette,
    onSaveConfig: (SignageConfig) -> Unit
) {
    var orgType by remember(config) { mutableStateOf(config.orgType) }
    var orgName by remember(config) { mutableStateOf(config.orgName) }
    var orgSubtitle by remember(config) { mutableStateOf(config.orgSubtitle) }
    var headerBannerText by remember(config) { mutableStateOf(config.headerBannerText) }
    var savedToast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("🏢 Identitas & Profil Lembaga", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val orgOptions = listOf("Sekolah", "Pemerintahan", "Umum")
            orgOptions.forEach { type ->
                val isSel = orgType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) palette.primaryColor.copy(alpha = 0.25f) else Color(0xFF1E293B))
                        .border(if (isSel) 1.5.dp else 1.dp, if (isSel) palette.primaryColor else Color(0xFF475569), RoundedCornerShape(8.dp))
                        .clickable { orgType = type }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (type == "Sekolah") "🏫 Sekolah / Kampus" else if (type == "Pemerintahan") "🏛️ Pemerintahan / PTSP" else "🏢 Umum / Korporat",
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSel) palette.primaryColor else Color.White
                    )
                }
            }
        }

        SignageInputField(
            value = orgName,
            onValueChange = { orgName = it; savedToast = false },
            label = "Nama Lembaga / Sekolah / Kantor",
            placeholder = "Contoh: SMA NEGERI 1 TELADAN NUSANTARA",
            palette = palette,
            modifier = Modifier.fillMaxWidth()
        )

        SignageInputField(
            value = orgSubtitle,
            onValueChange = { orgSubtitle = it; savedToast = false },
            label = "Slogan / Visi Misi",
            placeholder = "Contoh: Mewujudkan Generasi Unggul, Cerdas & Berkarakter",
            palette = palette,
            modifier = Modifier.fillMaxWidth()
        )

        SignageInputField(
            value = headerBannerText,
            onValueChange = { headerBannerText = it; savedToast = false },
            label = "Teks Banner / Status Akreditasi (Header)",
            placeholder = "Contoh: TERAKREDITASI A • SEKOLAH PENGGERAK",
            palette = palette,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSaveConfig(
                    config.copy(
                        orgType = orgType,
                        orgName = orgName,
                        orgSubtitle = orgSubtitle,
                        headerBannerText = headerBannerText
                    )
                )
                savedToast = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryColor),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Text(
                "💾 Simpan Perubahan Identitas",
                color = if (palette.isDark) Color(0xFF0F172A) else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        if (savedToast) {
            Text("✅ Profil & Identitas Lembaga Berhasil Disimpan!", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TabThemeAndLayoutSignage(
    config: SignageConfig,
    palette: SignagePalette,
    onSaveConfig: (SignageConfig) -> Unit,
    onApplyPreset: (String) -> Unit,
    onUpdateTheme: (String) -> Unit
) {
    val presets = listOf(
        Triple("school", "🏫 Template Sekolah", "Otomatis memuat kurikulum, prestasi siswa, ANBK, dan jadwal belajar."),
        Triple("gov", "🏛️ Template Pemerintahan / PTSP", "Otomatis memuat jam pelayanan, PTSP, Zona Integritas & Adminduk.")
    )

    val themeList = listOf(
        Triple("high_density", "High Density (M3)", listOf(Color(0xFF6750A4), Color(0xFFB3261E))),
        Triple("school_modern", "Sekolah Modern", listOf(Color(0xFF059669), Color(0xFF06B6D4))),
        Triple("gov_nusantara", "Gov Nusantara", listOf(Color(0xFF1E3A8A), Color(0xFFD97706))),
        Triple("cyber_blue", "Cyber Tech", listOf(Color(0xFF00F0FF), Color(0xFF3B82F6))),
        Triple("minimal_clean", "Minimal Light", listOf(Color(0xFFF8FAFC), Color(0xFF6366F1))),
        Triple("sunset_warm", "Sunset Gold", listOf(Color(0xFFE11D48), Color(0xFFF59E0B)))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("✨ Template Instan (1-Klik)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            presets.forEach { (type, title, desc) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF475569), RoundedCornerShape(10.dp))
                        .clickable { onApplyPreset(type) }
                        .padding(10.dp)
                ) {
                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(desc, fontSize = 10.sp, color = Color(0xFFCBD5E1), lineHeight = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Terapkan Template ➔", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text("🎨 Pilihan Tema Warna TV", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            themeList.forEach { (tId, tName, tColors) ->
                val isSel = config.themeId == tId
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF334155) else Color(0xFF1E293B))
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) palette.primaryColor else Color(0xFF475569),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onUpdateTheme(tId) }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Brush.horizontalGradient(tColors))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tName,
                            color = if (isSel) palette.primaryColor else Color(0xFFE2E8F0),
                            fontSize = 9.5.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabSlideMediaSignage(
    slides: List<SlideItem>,
    palette: SignagePalette,
    onAddSlide: (SlideItem) -> Unit,
    onUpdateSlide: (SlideItem) -> Unit,
    onDeleteSlide: (Long) -> Unit
) {
    var editingSlideId by remember { mutableStateOf<Long?>(null) }
    var mediaType by remember { mutableStateOf("IMAGE") }
    var title by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("INFORMASI") }
    var imageUrl by remember { mutableStateOf("") }
    var durationSeconds by remember { mutableIntStateOf(8) }
    var savedToast by remember { mutableStateOf<String?>(null) }

    fun resetForm() {
        editingSlideId = null
        mediaType = "IMAGE"
        title = ""
        caption = ""
        category = "INFORMASI"
        imageUrl = ""
        durationSeconds = 8
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🖼️ Slide Gambar & 🎬 Video Player", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
            if (editingSlideId != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.primaryColor.copy(alpha = 0.25f))
                        .border(1.dp, palette.primaryColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("✏️ Mode Edit (ID: #$editingSlideId)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
                }
            }
        }

        // Form Card (Add / Edit)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (editingSlideId != null) palette.primaryColor else Color(0xFF475569))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingSlideId != null) "✏️ Formulir Edit Data Slide / Video" else "➕ Tambah Slide Gambar atau Video Baru",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (editingSlideId != null) palette.primaryColor else Color.White
                    )
                    if (editingSlideId != null) {
                        Button(
                            onClick = {
                                resetForm()
                                savedToast = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Batal Edit ✕", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { mediaType = "IMAGE"; if (category == "VIDEO PROFIL") category = "INFORMASI" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (mediaType == "IMAGE") palette.primaryColor else Color(0xFF334155)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("🖼️ Gambar / Poster", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { mediaType = "VIDEO"; category = "VIDEO PROFIL"; if (durationSeconds < 10) durationSeconds = 15 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (mediaType == "VIDEO") Color(0xFFEF4444) else Color(0xFF334155)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("🎬 Video (MP4 / Web)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                SignageInputField(
                    value = title,
                    onValueChange = { title = it; savedToast = null },
                    label = if (mediaType == "VIDEO") "Judul Video" else "Judul Informasi",
                    placeholder = "Contoh: Video Profil Sekolah 2026",
                    palette = palette,
                    modifier = Modifier.fillMaxWidth()
                )

                SignageInputField(
                    value = caption,
                    onValueChange = { caption = it; savedToast = null },
                    label = "Keterangan Lengkap",
                    placeholder = "Keterangan atau rincian kegiatan...",
                    palette = palette,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SignageInputField(
                        value = category,
                        onValueChange = { category = it; savedToast = null },
                        label = "Kategori (Badge)",
                        placeholder = "INFORMASI",
                        palette = palette,
                        modifier = Modifier.weight(1f)
                    )
                    SignageInputField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it; savedToast = null },
                        label = if (mediaType == "VIDEO") "Link URL Video MP4" else "URL Gambar / Preset ID",
                        placeholder = if (mediaType == "VIDEO") "https://domain.com/video.mp4" else "preset_school_1",
                        palette = palette,
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val finalUrl = imageUrl.ifBlank { if (mediaType == "VIDEO") "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4" else "preset_school_1" }
                            if (editingSlideId != null) {
                                onUpdateSlide(
                                    SlideItem(
                                        id = editingSlideId!!,
                                        title = title,
                                        caption = caption,
                                        category = category,
                                        imageUrl = finalUrl,
                                        mediaType = mediaType,
                                        durationSeconds = durationSeconds,
                                        displayOrder = 99,
                                        isActive = true
                                    )
                                )
                                savedToast = "Slide/Video Berhasil Diperbarui!"
                                resetForm()
                            } else {
                                onAddSlide(
                                    SlideItem(
                                        title = title,
                                        caption = caption,
                                        category = category,
                                        imageUrl = finalUrl,
                                        mediaType = mediaType,
                                        durationSeconds = durationSeconds,
                                        displayOrder = 99,
                                        isActive = true
                                    )
                                )
                                savedToast = "Slide/Video Berhasil Ditambahkan!"
                                resetForm()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (editingSlideId != null) palette.primaryColor else Color(0xFF10B981)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = if (editingSlideId != null) "💾 SIMPAN PEMBARUAN SLIDE / VIDEO" else "➕ TAMBAHKAN KE LAYAR TV",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                if (savedToast != null) {
                    Text("✅ $savedToast", color = Color(0xFF81C784), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("📋 Daftar Slide & Video Aktif di Layar (${slides.size}) — Klik pada item atau tombol ✏️ Edit untuk mengedit:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))

        slides.forEach { s ->
            val isVid = s.mediaType == "VIDEO" || s.imageUrl.endsWith(".mp4") || s.imageUrl.startsWith("/videos/")
            val isBeingEdited = editingSlideId == s.id

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isBeingEdited) Color(0xFF334155) else Color(0xFF1E293B))
                    .border(if (isBeingEdited) 1.5.dp else 1.dp, if (isBeingEdited) palette.primaryColor else Color(0xFF475569), RoundedCornerShape(8.dp))
                    .clickable {
                        editingSlideId = s.id
                        mediaType = s.mediaType
                        title = s.title
                        caption = s.caption
                        category = s.category
                        imageUrl = s.imageUrl
                        durationSeconds = s.durationSeconds
                        savedToast = null
                    }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(if (isVid) "🎬" else "🖼️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = s.category,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVid) Color(0xFFEF4444) else palette.primaryColor
                            )
                            Text(" • ${s.durationSeconds}s", fontSize = 9.5.sp, color = Color(0xFFCBD5E1))
                            if (isBeingEdited) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("• [SEDANG DIEDIT]", fontSize = 9.sp, fontWeight = FontWeight.Black, color = palette.primaryColor)
                            }
                        }
                        Text(s.title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Edit Icon Button
                    IconButton(
                        onClick = {
                            editingSlideId = s.id
                            mediaType = s.mediaType
                            title = s.title
                            caption = s.caption
                            category = s.category
                            imageUrl = s.imageUrl
                            durationSeconds = s.durationSeconds
                            savedToast = null
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3310B981))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Slide", tint = Color(0xFFA7F3D0), modifier = Modifier.size(16.dp))
                    }

                    // Delete Icon Button
                    IconButton(
                        onClick = {
                            if (editingSlideId == s.id) resetForm()
                            onDeleteSlide(s.id)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33EF4444))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TabAgendaSignage(
    agendas: List<AgendaItem>,
    palette: SignagePalette,
    onAddAgenda: (AgendaItem) -> Unit,
    onUpdateAgenda: (AgendaItem) -> Unit,
    onDeleteAgenda: (Long) -> Unit
) {
    var editingAgendaId by remember { mutableStateOf<Long?>(null) }
    var time by remember { mutableStateOf("08:00 - 10:00 WIB") }
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Aula Utama") }
    var pic by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("UPCOMING") }
    var savedToast by remember { mutableStateOf<String?>(null) }

    fun resetForm() {
        editingAgendaId = null
        time = "08:00 - 10:00 WIB"
        title = ""
        location = "Aula Utama"
        pic = ""
        status = "UPCOMING"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📅 Agenda & Jadwal Kegiatan Hari Ini", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
            if (editingAgendaId != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.primaryColor.copy(alpha = 0.25f))
                        .border(1.dp, palette.primaryColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("✏️ Mode Edit Agenda (ID: #$editingAgendaId)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (editingAgendaId != null) palette.primaryColor else Color(0xFF475569))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingAgendaId != null) "✏️ Formulir Edit Agenda" else "➕ Tambah Agenda Baru",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (editingAgendaId != null) palette.primaryColor else Color.White
                    )
                    if (editingAgendaId != null) {
                        Button(
                            onClick = {
                                resetForm()
                                savedToast = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Batal Edit ✕", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SignageInputField(value = time, onValueChange = { time = it; savedToast = null }, label = "Waktu", placeholder = "08:00 - 10:00 WIB", palette = palette, modifier = Modifier.weight(1f))
                    SignageInputField(value = status, onValueChange = { status = it; savedToast = null }, label = "Status (UPCOMING/ONGOING)", placeholder = "UPCOMING", palette = palette, modifier = Modifier.weight(1f))
                }

                SignageInputField(value = title, onValueChange = { title = it; savedToast = null }, label = "Nama Kegiatan", placeholder = "Contoh: Upacara Bendera & Pembagian Raport", palette = palette, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SignageInputField(value = location, onValueChange = { location = it; savedToast = null }, label = "Tempat / Ruangan", placeholder = "Aula Utama", palette = palette, modifier = Modifier.weight(1f))
                    SignageInputField(value = pic, onValueChange = { pic = it; savedToast = null }, label = "Penanggung Jawab / PIC", placeholder = "Wakasek Kesiswaan", palette = palette, modifier = Modifier.weight(1f))
                }

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            if (editingAgendaId != null) {
                                onUpdateAgenda(
                                    AgendaItem(
                                        id = editingAgendaId!!,
                                        time = time,
                                        title = title,
                                        location = location,
                                        pic = pic,
                                        status = status,
                                        displayOrder = 99,
                                        isActive = true
                                    )
                                )
                                savedToast = "Agenda Berhasil Diperbarui!"
                                resetForm()
                            } else {
                                onAddAgenda(
                                    AgendaItem(
                                        time = time,
                                        title = title,
                                        location = location,
                                        pic = pic,
                                        status = status,
                                        displayOrder = 99,
                                        isActive = true
                                    )
                                )
                                savedToast = "Agenda Berhasil Ditambahkan!"
                                resetForm()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (editingAgendaId != null) palette.primaryColor else Color(0xFF10B981)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = if (editingAgendaId != null) "💾 SIMPAN PEMBARUAN AGENDA" else "➕ TAMBAH KE AGENDA TV",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                if (savedToast != null) {
                    Text("✅ $savedToast", color = Color(0xFF81C784), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("Daftar Agenda Aktif (${agendas.size}) — Klik pada item atau tombol ✏️ Edit untuk mengedit:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))

        agendas.forEach { a ->
            val isBeingEdited = editingAgendaId == a.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isBeingEdited) Color(0xFF334155) else Color(0xFF1E293B))
                    .border(if (isBeingEdited) 1.5.dp else 1.dp, if (isBeingEdited) palette.primaryColor else Color(0xFF475569), RoundedCornerShape(8.dp))
                    .clickable {
                        editingAgendaId = a.id
                        time = a.time
                        title = a.title
                        location = a.location
                        pic = a.pic
                        status = a.status
                        savedToast = null
                    }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${a.time} • ${a.status}", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        if (isBeingEdited) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("• [SEDANG DIEDIT]", fontSize = 9.sp, fontWeight = FontWeight.Black, color = palette.primaryColor)
                        }
                    }
                    Text(a.title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("📍 ${a.location} ${if (a.pic.isNotBlank()) "• 👤 " + a.pic else ""}", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            editingAgendaId = a.id
                            time = a.time
                            title = a.title
                            location = a.location
                            pic = a.pic
                            status = a.status
                            savedToast = null
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3310B981))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Agenda", tint = Color(0xFFA7F3D0), modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = {
                            if (editingAgendaId == a.id) resetForm()
                            onDeleteAgenda(a.id)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33EF4444))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TabNoticeSignage(
    notices: List<NoticeItem>,
    palette: SignagePalette,
    onAddNotice: (NoticeItem) -> Unit,
    onUpdateNotice: (NoticeItem) -> Unit,
    onDeleteNotice: (Long) -> Unit
) {
    var editingNoticeId by remember { mutableStateOf<Long?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("INFO") }
    var dateText by remember { mutableStateOf("Hari Ini") }
    var savedToast by remember { mutableStateOf<String?>(null) }

    fun resetForm() {
        editingNoticeId = null
        title = ""
        content = ""
        priority = "INFO"
        dateText = "Hari Ini"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📢 Papan Pengumuman & Bulletin", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
            if (editingNoticeId != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.primaryColor.copy(alpha = 0.25f))
                        .border(1.dp, palette.primaryColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("✏️ Mode Edit Pengumuman (ID: #$editingNoticeId)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (editingNoticeId != null) palette.primaryColor else Color(0xFF475569))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingNoticeId != null) "✏️ Formulir Edit Pengumuman" else "➕ Buat Pengumuman Baru",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (editingNoticeId != null) palette.primaryColor else Color.White
                    )
                    if (editingNoticeId != null) {
                        Button(
                            onClick = {
                                resetForm()
                                savedToast = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Batal Edit ✕", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SignageInputField(value = priority, onValueChange = { priority = it; savedToast = null }, label = "Prioritas (URGENT/IMPORTANT/INFO)", placeholder = "INFO", palette = palette, modifier = Modifier.weight(1f))
                    SignageInputField(value = dateText, onValueChange = { dateText = it; savedToast = null }, label = "Tanggal / Waktu", placeholder = "Hari Ini", palette = palette, modifier = Modifier.weight(1f))
                }

                SignageInputField(value = title, onValueChange = { title = it; savedToast = null }, label = "Judul Pengumuman", placeholder = "Contoh: Pelaksanaan Gladi ANBK 2026", palette = palette, modifier = Modifier.fillMaxWidth())
                SignageInputField(value = content, onValueChange = { content = it; savedToast = null }, label = "Rincian Isi Pengumuman", placeholder = "Tuliskan detail pengumuman yang akan tampil di papan...", palette = palette, modifier = Modifier.fillMaxWidth(), minLines = 2)

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            if (editingNoticeId != null) {
                                onUpdateNotice(
                                    NoticeItem(
                                        id = editingNoticeId!!,
                                        title = title,
                                        content = content,
                                        priority = priority,
                                        dateText = dateText,
                                        displayOrder = 99,
                                        isActive = true
                                    )
                                )
                                savedToast = "Pengumuman Berhasil Diperbarui!"
                                resetForm()
                            } else {
                                onAddNotice(
                                    NoticeItem(
                                        title = title,
                                        content = content,
                                        priority = priority,
                                        dateText = dateText,
                                        displayOrder = 99,
                                        isActive = true
                                    )
                                )
                                savedToast = "Pengumuman Berhasil Diterbitkan!"
                                resetForm()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (editingNoticeId != null) palette.primaryColor else Color(0xFF10B981)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = if (editingNoticeId != null) "💾 SIMPAN PEMBARUAN PENGUMUMAN" else "➕ TERBITKAN PENGUMUMAN DI TV",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                if (savedToast != null) {
                    Text("✅ $savedToast", color = Color(0xFF81C784), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("Daftar Pengumuman Aktif (${notices.size}) — Klik pada item atau tombol ✏️ Edit untuk mengedit:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))

        notices.forEach { n ->
            val isBeingEdited = editingNoticeId == n.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isBeingEdited) Color(0xFF334155) else Color(0xFF1E293B))
                    .border(if (isBeingEdited) 1.5.dp else 1.dp, if (isBeingEdited) palette.primaryColor else Color(0xFF475569), RoundedCornerShape(8.dp))
                    .clickable {
                        editingNoticeId = n.id
                        title = n.title
                        content = n.content
                        priority = n.priority
                        dateText = n.dateText
                        savedToast = null
                    }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${n.priority} • ${n.dateText}", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = if (n.priority == "URGENT") Color(0xFFEF4444) else if (n.priority == "IMPORTANT") Color(0xFFF59E0B) else palette.primaryColor)
                        if (isBeingEdited) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("• [SEDANG DIEDIT]", fontSize = 9.sp, fontWeight = FontWeight.Black, color = palette.primaryColor)
                        }
                    }
                    Text(n.title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(n.content, fontSize = 10.sp, color = Color(0xFFCBD5E1), maxLines = 2, overflow = TextOverflow.Ellipsis)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            editingNoticeId = n.id
                            title = n.title
                            content = n.content
                            priority = n.priority
                            dateText = n.dateText
                            savedToast = null
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3310B981))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Pengumuman", tint = Color(0xFFA7F3D0), modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = {
                            if (editingNoticeId == n.id) resetForm()
                            onDeleteNotice(n.id)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33EF4444))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TabTickerSignage(
    tickers: List<RunningTickerItem>,
    config: SignageConfig,
    palette: SignagePalette,
    onSaveConfig: (SignageConfig) -> Unit,
    onAddTicker: (RunningTickerItem) -> Unit,
    onUpdateTicker: (RunningTickerItem) -> Unit,
    onDeleteTicker: (Long) -> Unit
) {
    var editingTickerId by remember { mutableStateOf<Long?>(null) }
    var badge by remember { mutableStateOf("PENGUMUMAN") }
    var text by remember { mutableStateOf("") }
    var speed by remember(config) { mutableIntStateOf(config.runningTextSpeed) }
    var savedToast by remember { mutableStateOf<String?>(null) }

    fun resetForm() {
        editingTickerId = null
        badge = "PENGUMUMAN"
        text = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📜 Running Text (Teks Berjalan Bawah TV)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
            if (editingTickerId != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.primaryColor.copy(alpha = 0.25f))
                        .border(1.dp, palette.primaryColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("✏️ Mode Edit Teks (ID: #$editingTickerId)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
                }
            }
        }

        // Speed setting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Kecepatan Teks Berjalan:", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0 to "Lambat", 1 to "Sedang", 2 to "Cepat").forEach { (spd, lbl) ->
                    val isSel = speed == spd
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) palette.primaryColor else Color(0xFF0F172A))
                            .clickable {
                                speed = spd
                                onSaveConfig(config.copy(runningTextSpeed = spd))
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(lbl, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) (if (palette.isDark) Color(0xFF0F172A) else Color.White) else Color.White)
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (editingTickerId != null) palette.primaryColor else Color(0xFF475569))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingTickerId != null) "✏️ Formulir Edit Running Text" else "➕ Tambah Running Text Baru",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (editingTickerId != null) palette.primaryColor else Color.White
                    )
                    if (editingTickerId != null) {
                        Button(
                            onClick = {
                                resetForm()
                                savedToast = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Batal Edit ✕", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                SignageInputField(value = badge, onValueChange = { badge = it; savedToast = null }, label = "Label Kategori (PENGUMUMAN / BERITA / INFO)", placeholder = "PENGUMUMAN", palette = palette, modifier = Modifier.fillMaxWidth())
                SignageInputField(value = text, onValueChange = { text = it; savedToast = null }, label = "Isi Kalimat Teks Berjalan", placeholder = "Tuliskan teks berjalan di sini...", palette = palette, modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            if (editingTickerId != null) {
                                onUpdateTicker(
                                    RunningTickerItem(
                                        id = editingTickerId!!,
                                        badge = badge,
                                        text = text,
                                        displayOrder = 99,
                                        isActive = true
                                    )
                                )
                                savedToast = "Teks Berjalan Berhasil Diperbarui!"
                                resetForm()
                            } else {
                                onAddTicker(
                                    RunningTickerItem(
                                        badge = badge,
                                        text = text,
                                        displayOrder = 99,
                                        isActive = true
                                    )
                                )
                                savedToast = "Teks Berjalan Berhasil Ditambahkan!"
                                resetForm()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (editingTickerId != null) palette.primaryColor else Color(0xFF10B981)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = if (editingTickerId != null) "💾 SIMPAN PEMBARUAN TEKS" else "➕ TAMBAH KE RUNNING TEXT TV",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                if (savedToast != null) {
                    Text("✅ $savedToast", color = Color(0xFF81C784), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("Daftar Teks Berjalan Aktif (${tickers.size}) — Klik pada item atau tombol ✏️ Edit untuk mengedit:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))

        tickers.forEach { t ->
            val isBeingEdited = editingTickerId == t.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isBeingEdited) Color(0xFF334155) else Color(0xFF1E293B))
                    .border(if (isBeingEdited) 1.5.dp else 1.dp, if (isBeingEdited) palette.primaryColor else Color(0xFF475569), RoundedCornerShape(8.dp))
                    .clickable {
                        editingTickerId = t.id
                        badge = t.badge
                        text = t.text
                        savedToast = null
                    }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(t.badge, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)
                        if (isBeingEdited) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("• [SEDANG DIEDIT]", fontSize = 9.sp, fontWeight = FontWeight.Black, color = palette.primaryColor)
                        }
                    }
                    Text(t.text, fontSize = 12.sp, color = Color.White)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            editingTickerId = t.id
                            badge = t.badge
                            text = t.text
                            savedToast = null
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3310B981))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Teks", tint = Color(0xFFA7F3D0), modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = {
                            if (editingTickerId == t.id) resetForm()
                            onDeleteTicker(t.id)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33EF4444))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TabFlashAlertSignage(
    config: SignageConfig,
    palette: SignagePalette,
    onSendFlashAlert: (String) -> Unit,
    onDismissFlashAlert: () -> Unit
) {
    var flashMessage by remember { mutableStateOf(config.activeFlashAlert) }
    var sentToast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🚨 Broadcast Pesan Kilat / Siaga TV", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))

        Text(
            text = "Kirimkan pesan mendesak atau pengumuman darurat yang langsung menimpa seluruh layar TV secara real-time.",
            fontSize = 11.sp,
            color = Color(0xFFCBD5E1)
        )

        SignageInputField(
            value = flashMessage,
            onValueChange = { flashMessage = it; sentToast = false },
            label = "Isi Pesan Siaga / Peringatan Darurat",
            placeholder = "Tulis pesan siaga darurat...",
            palette = palette,
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 3
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    if (flashMessage.isNotBlank()) {
                        onSendFlashAlert(flashMessage)
                        sentToast = true
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🚨 Broadcast ke Layar TV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    onDismissFlashAlert()
                    flashMessage = ""
                    sentToast = false
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("❌ Hapus Pesan di TV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        if (sentToast) {
            Text("🚨 Pesan Siaga berhasil dikirimkan ke layar TV!", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TabWidgetsToggleSignage(
    config: SignageConfig,
    palette: SignagePalette,
    onSaveConfig: (SignageConfig) -> Unit
) {
    var enableClock by remember(config) { mutableStateOf(config.enableClock) }
    var enableAgendas by remember(config) { mutableStateOf(config.enableAgendas) }
    var enableNotices by remember(config) { mutableStateOf(config.enableNotices) }
    var enableQuickInfo by remember(config) { mutableStateOf(config.enableQuickInfo) }
    var slideInterval by remember(config) { mutableIntStateOf(config.slideIntervalSeconds) }
    var savedToast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("⚙️ Pengaturan Visibilitas Widget & Layar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.primaryColor)

        val widgetToggles = listOf(
            Triple("⏰ Jam Digital & Tanggal Live", enableClock) { v: Boolean -> enableClock = v },
            Triple("📅 Widget Agenda & Kegiatan", enableAgendas) { v: Boolean -> enableAgendas = v },
            Triple("📢 Widget Papan Pengumuman", enableNotices) { v: Boolean -> enableNotices = v },
            Triple("ℹ️ Bar Quick Info & Status Layar", enableQuickInfo) { v: Boolean -> enableQuickInfo = v }
        )

        widgetToggles.forEach { (label, isChecked, onToggle) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF475569), RoundedCornerShape(8.dp))
                    .clickable { onToggle(!isChecked) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Switch(
                    checked = isChecked,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = palette.primaryColor)
                )
            }
        }

        // Interval durasi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Durasi Default Slide:", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(5, 7, 10, 15).forEach { sec ->
                    val isSel = slideInterval == sec
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) palette.primaryColor else Color(0xFF0F172A))
                            .clickable { slideInterval = sec }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("${sec}s", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) (if (palette.isDark) Color(0xFF0F172A) else Color.White) else Color.White)
                    }
                }
            }
        }

        Button(
            onClick = {
                onSaveConfig(
                    config.copy(
                        enableClock = enableClock,
                        enableAgendas = enableAgendas,
                        enableNotices = enableNotices,
                        enableQuickInfo = enableQuickInfo,
                        slideIntervalSeconds = slideInterval
                    )
                )
                savedToast = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryColor),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Text(
                "💾 Simpan Opsi Widget & Layar",
                color = if (palette.isDark) Color(0xFF0F172A) else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        if (savedToast) {
            Text("✅ Pengaturan Widget Berhasil Disimpan!", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TabTelegramSignage(
    config: SignageConfig,
    palette: SignagePalette,
    onSaveConfig: (SignageConfig) -> Unit
) {
    var telegramEnabled by remember(config) { mutableStateOf(config.telegramBotEnabled) }
    var telegramToken by remember(config) { mutableStateOf(config.telegramBotToken) }
    var telegramUserIds by remember(config) { mutableStateOf(config.telegramBotAuthorizedUserIds) }
    var savedToast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🤖 Konfigurasi Remote Telegram Bot", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (telegramEnabled) Color(0x3310B981) else Color(0x3364748B))
                    .border(1.dp, if (telegramEnabled) Color(0xFF10B981) else Color(0xFF64748B), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (telegramEnabled) "● BOT AKTIF" else "○ NONAKTIF",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (telegramEnabled) Color(0xFFA7F3D0) else Color(0xFF94A3B8)
                )
            }
        }

        Text(
            text = "Kontrol nama sekolah/instansi, tema, slide poster foto, running text, agenda, pengumuman, dan pesan kilat TV secara real-time langsung lewat chat Telegram di HP Anda!",
            fontSize = 11.sp,
            color = Color(0xFFCBD5E1),
            lineHeight = 16.sp
        )

        // Toggle Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
                .border(1.dp, Color(0xFF475569), RoundedCornerShape(8.dp))
                .clickable { telegramEnabled = !telegramEnabled }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Aktifkan Remote Telegram Bot", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text("TV akan menerima perintah dan foto dari Telegram", fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
            Switch(
                checked = telegramEnabled,
                onCheckedChange = { telegramEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
            )
        }

        SignageInputField(
            value = telegramToken,
            onValueChange = { telegramToken = it; savedToast = false },
            label = "Token Bot Telegram (Dibuat via @BotFather)",
            placeholder = "Contoh: 1234567890:ABCdefGHIjklMNOpqrsTUVwxyz",
            palette = palette,
            modifier = Modifier.fillMaxWidth()
        )

        SignageInputField(
            value = telegramUserIds,
            onValueChange = { telegramUserIds = it; savedToast = false },
            label = "User ID / Chat ID Admin yang Diizinkan (Whitelist)",
            placeholder = "Contoh: 123456789 (Kosongkan untuk otomatis daftarkan akun pengirim pertama)",
            palette = palette,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSaveConfig(
                    config.copy(
                        telegramBotEnabled = telegramEnabled,
                        telegramBotToken = telegramToken.trim(),
                        telegramBotAuthorizedUserIds = telegramUserIds.trim()
                    )
                )
                savedToast = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Text(
                "💾 Simpan Konfigurasi Telegram Bot",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        if (savedToast) {
            Text("✅ Konfigurasi Telegram Bot Berhasil Disimpan!", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Guide Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("📖 Panduan Perintah Telegram Bot:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                Text("• /nama [Nama Baru] : Ubah nama sekolah/instansi di TV", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text("• /slogan [Slogan Baru] : Ubah slogan lembaga", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text("• /banner [Teks] : Ubah teks status akreditasi di header", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text("• /tema [school/gov/cyber/minimal/sunset] : Ubah tema warna", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text("• /pesan [Pesan] : Tampilkan pesan kilat siaga di TV (/hapuspesan)", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text("• Kirim Foto : Otomatis jadi slide poster di TV!", fontSize = 10.sp, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold)
                Text("• /text [Teks] : Tambah kalimat running text bawah TV", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text("• /agenda [Jam] | [Acara] | [Lokasi] : Tambah agenda", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text("• /status : Cek info IP & status tayangan TV", fontSize = 10.sp, color = Color(0xFFE2E8F0))
            }
        }
    }
}

