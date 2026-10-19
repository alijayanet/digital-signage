package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.DefaultDataSeeder
import com.example.data.db.SignageDatabase
import com.example.data.db.SignageRepository
import com.example.data.entity.*
import com.example.notification.TelegramSignageBotManager
import com.example.server.SignageHttpServer
import com.example.ui.theme.SignagePalette
import com.example.ui.theme.SignageThemePalettes
import com.example.util.NetworkUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject

data class SignageUiState(
    val config: SignageConfig = SignageConfig(),
    val slides: List<SlideItem> = emptyList(),
    val agendas: List<AgendaItem> = emptyList(),
    val notices: List<NoticeItem> = emptyList(),
    val tickers: List<RunningTickerItem> = emptyList(),
    val serverUrl: String = "http://127.0.0.1:8080",
    val isAdminModalOpen: Boolean = false,
    val isLoading: Boolean = true
)

class SignageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SignageRepository
    private val httpServer: SignageHttpServer
    private val chimePlayer = com.example.util.AttendanceChimePlayer(application)

    private val _attendancePopupResult = MutableStateFlow<AttendanceScanResult?>(null)
    val attendancePopupResult: StateFlow<AttendanceScanResult?> = _attendancePopupResult.asStateFlow()
    private var dismissPopupJob: Job? = null

    private val _isAdminModalOpen = MutableStateFlow(false)
    val isAdminModalOpen: StateFlow<Boolean> = _isAdminModalOpen.asStateFlow()


    private val _serverUrl = MutableStateFlow("http://127.0.0.1:8080")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    val uiState: StateFlow<SignageUiState>

    private var telegramJob: Job? = null
    private var lastTelegramOffset: Long = 0L

    init {
        val db = SignageDatabase.getDatabase(application)
        repository = SignageRepository(db.signageDao(), application)
        httpServer = SignageHttpServer(application, repository, 8080)

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
            _serverUrl.value = NetworkUtils.getServerUrl(application, 8080)
            httpServer.start()
        }

        uiState = combine(
            repository.configFlow,
            repository.activeSlidesFlow,
            repository.activeAgendasFlow,
            repository.activeNoticesFlow,
            repository.activeTickersFlow,
            _isAdminModalOpen,
            _serverUrl
        ) { values ->
            val cfg = values[0] as? SignageConfig ?: SignageConfig()
            val slides = (values[1] as? List<*>)?.filterIsInstance<SlideItem>() ?: emptyList()
            val agendas = (values[2] as? List<*>)?.filterIsInstance<AgendaItem>() ?: emptyList()
            val notices = (values[3] as? List<*>)?.filterIsInstance<NoticeItem>() ?: emptyList()
            val tickers = (values[4] as? List<*>)?.filterIsInstance<RunningTickerItem>() ?: emptyList()
            val adminOpen = values[5] as? Boolean ?: false
            val url = values[6] as? String ?: "http://127.0.0.1:8080"

            SignageUiState(
                config = cfg,
                slides = slides,
                agendas = agendas,
                notices = notices,
                tickers = tickers,
                serverUrl = url,
                isAdminModalOpen = adminOpen,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SignageUiState(isLoading = true)
        )

        // Observe config for Telegram Bot management
        viewModelScope.launch {
            repository.configFlow.collectLatest { cfg ->
                if (cfg != null) {
                    syncTelegramBotWorker(cfg)
                }
            }
        }
    }

    private fun syncTelegramBotWorker(config: SignageConfig) {
        if (config.telegramBotEnabled && config.telegramBotToken.isNotBlank()) {
            if (telegramJob?.isActive != true) {
                startTelegramPolling(config.telegramBotToken.trim())
            }
        } else {
            telegramJob?.cancel()
            telegramJob = null
        }
    }

    private fun startTelegramPolling(token: String) {
        telegramJob?.cancel()
        telegramJob = viewModelScope.launch {
            Log.i("SignageTelegram", "Telegram bot polling starting...")
            try {
                TelegramSignageBotManager.deleteWebhook(token)
            } catch (e: Exception) {
                Log.w("SignageTelegram", "deleteWebhook notice: ${e.message}")
            }

            while (isActive) {
                try {
                    val cfg = uiState.value.config
                    if (!cfg.telegramBotEnabled || cfg.telegramBotToken.isBlank()) {
                        break
                    }

                    val jsonStr = TelegramSignageBotManager.getUpdates(token, lastTelegramOffset, timeoutSeconds = 20)
                    if (!jsonStr.isNullOrBlank()) {
                        val root = JSONObject(jsonStr)
                        if (root.optBoolean("ok", false)) {
                            val result = root.optJSONArray("result")
                            if (result != null && result.length() > 0) {
                                for (i in 0 until result.length()) {
                                    val update = result.getJSONObject(i)
                                    val updateId = update.optLong("update_id", 0L)
                                    if (updateId >= lastTelegramOffset) {
                                        lastTelegramOffset = updateId + 1
                                    }
                                    try {
                                        handleTelegramUpdate(token, update)
                                    } catch (ex: Exception) {
                                        Log.e("SignageTelegram", "Error handling update #$updateId: ${ex.message}", ex)
                                    }
                                }
                            }
                        }
                    }
                } catch (_: kotlinx.coroutines.CancellationException) {
                    break
                } catch (e: Exception) {
                    Log.d("SignageTelegram", "Polling loop notice: ${e.message}")
                    delay(3000)
                }
            }
            Log.i("SignageTelegram", "Telegram bot polling stopped.")
        }
    }

    private fun isTelegramUserAuthorized(userId: Long): Boolean {
        val config = uiState.value.config
        val whitelist = config.getTelegramAuthorizedIdList()
        // If whitelist is empty, auto-register the first user so they can immediately configure
        if (whitelist.isEmpty()) {
            viewModelScope.launch {
                val updated = config.copy(telegramBotAuthorizedUserIds = userId.toString())
                repository.updateConfig(updated)
            }
            return true
        }
        return whitelist.contains(userId)
    }

    private suspend fun handleTelegramUpdate(token: String, update: JSONObject) {
        val config = uiState.value.config

        // 1. Handle Callback Query (Inline Button Click)
        val cq = update.optJSONObject("callback_query")
        if (cq != null) {
            handleTelegramCallback(token, cq, config)
            return
        }

        // 2. Handle Regular Message (Text / Photo)
        val msg = update.optJSONObject("message") ?: return
        val from = msg.optJSONObject("from")
        val fromId = from?.optLong("id", 0L) ?: 0L
        val chatId = msg.optJSONObject("chat")?.optLong("id", 0L) ?: fromId

        if (!isTelegramUserAuthorized(fromId)) {
            TelegramSignageBotManager.sendMessage(
                token, chatId,
                "⛔ *Akses Ditolak*\nAkun Telegram Anda (ID: `$fromId`) belum terdaftar sebagai admin resmi TV.\n\nSilakan masukkan ID `$fromId` ke daftar User ID yang diizinkan melalui Web Remote TV atau menu pengaturan."
            )
            return
        }

        // Photo Upload Handler (Telegram user sends image directly -> TV slide)
        val photos = msg.optJSONArray("photo")
        val doc = msg.optJSONObject("document")
        val isImageDoc = doc != null && (doc.optString("mime_type", "").startsWith("image/") ||
                listOf(".jpg", ".jpeg", ".png", ".webp").any { doc.optString("file_name", "").endsWith(it, true) })

        if ((photos != null && photos.length() > 0) || isImageDoc) {
            handleTelegramPhotoUpload(token, chatId, msg, photos, doc)
            return
        }

        var text = msg.optString("text", "").trim()
        if (text.isBlank()) return

        // Strip @BotUsername if present
        if (text.startsWith("/")) {
            val cmdPart = text.substringBefore(" ")
            val argPart = if (text.contains(" ")) " " + text.substringAfter(" ") else ""
            val cleanCmd = cmdPart.substringBefore("@")
            text = cleanCmd + argPart
        }

        handleTelegramTextCommand(token, chatId, text, config)
    }

    private suspend fun handleTelegramCallback(token: String, cq: JSONObject, config: SignageConfig) {
        val cqId = cq.optString("id", "")
        val from = cq.optJSONObject("from")
        val fromId = from?.optLong("id", 0L) ?: 0L
        val msg = cq.optJSONObject("message")
        val chatId = msg?.optJSONObject("chat")?.optLong("id", 0L) ?: fromId
        val data = cq.optString("data", "")

        if (!isTelegramUserAuthorized(fromId)) {
            TelegramSignageBotManager.answerCallbackQuery(token, cqId, "⛔ Akun Anda belum terdaftar!")
            return
        }

        TelegramSignageBotManager.answerCallbackQuery(token, cqId)

        when (data) {
            "menu_main" -> {
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📺 *PANEL KONTROL DIGITAL SIGNAGE TV*\n*${config.orgName}*\n━━━━━━━━━━━━━━━━━━━━\nPilih menu yang ingin Anda kelola:",
                    TelegramSignageBotManager.buildMainMenuKeyboard()
                )
            }
            "menu_profil" -> {
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "🏢 *PROFIL & IDENTITAS LEMBAGA*\n━━━━━━━━━━━━━━━━━━━━\n• *Nama:* ${config.orgName}\n• *Slogan:* ${config.orgSubtitle}\n• *Banner Akreditasi:* ${config.headerBannerText}\n• *Tipe:* ${config.orgType}\n\n💡 *Perintah Cepat:*\n• `/nama [Nama Baru]`\n• `/slogan [Slogan Baru]`\n• `/banner [Teks Banner]`"
                )
            }
            "menu_tema" -> {
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "🎨 *PILIH TEMA LAYAR TV*\nTema saat ini: *${config.themeId}*\n━━━━━━━━━━━━━━━━━━━━\nPilih tema di bawah untuk langsung mengubah warna TV:",
                    TelegramSignageBotManager.buildThemeMenuKeyboard()
                )
            }
            "set_theme_school" -> {
                updateTheme("school_modern")
                TelegramSignageBotManager.sendMessage(token, chatId, "✅ Tema TV berhasil diubah ke *Sekolah Modern* (Hijau / Cyan)!")
            }
            "set_theme_gov" -> {
                updateTheme("gov_nusantara")
                TelegramSignageBotManager.sendMessage(token, chatId, "✅ Tema TV berhasil diubah ke *Gov Nusantara* (Biru / Emas)!")
            }
            "set_theme_cyber" -> {
                updateTheme("cyber_blue")
                TelegramSignageBotManager.sendMessage(token, chatId, "✅ Tema TV berhasil diubah ke *Cyber Tech* (Cyan / Biru)!")
            }
            "set_theme_minimal" -> {
                updateTheme("minimal_clean")
                TelegramSignageBotManager.sendMessage(token, chatId, "✅ Tema TV berhasil diubah ke *Minimal Clean* (Indigo / Terang)!")
            }
            "set_theme_sunset" -> {
                updateTheme("sunset_warm")
                TelegramSignageBotManager.sendMessage(token, chatId, "✅ Tema TV berhasil diubah ke *Sunset Gold* (Merah / Emas)!")
            }
            "set_theme_m3" -> {
                updateTheme("high_density")
                TelegramSignageBotManager.sendMessage(token, chatId, "✅ Tema TV berhasil diubah ke *High Density M3*!")
            }
            "menu_preset" -> {
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "✨ *TEMPLATE INSTAN (1-KLIK)*\n━━━━━━━━━━━━━━━━━━━━\nOtomatis menyesuaikan profil, slide, agenda, dan running text TV:",
                    TelegramSignageBotManager.buildPresetMenuKeyboard()
                )
            }
            "apply_preset_school" -> {
                applyPreset("school")
                TelegramSignageBotManager.sendMessage(token, chatId, "🏫 *Template Sekolah Berhasil Diterapkan ke Layar TV!*")
            }
            "apply_preset_gov" -> {
                applyPreset("gov")
                TelegramSignageBotManager.sendMessage(token, chatId, "🏛️ *Template Pemerintahan / PTSP Berhasil Diterapkan ke Layar TV!*")
            }
            "menu_slide" -> {
                val count = uiState.value.slides.size
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "🖼️ *SLIDE & VIDEO TV*\nJumlah slide aktif: *$count slide*\n\n💡 *Cara Tambah Slide:*\n1. Kirim Foto langsung ke chat ini (otomatis jadi slide TV)\n2. Atau gunakan: `/slide [Judul] | [Keterangan]`"
                )
            }
            "menu_ticker" -> {
                val tickers = uiState.value.tickers
                val listStr = if (tickers.isEmpty()) "Belum ada running text." else tickers.joinToString("\n") { "• [${it.badge}] ${it.text}" }
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📜 *RUNNING TEXT (TEKS BERJALAN)*\n━━━━━━━━━━━━━━━━━━━━\n$listStr\n\n💡 *Tambah Teks Berjalan:*\n`/text [Isi Kalimat Teks Berjalan]`"
                )
            }
            "menu_agenda" -> {
                val agendas = uiState.value.agendas
                val listStr = if (agendas.isEmpty()) "Belum ada agenda hari ini." else agendas.joinToString("\n") { "• *${it.time}*: ${it.title} (${it.location})" }
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📅 *AGENDA & JADWAL HARI INI*\n━━━━━━━━━━━━━━━━━━━━\n$listStr\n\n💡 *Tambah Agenda:*\n`/agenda [Waktu] | [Judul] | [Lokasi] | [PIC]`\nContoh: `/agenda 08:00 - 10:00 | Rapat Koordinasi | Aula | Kepsek`"
                )
            }
            "menu_notice" -> {
                val notices = uiState.value.notices
                val listStr = if (notices.isEmpty()) "Belum ada pengumuman." else notices.joinToString("\n") { "• [${it.priority}] *${it.title}*\n${it.content}" }
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📢 *PAPAN PENGUMUMAN*\n━━━━━━━━━━━━━━━━━━━━\n$listStr\n\n💡 *Tambah Pengumuman:*\n`/pengumuman [Judul] | [Isi Pengumuman]`"
                )
            }
            "menu_alert" -> {
                val active = config.activeFlashAlert
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "🚨 *PESAN KILAT / SIAGA TV*\nStatus saat ini: *${if (active.isNotBlank()) "AKTIF: \"$active\"" else "Tidak ada pesan aktif"}*\n\n💡 *Perintah:*\n• `/pesan [Pesan Siaga]` (Tampilkan ke layar TV)\n• `/hapuspesan` (Hapus pesan di TV)"
                )
            }
            "cmd_status" -> {
                val ip = NetworkUtils.getIpAddress(getApplication())
                val state = uiState.value
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📊 *STATUS DIGITAL SIGNAGE TV*\n━━━━━━━━━━━━━━━━━━━━\n" +
                            "• *Lembaga:* ${state.config.orgName}\n" +
                            "• *Slogan:* ${state.config.orgSubtitle}\n" +
                            "• *Tema TV:* ${state.config.themeId}\n" +
                            "• *IP Web Remote:* `http://$ip:8080`\n" +
                            "• *Slide Aktif:* ${state.slides.size} slide\n" +
                            "• *Agenda Hari Ini:* ${state.agendas.size} agenda\n" +
                            "• *Pengumuman:* ${state.notices.size} info\n" +
                            "• *Running Text:* ${state.tickers.size} item\n" +
                            "• *Pesan Kilat:* ${if (state.config.activeFlashAlert.isNotBlank()) "AKTIF" else "Nonaktif"}"
                )
            }
            "cmd_help" -> {
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📖 *DAFTAR PERINTAH TELEGRAM BOT*\n━━━━━━━━━━━━━━━━━━━━\n" +
                            "• `/nama [Nama]` : Ubah nama lembaga\n" +
                            "• `/slogan [Slogan]` : Ubah slogan lembaga\n" +
                            "• `/banner [Teks]` : Ubah teks status akreditasi\n" +
                            "• `/tema [Nama Tema]` : Ubah tema warna TV\n" +
                            "• `/pesan [Teks]` : Broadcast pesan kilat darurat di TV\n" +
                            "• `/hapuspesan` : Hapus pesan kilat di TV\n" +
                            "• `/text [Teks]` : Tambah teks berjalan di bawah TV\n" +
                            "• `/slide [Judul] | [Keterangan]` : Tambah slide informasi\n" +
                            "• *Kirim Foto Langsung* : Otomatis jadi Slide TV!\n" +
                            "• `/agenda [Jam] | [Judul] | [Lokasi]` : Tambah agenda\n" +
                            "• `/pengumuman [Judul] | [Isi]` : Tambah pengumuman\n" +
                            "• `/status` : Cek IP dan status layar TV\n" +
                            "• `/menu` : Tampilkan menu tombol interaktif"
                )
            }
        }
    }

    private suspend fun handleTelegramTextCommand(token: String, chatId: Long, text: String, config: SignageConfig) {
        val parts = text.split(" ", limit = 2)
        val cmd = parts[0].lowercase()
        val arg = if (parts.size > 1) parts[1].trim() else ""

        when (cmd) {
            "/start", "/menu" -> {
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📺 *SELAMAT DATANG DI PANEL TV SIGNAGE*\n*${config.orgName}*\n━━━━━━━━━━━━━━━━━━━━\nAnda dapat mengelola TV secara langsung lewat Telegram kapan saja dan di mana saja!",
                    TelegramSignageBotManager.buildMainMenuKeyboard()
                )
            }
            "/status" -> {
                val ip = NetworkUtils.getIpAddress(getApplication())
                val state = uiState.value
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📊 *STATUS DIGITAL SIGNAGE TV*\n━━━━━━━━━━━━━━━━━━━━\n" +
                            "• *Lembaga:* ${state.config.orgName}\n" +
                            "• *Slogan:* ${state.config.orgSubtitle}\n" +
                            "• *Tema TV:* ${state.config.themeId}\n" +
                            "• *IP Web Remote:* `http://$ip:8080`\n" +
                            "• *Slide Aktif:* ${state.slides.size} slide\n" +
                            "• *Agenda Hari Ini:* ${state.agendas.size} agenda\n" +
                            "• *Pengumuman:* ${state.notices.size} info\n" +
                            "• *Running Text:* ${state.tickers.size} item"
                )
            }
            "/help", "/bantuan" -> {
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "📖 *PANDUAN PERINTAH KONTROL TV*\n━━━━━━━━━━━━━━━━━━━━\n" +
                            "• `/nama [Nama Baru]` -> Ganti nama instansi\n" +
                            "• `/slogan [Slogan Baru]` -> Ganti slogan visi misi\n" +
                            "• `/banner [Teks Banner]` -> Ganti teks akreditasi\n" +
                            "• `/tema [Nama Tema]` -> Ganti tema TV\n" +
                            "• `/pesan [Pesan Siaga]` -> Kirim pesan darurat TV\n" +
                            "• `/hapuspesan` -> Tutup pesan darurat di TV\n" +
                            "• `/text [Teks Berjalan]` -> Tambah running text\n" +
                            "• `/slide [Judul] | [Keterangan]` -> Tambah slide\n" +
                            "• *Kirim Foto* -> Otomatis jadi slide poster di TV!\n" +
                            "• `/agenda [Jam] | [Judul] | [Lokasi]` -> Tambah agenda\n" +
                            "• `/pengumuman [Judul] | [Isi]` -> Tambah pengumuman\n" +
                            "• `/status` -> Cek info TV & IP address"
                )
            }
            "/nama" -> {
                if (arg.isBlank()) {
                    TelegramSignageBotManager.sendMessage(token, chatId, "⚠️ Format salah! Gunakan: `/nama [Nama Sekolah/Instansi Baru]`")
                } else {
                    val updated = config.copy(orgName = arg)
                    saveConfig(updated)
                    TelegramSignageBotManager.sendMessage(token, chatId, "✅ *Nama Lembaga Berhasil Diubah!* \nNama Baru: *$arg*")
                }
            }
            "/slogan" -> {
                if (arg.isBlank()) {
                    TelegramSignageBotManager.sendMessage(token, chatId, "⚠️ Format salah! Gunakan: `/slogan [Slogan Baru]`")
                } else {
                    val updated = config.copy(orgSubtitle = arg)
                    saveConfig(updated)
                    TelegramSignageBotManager.sendMessage(token, chatId, "✅ *Slogan Lembaga Berhasil Diubah!* \nSlogan: *$arg*")
                }
            }
            "/banner" -> {
                if (arg.isBlank()) {
                    TelegramSignageBotManager.sendMessage(token, chatId, "⚠️ Format salah! Gunakan: `/banner [Teks Status Akreditasi]`")
                } else {
                    val updated = config.copy(headerBannerText = arg)
                    saveConfig(updated)
                    TelegramSignageBotManager.sendMessage(token, chatId, "✅ *Banner Status Header TV Diperbarui!* \nTeks: *$arg*")
                }
            }
            "/tema" -> {
                val themeId = when (arg.lowercase()) {
                    "sekolah", "school", "school_modern" -> "school_modern"
                    "gov", "pemerintahan", "gov_nusantara" -> "gov_nusantara"
                    "cyber", "cyber_blue" -> "cyber_blue"
                    "minimal", "minimal_clean" -> "minimal_clean"
                    "sunset", "sunset_warm" -> "sunset_warm"
                    "m3", "high_density" -> "high_density"
                    else -> ""
                }
                if (themeId.isBlank()) {
                    TelegramSignageBotManager.sendMessage(
                        token, chatId,
                        "⚠️ Pilih tema: `school`, `gov`, `cyber`, `minimal`, `sunset`, atau `m3`.\nAtau ketik `/menu` untuk memilih lewat tombol.",
                        TelegramSignageBotManager.buildThemeMenuKeyboard()
                    )
                } else {
                    updateTheme(themeId)
                    TelegramSignageBotManager.sendMessage(token, chatId, "✅ Tema TV berhasil diubah ke: *$themeId*")
                }
            }
            "/pesan", "/alert" -> {
                if (arg.isBlank()) {
                    TelegramSignageBotManager.sendMessage(token, chatId, "⚠️ Format salah! Gunakan: `/pesan [Isi Pesan Kilat/Siaga]`")
                } else {
                    setFlashAlert(arg)
                    TelegramSignageBotManager.sendMessage(token, chatId, "🚨 *Pesan Siaga Berhasil Dikirimkan ke Layar TV!* \nPesan: \"$arg\"\n\nKetik `/hapuspesan` untuk menghapusnya.")
                }
            }
            "/hapuspesan", "/clearalert" -> {
                dismissFlashAlert()
                TelegramSignageBotManager.sendMessage(token, chatId, "✅ Pesan siaga darurat pada layar TV telah dinonaktifkan.")
            }
            "/text", "/runningtext", "/ticker" -> {
                if (arg.isBlank()) {
                    TelegramSignageBotManager.sendMessage(token, chatId, "⚠️ Format salah! Gunakan: `/text [Kalimat Teks Berjalan]`")
                } else {
                    val ticker = RunningTickerItem(badge = "PENGUMUMAN", text = arg)
                    addTicker(ticker)
                    TelegramSignageBotManager.sendMessage(token, chatId, "✅ *Running Text Berhasil Ditambahkan ke Bawah Layar TV!* \n\"$arg\"")
                }
            }
            "/slide" -> {
                if (arg.isBlank()) {
                    TelegramSignageBotManager.sendMessage(token, chatId, "⚠️ Format salah! Gunakan: `/slide [Judul] | [Keterangan]`\nAtau langsung kirimkan foto poster ke bot ini!")
                } else {
                    val sub = arg.split("|", limit = 2)
                    val title = sub[0].trim()
                    val caption = if (sub.size > 1) sub[1].trim() else ""
                    val slide = SlideItem(
                        title = title,
                        caption = caption,
                        category = "INFORMASI",
                        imageUrl = "preset_school_1",
                        mediaType = "IMAGE",
                        durationSeconds = 8
                    )
                    addSlide(slide)
                    TelegramSignageBotManager.sendMessage(token, chatId, "✅ *Slide Informasi Berhasil Ditambahkan!* \nJudul: *$title*")
                }
            }
            "/agenda" -> {
                if (arg.isBlank()) {
                    TelegramSignageBotManager.sendMessage(token, chatId, "⚠️ Format salah! Gunakan:\n`/agenda [Waktu] | [Nama Kegiatan] | [Ruangan] | [PIC]`")
                } else {
                    val sub = arg.split("|")
                    val time = sub.getOrNull(0)?.trim() ?: "Hari Ini"
                    val title = sub.getOrNull(1)?.trim() ?: "Kegiatan"
                    val loc = sub.getOrNull(2)?.trim() ?: "Aula"
                    val pic = sub.getOrNull(3)?.trim() ?: ""
                    val agenda = AgendaItem(time = time, title = title, location = loc, pic = pic, status = "UPCOMING")
                    addAgenda(agenda)
                    TelegramSignageBotManager.sendMessage(token, chatId, "✅ *Agenda Kegiatan Berhasil Ditambahkan ke TV!* \n• Waktu: $time\n• Acara: $title\n• Lokasi: $loc")
                }
            }
            "/pengumuman", "/notice" -> {
                if (arg.isBlank()) {
                    TelegramSignageBotManager.sendMessage(token, chatId, "⚠️ Format salah! Gunakan:\n`/pengumuman [Judul] | [Isi Maklumat]`")
                } else {
                    val sub = arg.split("|", limit = 2)
                    val title = sub[0].trim()
                    val content = if (sub.size > 1) sub[1].trim() else ""
                    val notice = NoticeItem(title = title, content = content, priority = "INFO", dateText = "Hari Ini")
                    addNotice(notice)
                    TelegramSignageBotManager.sendMessage(token, chatId, "✅ *Pengumuman Berhasil Diterbitkan di TV!* \nJudul: *$title*")
                }
            }
            "/template" -> {
                if (arg.equals("gov", true)) {
                    applyPreset("gov")
                    TelegramSignageBotManager.sendMessage(token, chatId, "🏛️ Template Pemerintahan / PTSP berhasil diterapkan ke TV!")
                } else {
                    applyPreset("school")
                    TelegramSignageBotManager.sendMessage(token, chatId, "🏫 Template Sekolah berhasil diterapkan ke TV!")
                }
            }
            else -> {
                TelegramSignageBotManager.sendMessage(
                    token, chatId,
                    "❓ Perintah tidak dikenali. Ketik `/menu` untuk melihat pilihan kontrol.",
                    TelegramSignageBotManager.buildMainMenuKeyboard()
                )
            }
        }
    }

    private suspend fun handleTelegramPhotoUpload(
        token: String,
        chatId: Long,
        msg: JSONObject,
        photos: org.json.JSONArray?,
        doc: JSONObject?
    ) {
        val fileId = if (photos != null && photos.length() > 0) {
            photos.getJSONObject(photos.length() - 1).getString("file_id")
        } else {
            doc?.getString("file_id") ?: ""
        }

        if (fileId.isBlank()) return

        TelegramSignageBotManager.sendMessage(token, chatId, "⏳ Sedang memproses dan mengunduh foto poster ke TV...")

        val localUrl = TelegramSignageBotManager.downloadTelegramPhoto(getApplication(), token, fileId)
        if (localUrl != null) {
            val caption = msg.optString("caption", "").trim()
            val title = if (caption.isNotBlank()) caption.lines().first() else "Informasi Poster"
            val bodyCaption = if (caption.contains("\n")) caption.substringAfter("\n") else ""

            val slide = SlideItem(
                title = title,
                caption = bodyCaption,
                category = "POSTER",
                imageUrl = localUrl,
                mediaType = "IMAGE",
                durationSeconds = 10
            )
            addSlide(slide)
            TelegramSignageBotManager.sendMessage(
                token, chatId,
                "✅ *Foto Berhasil Dijadikan Slide di Layar TV!*\n• Judul: *$title*\n• Durasi Tampil: *10 Detik*"
            )
        } else {
            TelegramSignageBotManager.sendMessage(token, chatId, "❌ Gagal mengunduh gambar. Periksa koneksi internet TV.")
        }
    }

    fun openAdminModal() {
        _isAdminModalOpen.value = true
    }

    fun closeAdminModal() {
        _isAdminModalOpen.value = false
    }

    fun applyPreset(presetType: String) {
        viewModelScope.launch {
            val preset = if (presetType == "gov") {
                DefaultDataSeeder.getGovPreset()
            } else {
                DefaultDataSeeder.getSchoolPreset()
            }
            repository.applyPreset(preset)
        }
    }

    fun updateTheme(themeId: String) {
        viewModelScope.launch {
            val current = repository.getConfig()
            repository.updateConfig(current.copy(themeId = themeId))
        }
    }

    fun saveConfig(newConfig: SignageConfig) {
        viewModelScope.launch {
            repository.updateConfig(newConfig)
        }
    }

    fun setFlashAlert(message: String) {
        viewModelScope.launch {
            repository.setFlashAlert(message)
        }
    }

    fun dismissFlashAlert() {
        viewModelScope.launch {
            repository.setFlashAlert("")
        }
    }

    fun addSlide(slide: SlideItem) {
        viewModelScope.launch {
            repository.addSlide(slide)
        }
    }

    fun updateSlide(slide: SlideItem) {
        viewModelScope.launch {
            repository.updateSlide(slide)
        }
    }

    fun deleteSlide(id: Long) {
        viewModelScope.launch {
            repository.deleteSlide(id)
        }
    }

    fun addAgenda(agenda: AgendaItem) {
        viewModelScope.launch {
            repository.addAgenda(agenda)
        }
    }

    fun updateAgenda(agenda: AgendaItem) {
        viewModelScope.launch {
            repository.updateAgenda(agenda)
        }
    }

    fun deleteAgenda(id: Long) {
        viewModelScope.launch {
            repository.deleteAgenda(id)
        }
    }

    fun addNotice(notice: NoticeItem) {
        viewModelScope.launch {
            repository.addNotice(notice)
        }
    }

    fun updateNotice(notice: NoticeItem) {
        viewModelScope.launch {
            repository.updateNotice(notice)
        }
    }

    fun deleteNotice(id: Long) {
        viewModelScope.launch {
            repository.deleteNotice(id)
        }
    }

    fun addTicker(ticker: RunningTickerItem) {
        viewModelScope.launch {
            repository.addTicker(ticker)
        }
    }

    fun updateTicker(ticker: RunningTickerItem) {
        viewModelScope.launch {
            repository.updateTicker(ticker)
        }
    }

    fun deleteTicker(id: Long) {
        viewModelScope.launch {
            repository.deleteTicker(id)
        }
    }

    // RFID Attendance Handling
    fun onRfidScanned(rawUid: String) {
        val uid = rawUid.trim()
        if (uid.isBlank()) return
        val cfg = uiState.value.config
        if (!cfg.enableAttendance) return

        viewModelScope.launch {
            val result = repository.processAttendanceScan(uid)
            _attendancePopupResult.value = result

            // Audio Feedback (Bell chime only - strictly no TTS)
            when (result) {
                is AttendanceScanResult.Success -> {
                    if (result.isCheckIn) {
                        chimePlayer.playCheckInChime()
                    } else {
                        chimePlayer.playCheckOutChime()
                    }
                }
                is AttendanceScanResult.AlreadyRecorded -> {
                    chimePlayer.playWarningChime()
                }
                is AttendanceScanResult.UnknownCard -> {
                    chimePlayer.playUnknownCardChime()
                }
            }

            // Auto dismiss corner notification after 4.5 seconds
            dismissPopupJob?.cancel()
            dismissPopupJob = launch {
                delay(4500)
                if (_attendancePopupResult.value == result) {
                    _attendancePopupResult.value = null
                }
            }
        }
    }

    fun dismissAttendancePopup() {
        dismissPopupJob?.cancel()
        _attendancePopupResult.value = null
    }

    override fun onCleared() {
        super.onCleared()
        telegramJob?.cancel()
        httpServer.stop()
        chimePlayer.release()
    }
}

