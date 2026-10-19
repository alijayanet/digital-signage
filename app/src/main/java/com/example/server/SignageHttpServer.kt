package com.example.server

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.data.db.DefaultDataSeeder
import com.example.data.db.SignageRepository
import com.example.data.entity.*
import com.example.notification.TelegramSignageBotManager
import com.example.util.CsvUtils
import com.example.util.NetworkUtils
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID

class SignageHttpServer(
    private val context: Context,
    private val repository: SignageRepository,
    private val port: Int = 8080
) {
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val mediaBaseDir = File(context.filesDir, "media").apply { if (!exists()) mkdirs() }
    private val imagesDir = File(mediaBaseDir, "images").apply { if (!exists()) mkdirs() }
    private val videosDir = File(mediaBaseDir, "videos").apply { if (!exists()) mkdirs() }

    fun start() {
        if (serverJob?.isActive == true) return

        serverJob = serverScope.launch {
            try {
                val s = ServerSocket()
                s.reuseAddress = true
                s.bind(java.net.InetSocketAddress(port))
                serverSocket = s
                Log.i("SignageHttpServer", "Signage Web Server running on port $port (reuseAddress=true)")

                while (isActive) {
                    val clientSocket = serverSocket?.accept() ?: break
                    try {
                        clientSocket.soTimeout = 60_000
                        clientSocket.tcpNoDelay = true
                    } catch (_: Exception) {}
                    launch {
                        handleClient(clientSocket)
                    }
                }
            } catch (e: Exception) {
                Log.e("SignageHttpServer", "Server error on port $port: ${e.message}")
            }
        }
    }

    fun stop() {
        try {
            serverSocket?.close()
            serverJob?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun handleClient(socket: Socket) = withContext(Dispatchers.IO) {
        try {
            val input = socket.getInputStream()
            val out = socket.getOutputStream()

            // 1. Read HTTP request line and headers safely (max 64KB for headers)
            val headerBaos = ByteArrayOutputStream()
            var prev1 = -1
            var prev2 = -1
            var prev3 = -1
            var b: Int
            while (input.read().also { b = it } != -1) {
                headerBaos.write(b)
                if (prev3 == '\r'.code && prev2 == '\n'.code && prev1 == '\r'.code && b == '\n'.code) {
                    break
                }
                prev3 = prev2
                prev2 = prev1
                prev1 = b
                if (headerBaos.size() > 65536) break
            }

            val headerBytes = headerBaos.toByteArray()
            if (headerBytes.isEmpty()) return@withContext

            val headerText = String(headerBytes, Charsets.UTF_8)
            val lines = headerText.split("\r\n")
            if (lines.isEmpty()) return@withContext

            val requestLine = lines[0]
            val parts = requestLine.split(" ")
            if (parts.size < 2) return@withContext

            val method = parts[0].uppercase()
            val uri = parts[1]

            // Handle HTTP OPTIONS preflight (CORS) immediately
            if (method == "OPTIONS") {
                val corsHeaders = "HTTP/1.1 200 OK\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +
                        "Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT, DELETE\r\n" +
                        "Access-Control-Allow-Headers: *\r\n" +
                        "Access-Control-Max-Age: 86400\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n\r\n"
                out.write(corsHeaders.toByteArray(Charsets.UTF_8))
                out.flush()
                return@withContext
            }

            var contentLength = 0
            for (i in 1 until lines.size) {
                val line = lines[i]
                if (line.isEmpty()) continue
                val colonIdx = line.indexOf(':')
                if (colonIdx > 0) {
                    val hName = line.substring(0, colonIdx).trim().lowercase()
                    val hVal = line.substring(colonIdx + 1).trim()
                    if (hName == "content-length") {
                        contentLength = hVal.toIntOrNull() ?: 0
                    }
                }
            }

            // Read Body if present safely with ByteArray
            val body = if (contentLength > 0) {
                val maxBodySize = minOf(contentLength, 30 * 1024 * 1024) // 30MB max for body
                val buffer = ByteArray(maxBodySize)
                var bytesRead = 0
                while (bytesRead < maxBodySize) {
                    val read = input.read(buffer, bytesRead, maxBodySize - bytesRead)
                    if (read == -1) break
                    bytesRead += read
                }
                var excess = contentLength - bytesRead
                while (excess > 0) {
                    val skipped = input.skip(excess.toLong())
                    if (skipped <= 0) break
                    excess -= skipped.toInt()
                }
                String(buffer, 0, bytesRead, Charsets.UTF_8)
            } else ""

            routeRequest(method, uri, body, out)
        } catch (e: Exception) {
            Log.e("SignageHttpServer", "Client handler error: ${e.message}")
        } finally {
            try { socket.close() } catch (_: Exception) {}
        }
    }

    private suspend fun routeRequest(method: String, uri: String, body: String, out: OutputStream) {
        val path = uri.substringBefore("?")

        when {
            // Web Dashboard UI
            method == "GET" && (path == "/" || path == "/index.html") -> {
                val ip = NetworkUtils.getIpAddress(context)
                val html = WebDashboardHtml.getHtml(ip, port)
                sendResponse(out, 200, "OK", "text/html; charset=UTF-8", html.toByteArray(Charsets.UTF_8))
            }

            // Serve local uploaded images
            method == "GET" && path.startsWith("/images/") -> {
                val filename = path.removePrefix("/images/")
                val file = File(imagesDir, filename)
                if (file.exists() && file.isFile) {
                    val bytes = file.readBytes()
                    val mime = if (filename.endsWith(".png")) "image/png" else "image/jpeg"
                    sendResponse(out, 200, "OK", mime, bytes)
                } else {
                    sendResponse(out, 404, "Not Found", "text/plain", "Image not found".toByteArray())
                }
            }

            // Serve local uploaded videos
            method == "GET" && path.startsWith("/videos/") -> {
                val filename = path.removePrefix("/videos/")
                val file = File(videosDir, filename)
                if (file.exists() && file.isFile) {
                    val bytes = file.readBytes()
                    val mime = if (filename.endsWith(".webm")) "video/webm" else "video/mp4"
                    sendResponse(out, 200, "OK", mime, bytes)
                } else {
                    sendResponse(out, 404, "Not Found", "text/plain", "Video not found".toByteArray())
                }
            }

            // API: Get All Data / Status
            method == "GET" && (path == "/api/all" || path == "/api/status") -> {
                val config = repository.getConfig()
                val slides = repository.getAllSlides()
                val tickers = repository.getAllTickers()
                val agendas = repository.getAllAgendas()
                val notices = repository.getAllNotices()
                val employees = repository.getAllEmployees()
                val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                val todayAttendances = repository.getAttendanceRecordsByDate(todayStr)

                val json = JSONObject().apply {
                    put("status", "ok")
                    put("config", configToJson(config))
                    put("slides", JSONArray().apply { slides.forEach { put(slideToJson(it)) } })
                    put("tickers", JSONArray().apply { tickers.forEach { put(tickerToJson(it)) } })
                    put("agendas", JSONArray().apply { agendas.forEach { put(agendaToJson(it)) } })
                    put("notices", JSONArray().apply { notices.forEach { put(noticeToJson(it)) } })
                    put("employees", JSONArray().apply { employees.forEach { put(employeeToJson(it)) } })
                    put("todayAttendances", JSONArray().apply { todayAttendances.forEach { put(attendanceToJson(it)) } })
                }
                sendJsonResponse(out, json.toString())
            }

            // API: Get List of Indonesian Cities & Coordinates
            method == "GET" && path == "/api/cities" -> {
                val array = JSONArray().apply {
                    com.example.util.IndonesiaCityData.CITIES.forEach { city ->
                        put(JSONObject().apply {
                            put("name", city.name)
                            put("province", city.province)
                            put("latitude", city.latitude)
                            put("longitude", city.longitude)
                            put("timezone", city.timezone)
                            put("tzName", city.getTimezoneName())
                            put("displayName", city.displayName)
                        })
                    }
                }
                sendJsonResponse(out, array.toString())
            }

            // API: Update General Settings (Name, Slogan, Banner, Theme, Speed, Telegram, Attendance)
            method == "POST" && (path == "/api/settings" || path == "/api/save-config") -> {
                Log.i("SignageHttpServer", "POST $path received body: $body")
                val params = parseParams(body)
                val current = repository.getConfig()
                val newOrgName = params["orgName"]?.trim()?.ifBlank { current.orgName } ?: current.orgName

                val updated = current.copy(
                    id = 1,
                    orgType = params["orgType"]?.trim() ?: current.orgType,
                    orgName = newOrgName,
                    orgSubtitle = params["orgSubtitle"]?.trim() ?: current.orgSubtitle,
                    headerBannerText = params["headerBannerText"]?.trim() ?: current.headerBannerText,
                    themeId = params["themeId"]?.trim() ?: current.themeId,
                    slideIntervalSeconds = params["slideIntervalSeconds"]?.toIntOrNull() ?: current.slideIntervalSeconds,
                    runningTextSpeed = params["runningTextSpeed"]?.toIntOrNull() ?: current.runningTextSpeed,
                    telegramBotEnabled = if (params.containsKey("telegramBotEnabled")) {
                        params["telegramBotEnabled"]?.toBooleanStrictOrNull() ?: (params["telegramBotEnabled"] == "1" || params["telegramBotEnabled"] == "on")
                    } else current.telegramBotEnabled,
                    telegramBotToken = params["telegramBotToken"]?.trim() ?: current.telegramBotToken,
                    telegramBotAuthorizedUserIds = params["telegramBotAuthorizedUserIds"]?.trim() ?: current.telegramBotAuthorizedUserIds,
                    enableAttendance = if (params.containsKey("enableAttendance")) {
                        params["enableAttendance"]?.toBooleanStrictOrNull() ?: (params["enableAttendance"] == "1" || params["enableAttendance"] == "on")
                    } else current.enableAttendance,
                    checkInLateLimit = params["checkInLateLimit"]?.trim()?.ifBlank { current.checkInLateLimit } ?: current.checkInLateLimit,
                    checkOutStart = params["checkOutStart"]?.trim()?.ifBlank { current.checkOutStart } ?: current.checkOutStart,
                    cityName = params["cityName"]?.trim()?.ifBlank { current.cityName } ?: current.cityName,
                    latitude = params["latitude"]?.toDoubleOrNull() ?: current.latitude,
                    longitude = params["longitude"]?.toDoubleOrNull() ?: current.longitude,
                    timezone = params["timezone"]?.toDoubleOrNull() ?: current.timezone
                )
                repository.updateConfig(updated)
                Log.i("SignageHttpServer", "Successfully updated config: orgName='${updated.orgName}', city='${updated.cityName}', tz='${updated.timezone}'")
                sendJsonResponse(out, """{"status":"ok","success":true,"message":"Pengaturan dan lokasi berhasil disimpan ke TV!"}""")
            }


            // API: Test Telegram Bot Connection
            method == "POST" && path == "/api/telegram/test" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val config = repository.getConfig()
                val token = json.optString("token", config.telegramBotToken).trim()
                val targetChatId = json.optLong("chatId", 0L).let {
                    if (it != 0L) it else config.getTelegramAuthorizedIdList().firstOrNull() ?: 0L
                }

                if (token.isBlank()) {
                    sendJsonResponse(out, """{"success":false,"message":"Token bot Telegram masih kosong!"}""")
                } else if (targetChatId == 0L) {
                    sendJsonResponse(out, """{"success":false,"message":"User ID / Chat ID tujuan masih kosong!"}""")
                } else {
                    val ip = NetworkUtils.getIpAddress(context)
                    val testMsg = "✅ *Uji Koneksi Berhasil!*\nDigital Signage TV (*${config.orgName}*) telah terhubung dengan bot Telegram Anda.\nIP TV: `http://$ip:8080`"
                    val ok = TelegramSignageBotManager.sendMessage(token, targetChatId, testMsg)
                    if (ok) {
                        sendJsonResponse(out, """{"success":true,"message":"Pesan tes berhasil dikirimkan ke Telegram!"}""")
                    } else {
                        sendJsonResponse(out, """{"success":false,"message":"Gagal mengirim pesan ke Telegram. Periksa token dan User ID Anda."}""")
                    }
                }
            }

            // API: Apply Presets (School or Gov)
            method == "POST" && path == "/api/preset" -> {
                val json = JSONObject(body)
                val type = json.optString("type", "school")
                val preset = if (type == "gov") DefaultDataSeeder.getGovPreset() else DefaultDataSeeder.getSchoolPreset()
                repository.applyPreset(preset)
                sendJsonResponse(out, """{"success":true}""")
            }

            // API: Flash Alert Message
            method == "POST" && path == "/api/flash-alert" -> {
                val json = JSONObject(body)
                val message = json.optString("message", "")
                repository.setFlashAlert(message)
                sendJsonResponse(out, """{"success":true}""")
            }

            // API: Slides (Add, Update, Delete)
            method == "POST" && path == "/api/slide/add" -> {
                val json = JSONObject(body)
                var mediaUrl = json.optString("imageUrl", "")
                var mediaType = json.optString("mediaType", "")
                
                // If base64 video, save to local video disk
                if (mediaUrl.startsWith("data:video")) {
                    val base64Data = mediaUrl.substringAfter("base64,")
                    val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                    val ext = if (mediaUrl.contains("video/webm")) "webm" else "mp4"
                    val filename = "video_${UUID.randomUUID()}.$ext"
                    val file = File(videosDir, filename)
                    FileOutputStream(file).use { it.write(bytes) }
                    mediaUrl = "/videos/$filename"
                    mediaType = "VIDEO"
                }
                // If base64 image, save to local image disk
                else if (mediaUrl.startsWith("data:image")) {
                    val base64Data = mediaUrl.substringAfter("base64,")
                    val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                    val filename = "slide_${UUID.randomUUID()}.jpg"
                    val file = File(imagesDir, filename)
                    FileOutputStream(file).use { it.write(bytes) }
                    mediaUrl = "/images/$filename"
                    mediaType = "IMAGE"
                }

                // Auto-detect mediaType if not explicitly passed
                if (mediaType.isBlank()) {
                    val lower = mediaUrl.lowercase()
                    mediaType = if (lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mkv") || lower.contains("/videos/")) {
                        "VIDEO"
                    } else {
                        "IMAGE"
                    }
                }

                val slide = SlideItem(
                    title = json.optString("title", "Info Baru"),
                    caption = json.optString("caption", ""),
                    category = json.optString("category", if (mediaType == "VIDEO") "VIDEO PROFIL" else "INFORMASI"),
                    imageUrl = mediaUrl,
                    mediaType = mediaType,
                    durationSeconds = json.optInt("durationSeconds", if (mediaType == "VIDEO") 15 else 8),
                    displayOrder = 99,
                    isActive = true
                )
                repository.addSlide(slide)
                sendJsonResponse(out, """{"success":true}""")
            }

            method == "POST" && path == "/api/slide/update" -> {
                val json = JSONObject(body)
                val id = json.optLong("id", -1)
                if (id > 0) {
                    var mediaUrl = json.optString("imageUrl", "")
                    var mediaType = json.optString("mediaType", "")
                    if (mediaUrl.startsWith("data:video")) {
                        val base64Data = mediaUrl.substringAfter("base64,")
                        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                        val ext = if (mediaUrl.contains("video/webm")) "webm" else "mp4"
                        val filename = "video_${UUID.randomUUID()}.$ext"
                        val file = File(videosDir, filename)
                        FileOutputStream(file).use { it.write(bytes) }
                        mediaUrl = "/videos/$filename"
                        mediaType = "VIDEO"
                    } else if (mediaUrl.startsWith("data:image")) {
                        val base64Data = mediaUrl.substringAfter("base64,")
                        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                        val filename = "slide_${UUID.randomUUID()}.jpg"
                        val file = File(imagesDir, filename)
                        FileOutputStream(file).use { it.write(bytes) }
                        mediaUrl = "/images/$filename"
                        mediaType = "IMAGE"
                    }
                    if (mediaType.isBlank()) {
                        val lower = mediaUrl.lowercase()
                        mediaType = if (lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mkv") || lower.contains("/videos/")) {
                            "VIDEO"
                        } else {
                            "IMAGE"
                        }
                    }
                    val slide = SlideItem(
                        id = id,
                        title = json.optString("title", "Info"),
                        caption = json.optString("caption", ""),
                        category = json.optString("category", if (mediaType == "VIDEO") "VIDEO PROFIL" else "INFORMASI"),
                        imageUrl = mediaUrl,
                        mediaType = mediaType,
                        durationSeconds = json.optInt("durationSeconds", if (mediaType == "VIDEO") 15 else 8),
                        displayOrder = json.optInt("displayOrder", 99),
                        isActive = json.optBoolean("isActive", true)
                    )
                    repository.updateSlide(slide)
                }
                sendJsonResponse(out, """{"success":true}""")
            }

            method == "POST" && path == "/api/slide/delete" -> {
                val json = JSONObject(body)
                val id = json.optLong("id", -1)
                if (id > 0) repository.deleteSlide(id)
                sendJsonResponse(out, """{"success":true}""")
            }

            // API: Tickers (Add, Update, Delete)
            method == "POST" && path == "/api/ticker/add" -> {
                val json = JSONObject(body)
                val ticker = RunningTickerItem(
                    badge = json.optString("badge", "PENGUMUMAN"),
                    text = json.optString("text", ""),
                    displayOrder = 99,
                    isActive = true
                )
                repository.addTicker(ticker)
                sendJsonResponse(out, """{"success":true}""")
            }

            method == "POST" && path == "/api/ticker/update" -> {
                val json = JSONObject(body)
                val id = json.optLong("id", -1)
                if (id > 0) {
                    val ticker = RunningTickerItem(
                        id = id,
                        badge = json.optString("badge", "PENGUMUMAN"),
                        text = json.optString("text", ""),
                        displayOrder = json.optInt("displayOrder", 99),
                        isActive = json.optBoolean("isActive", true)
                    )
                    repository.updateTicker(ticker)
                }
                sendJsonResponse(out, """{"success":true}""")
            }

            method == "POST" && path == "/api/ticker/delete" -> {
                val json = JSONObject(body)
                val id = json.optLong("id", -1)
                if (id > 0) repository.deleteTicker(id)
                sendJsonResponse(out, """{"success":true}""")
            }

            // API: Agendas (Add, Update, Delete)
            method == "POST" && path == "/api/agenda/add" -> {
                val json = JSONObject(body)
                val agenda = AgendaItem(
                    time = json.optString("time", "Hari Ini"),
                    title = json.optString("title", "Kegiatan"),
                    location = json.optString("location", "-"),
                    pic = json.optString("pic", ""),
                    status = json.optString("status", "UPCOMING"),
                    displayOrder = 99,
                    isActive = true
                )
                repository.addAgenda(agenda)
                sendJsonResponse(out, """{"success":true}""")
            }

            method == "POST" && path == "/api/agenda/update" -> {
                val json = JSONObject(body)
                val id = json.optLong("id", -1)
                if (id > 0) {
                    val agenda = AgendaItem(
                        id = id,
                        time = json.optString("time", "Hari Ini"),
                        title = json.optString("title", "Kegiatan"),
                        location = json.optString("location", "-"),
                        pic = json.optString("pic", ""),
                        status = json.optString("status", "UPCOMING"),
                        displayOrder = json.optInt("displayOrder", 99),
                        isActive = json.optBoolean("isActive", true)
                    )
                    repository.updateAgenda(agenda)
                }
                sendJsonResponse(out, """{"success":true}""")
            }

            method == "POST" && path == "/api/agenda/delete" -> {
                val json = JSONObject(body)
                val id = json.optLong("id", -1)
                if (id > 0) repository.deleteAgenda(id)
                sendJsonResponse(out, """{"success":true}""")
            }

            // API: Notices (Add, Update, Delete)
            method == "POST" && path == "/api/notice/add" -> {
                val json = JSONObject(body)
                val notice = NoticeItem(
                    title = json.optString("title", "Pengumuman"),
                    content = json.optString("content", ""),
                    priority = json.optString("priority", "INFO"),
                    dateText = json.optString("dateText", "Hari Ini"),
                    displayOrder = 99,
                    isActive = true
                )
                repository.addNotice(notice)
                sendJsonResponse(out, """{"success":true}""")
            }

            method == "POST" && path == "/api/notice/update" -> {
                val json = JSONObject(body)
                val id = json.optLong("id", -1)
                if (id > 0) {
                    val notice = NoticeItem(
                        id = id,
                        title = json.optString("title", "Pengumuman"),
                        content = json.optString("content", ""),
                        priority = json.optString("priority", "INFO"),
                        dateText = json.optString("dateText", "Hari Ini"),
                        displayOrder = json.optInt("displayOrder", 99),
                        isActive = json.optBoolean("isActive", true)
                    )
                    repository.updateNotice(notice)
                }
                sendJsonResponse(out, """{"success":true}""")
            }

            method == "POST" && path == "/api/notice/delete" -> {
                val json = JSONObject(body)
                val id = json.optLong("id", -1)
                if (id > 0) repository.deleteNotice(id)
                sendJsonResponse(out, """{"success":true}""")
            }

            // API: Employee Management (List, Save, Delete)
            method == "GET" && path == "/api/employee/list" -> {
                val employees = repository.getAllEmployees()
                val json = JSONObject().apply {
                    put("status", "ok")
                    put("employees", JSONArray().apply { employees.forEach { put(employeeToJson(it)) } })
                }
                sendJsonResponse(out, json.toString())
            }

            method == "POST" && (path == "/api/employee/save" || path == "/api/employee/add" || path == "/api/employee/update") -> {
                val params = parseParams(body)
                val id = params["id"]?.toLongOrNull() ?: 0L
                val name = params["name"]?.trim().orEmpty()
                val rfidUid = params["rfidUid"]?.trim().orEmpty()
                val department = params["department"]?.trim()?.ifBlank { "Umum" } ?: "Umum"
                val role = params["role"]?.trim()?.ifBlank { "Karyawan" } ?: "Karyawan"
                var photoUrl = params["photoUrl"]?.trim().orEmpty()
                val phone = params["phone"]?.trim().orEmpty()
                val isActive = if (params.containsKey("isActive")) {
                    params["isActive"] == "true" || params["isActive"] == "1" || params["isActive"] == "on"
                } else true

                // Decode and store Base64 uploaded employee photo to local disk
                if (photoUrl.startsWith("data:image")) {
                    try {
                        val base64Data = photoUrl.substringAfter("base64,")
                        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                        val filename = "emp_${UUID.randomUUID()}.jpg"
                        val file = File(imagesDir, filename)
                        FileOutputStream(file).use { it.write(bytes) }
                        photoUrl = "/images/$filename"
                    } catch (e: Exception) {
                        Log.e("SignageHttpServer", "Failed to save employee photo: ${e.message}")
                    }
                }

                if (name.isBlank() || rfidUid.isBlank()) {
                    sendJsonResponse(out, """{"status":"error","success":false,"message":"Nama dan UID Kartu RFID wajib diisi!"}""")
                } else {
                    val employee = EmployeeItem(
                        id = id,
                        name = name,
                        rfidUid = rfidUid,
                        department = department,
                        role = role,
                        photoUrl = photoUrl,
                        phone = phone,
                        isActive = isActive
                    )
                    if (id > 0L) {
                        repository.updateEmployee(employee)
                    } else {
                        repository.addEmployee(employee)
                    }
                    sendJsonResponse(out, """{"status":"ok","success":true,"message":"Data pegawai berhasil disimpan!"}""")
                }
            }

            method == "POST" && path == "/api/employee/delete" -> {
                val params = parseParams(body)
                val id = params["id"]?.toLongOrNull() ?: 0L
                if (id > 0L) {
                    repository.deleteEmployee(id)
                    sendJsonResponse(out, """{"status":"ok","success":true,"message":"Pegawai berhasil dihapus!"}""")
                } else {
                    sendJsonResponse(out, """{"status":"error","success":false,"message":"ID pegawai tidak valid!"}""")
                }
            }

            // API: Save / Update Employee Roles List
            method == "POST" && path == "/api/employee/roles/save" -> {
                val params = parseParams(body)
                val roles = params["roles"]?.trim().orEmpty()
                if (roles.isNotBlank()) {
                    repository.updateEmployeeRoles(roles)
                    sendJsonResponse(out, """{"status":"ok","success":true,"message":"Kategori role pegawai berhasil disimpan!"}""")
                } else {
                    sendJsonResponse(out, """{"status":"error","success":false,"message":"Daftar role tidak boleh kosong!"}""")
                }
            }

            // API: Employee CSV Export
            method == "GET" && path == "/api/employee/export-csv" -> {
                val employees = repository.getAllEmployees()
                val sb = StringBuilder()
                sb.append("\uFEFF") // UTF-8 BOM for Indonesian Excel
                sb.append("No,Nama,UID Kartu RFID,Jabatan / Divisi,Role / Kategori,No HP,Status Aktif\n")
                employees.forEachIndexed { idx, emp ->
                    sb.append("${idx + 1},${CsvUtils.escapeCsv(emp.name)},${CsvUtils.escapeCsv(emp.rfidUid)},${CsvUtils.escapeCsv(emp.department)},${CsvUtils.escapeCsv(emp.role)},${CsvUtils.escapeCsv(emp.phone)},${if (emp.isActive) "Aktif" else "Nonaktif"}\n")
                }
                val csvBytes = sb.toString().toByteArray(Charsets.UTF_8)
                val headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/csv; charset=UTF-8\r\n" +
                        "Content-Disposition: attachment; filename=\"data_pegawai.csv\"\r\n" +
                        "Content-Length: ${csvBytes.size}\r\n" +
                        "Cache-Control: no-cache, no-store, must-revalidate\r\n" +
                        "Connection: close\r\n\r\n"
                out.write(headers.toByteArray(Charsets.UTF_8))
                out.write(csvBytes)
                out.flush()
            }

            // API: Employee CSV Template
            method == "GET" && path == "/api/employee/template-csv" -> {
                val sb = StringBuilder()
                sb.append("\uFEFF")
                sb.append("No,Nama,UID Kartu RFID,Jabatan / Divisi,Role / Kategori,No HP,Status Aktif\n")
                sb.append("1,\"Drs. H. Bambang Sutrisno, M.Pd\",\"12345678\",\"Dewan Guru\",\"Guru\",\"081234567890\",\"Aktif\"\n")
                sb.append("2,\"Siti Aminah, S.Kom\",\"87654321\",\"Tata Usaha\",\"Staf TU\",\"081298765432\",\"Aktif\"\n")
                sb.append("3,\"Ahmad Fauzi\",\"11223344\",\"Petugas Keamanan\",\"Keamanan\",\"081345678901\",\"Aktif\"\n")
                val csvBytes = sb.toString().toByteArray(Charsets.UTF_8)
                val headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/csv; charset=UTF-8\r\n" +
                        "Content-Disposition: attachment; filename=\"template_data_pegawai.csv\"\r\n" +
                        "Content-Length: ${csvBytes.size}\r\n" +
                        "Cache-Control: no-cache, no-store, must-revalidate\r\n" +
                        "Connection: close\r\n\r\n"
                out.write(headers.toByteArray(Charsets.UTF_8))
                out.write(csvBytes)
                out.flush()
            }

            // API: Employee CSV Import
            method == "POST" && path == "/api/employee/import-csv" -> {
                try {
                    val rawCsv = if (body.trim().startsWith("{")) {
                        val json = JSONObject(body)
                        json.optString("csvData", "")
                    } else body

                    val rows = CsvUtils.parseCsv(rawCsv)
                    if (rows.isEmpty()) {
                        sendJsonResponse(out, """{"status":"error","success":false,"message":"File CSV kosong atau tidak valid!"}""")
                    } else {
                        val header = rows.first().map { it.lowercase().trim() }
                        var nameIdx = header.indexOfFirst { it.contains("nama") }
                        var uidIdx = header.indexOfFirst { it.contains("uid") || it.contains("kartu") || it.contains("rfid") }
                        var deptIdx = header.indexOfFirst { it.contains("jabatan") || it.contains("divisi") || it.contains("bagian") }
                        var roleIdx = header.indexOfFirst { it.contains("role") || it.contains("kategori") }
                        var phoneIdx = header.indexOfFirst { it.contains("hp") || it.contains("telepon") || it.contains("wa") }
                        var activeIdx = header.indexOfFirst { it.contains("aktif") || it.contains("status") }

                        val dataRows = if (nameIdx != -1 || uidIdx != -1) rows.drop(1) else rows
                        if (nameIdx == -1) nameIdx = 1
                        if (uidIdx == -1) uidIdx = 2
                        if (deptIdx == -1) deptIdx = 3
                        if (roleIdx == -1) roleIdx = 4
                        if (phoneIdx == -1) phoneIdx = 5
                        if (activeIdx == -1) activeIdx = 6

                        val employeesToImport = mutableListOf<EmployeeItem>()
                        for (row in dataRows) {
                            if (row.size <= maxOf(nameIdx, uidIdx)) continue
                            val name = row.getOrNull(nameIdx)?.trim().orEmpty()
                            val rfid = row.getOrNull(uidIdx)?.trim().orEmpty()
                            if (name.isBlank() || rfid.isBlank()) continue

                            val dept = row.getOrNull(deptIdx)?.trim()?.ifBlank { "Umum" } ?: "Umum"
                            val role = row.getOrNull(roleIdx)?.trim()?.ifBlank { "Karyawan" } ?: "Karyawan"
                            val phone = row.getOrNull(phoneIdx)?.trim().orEmpty()
                            val activeVal = row.getOrNull(activeIdx)?.trim().orEmpty()
                            val isActive = !activeVal.equals("nonaktif", ignoreCase = true) && !activeVal.equals("false", ignoreCase = true)

                            employeesToImport.add(
                                EmployeeItem(
                                    name = name,
                                    rfidUid = rfid,
                                    department = dept,
                                    role = role,
                                    phone = phone,
                                    isActive = isActive
                                )
                            )
                        }

                        val count = repository.importEmployees(employeesToImport)
                        sendJsonResponse(out, """{"status":"ok","success":true,"count":$count,"message":"Berhasil mengimpor $count data pegawai!"}""")
                    }
                } catch (e: Exception) {
                    sendJsonResponse(out, """{"status":"error","success":false,"message":"Gagal memproses file CSV: ${e.message?.replace("\"", "'")}"}""")
                }
            }

            // API: Attendance Log & Recap (Daily, Monthly, or All)
            method == "GET" && path == "/api/attendance/list" -> {
                val queryParams = parseQuery(uri)
                val mode = queryParams["mode"] ?: ""
                val monthParam = queryParams["month"]?.trim()
                val dateParam = queryParams["date"]?.trim()
                val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

                val records: List<AttendanceRecordItem>
                val filterDesc: String

                when {
                    !monthParam.isNullOrBlank() || mode == "month" -> {
                        val month = monthParam?.ifBlank { todayStr.substring(0, 7) } ?: todayStr.substring(0, 7)
                        records = repository.getAttendanceRecordsByMonth(month)
                        filterDesc = "Bulan $month"
                    }
                    dateParam == "all" || mode == "all" -> {
                        records = repository.getAllAttendanceRecords()
                        filterDesc = "Semua Riwayat"
                    }
                    else -> {
                        val date = dateParam?.ifBlank { todayStr } ?: todayStr
                        records = repository.getAttendanceRecordsByDate(date)
                        filterDesc = "Tanggal $date"
                    }
                }

                val json = JSONObject().apply {
                    put("status", "ok")
                    put("filter", filterDesc)
                    put("records", JSONArray().apply { records.forEach { put(attendanceToJson(it)) } })
                }
                sendJsonResponse(out, json.toString())
            }

            // API: Export Attendance CSV (Daily, Monthly, or All)
            method == "GET" && path == "/api/attendance/export-csv" -> {
                val queryParams = parseQuery(uri)
                val mode = queryParams["mode"] ?: ""
                val monthParam = queryParams["month"]?.trim()
                val dateParam = queryParams["date"]?.trim()
                val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

                val records: List<AttendanceRecordItem>
                val filename: String

                when {
                    !monthParam.isNullOrBlank() || mode == "month" -> {
                        val month = monthParam?.ifBlank { todayStr.substring(0, 7) } ?: todayStr.substring(0, 7)
                        records = repository.getAttendanceRecordsByMonth(month)
                        filename = "rekap_presensi_bulan_$month.csv"
                    }
                    dateParam == "all" || mode == "all" -> {
                        records = repository.getAllAttendanceRecords()
                        filename = "rekap_presensi_semua.csv"
                    }
                    else -> {
                        val date = dateParam?.ifBlank { todayStr } ?: todayStr
                        records = repository.getAttendanceRecordsByDate(date)
                        filename = "rekap_presensi_$date.csv"
                    }
                }

                val sb = StringBuilder()
                sb.append("\uFEFF") // UTF-8 BOM
                sb.append("No,Nama Pegawai,Jabatan / Divisi,Role,UID Kartu,Tanggal,Jam Masuk,Status Masuk,Jam Pulang,Status Pulang\n")
                records.forEachIndexed { idx, r ->
                    sb.append("${idx + 1},${CsvUtils.escapeCsv(r.employeeName)},${CsvUtils.escapeCsv(r.department)},${CsvUtils.escapeCsv(r.role)},${CsvUtils.escapeCsv(r.rfidUid)},${CsvUtils.escapeCsv(r.date)},${CsvUtils.escapeCsv(r.timeIn)},${CsvUtils.escapeCsv(r.statusIn)},${CsvUtils.escapeCsv(r.timeOut)},${CsvUtils.escapeCsv(r.statusOut)}\n")
                }

                val csvBytes = sb.toString().toByteArray(Charsets.UTF_8)
                val headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/csv; charset=UTF-8\r\n" +
                        "Content-Disposition: attachment; filename=\"$filename\"\r\n" +
                        "Content-Length: ${csvBytes.size}\r\n" +
                        "Cache-Control: no-cache, no-store, must-revalidate\r\n" +
                        "Connection: close\r\n\r\n"
                out.write(headers.toByteArray(Charsets.UTF_8))
                out.write(csvBytes)
                out.flush()
            }

            // API: Attendance CSV Template
            method == "GET" && path == "/api/attendance/template-csv" -> {
                val sb = StringBuilder()
                sb.append("\uFEFF")
                sb.append("No,Nama Pegawai,Jabatan / Divisi,Role,UID Kartu,Tanggal,Jam Masuk,Status Masuk,Jam Pulang,Status Pulang\n")
                sb.append("1,\"Drs. H. Bambang Sutrisno, M.Pd\",\"Dewan Guru\",\"Guru\",\"12345678\",\"2026-10-13\",\"06:45\",\"TEPAT WAKTU\",\"14:15\",\"PULANG\"\n")
                sb.append("2,\"Siti Aminah, S.Kom\",\"Tata Usaha\",\"Staf TU\",\"87654321\",\"2026-10-13\",\"07:35\",\"TERLAMBAT (5 mnt)\",\"14:05\",\"PULANG\"\n")
                val csvBytes = sb.toString().toByteArray(Charsets.UTF_8)
                val headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/csv; charset=UTF-8\r\n" +
                        "Content-Disposition: attachment; filename=\"template_data_presensi.csv\"\r\n" +
                        "Content-Length: ${csvBytes.size}\r\n" +
                        "Cache-Control: no-cache, no-store, must-revalidate\r\n" +
                        "Connection: close\r\n\r\n"
                out.write(headers.toByteArray(Charsets.UTF_8))
                out.write(csvBytes)
                out.flush()
            }

            // API: Attendance CSV Import
            method == "POST" && path == "/api/attendance/import-csv" -> {
                try {
                    val rawCsv = if (body.trim().startsWith("{")) {
                        val json = JSONObject(body)
                        json.optString("csvData", "")
                    } else body

                    val rows = CsvUtils.parseCsv(rawCsv)
                    if (rows.isEmpty()) {
                        sendJsonResponse(out, """{"status":"error","success":false,"message":"File CSV kosong atau tidak valid!"}""")
                    } else {
                        val header = rows.first().map { it.lowercase().trim() }
                        var nameIdx = header.indexOfFirst { it.contains("nama") }
                        var deptIdx = header.indexOfFirst { it.contains("jabatan") || it.contains("divisi") }
                        var roleIdx = header.indexOfFirst { it.contains("role") }
                        var uidIdx = header.indexOfFirst { it.contains("uid") || it.contains("kartu") || it.contains("rfid") }
                        var dateIdx = header.indexOfFirst { it.contains("tanggal") || it.contains("date") }
                        var inIdx = header.indexOfFirst { it.contains("masuk") && !it.contains("status") }
                        var statusInIdx = header.indexOfFirst { it.contains("status") && it.contains("masuk") }
                        var outIdx = header.indexOfFirst { it.contains("pulang") && !it.contains("status") }
                        var statusOutIdx = header.indexOfFirst { it.contains("status") && it.contains("pulang") }

                        val dataRows = if (nameIdx != -1 || uidIdx != -1 || dateIdx != -1) rows.drop(1) else rows
                        if (nameIdx == -1) nameIdx = 1
                        if (deptIdx == -1) deptIdx = 2
                        if (roleIdx == -1) roleIdx = 3
                        if (uidIdx == -1) uidIdx = 4
                        if (dateIdx == -1) dateIdx = 5
                        if (inIdx == -1) inIdx = 6
                        if (statusInIdx == -1) statusInIdx = 7
                        if (outIdx == -1) outIdx = 8
                        if (statusOutIdx == -1) statusOutIdx = 9

                        val employees = repository.getAllEmployees()
                        val empByUid = employees.associateBy { it.rfidUid.trim().uppercase() }

                        val recordsToImport = mutableListOf<AttendanceRecordItem>()
                        for (row in dataRows) {
                            val rfid = row.getOrNull(uidIdx)?.trim().orEmpty()
                            val date = row.getOrNull(dateIdx)?.trim().orEmpty()
                            if (rfid.isBlank() || date.isBlank()) continue

                            val matchedEmp = empByUid[rfid.uppercase()]
                            val empName = row.getOrNull(nameIdx)?.trim()?.ifBlank { matchedEmp?.name ?: "Pegawai $rfid" } ?: (matchedEmp?.name ?: "Pegawai $rfid")
                            val dept = row.getOrNull(deptIdx)?.trim()?.ifBlank { matchedEmp?.department ?: "Umum" } ?: (matchedEmp?.department ?: "Umum")
                            val role = row.getOrNull(roleIdx)?.trim()?.ifBlank { matchedEmp?.role ?: "Karyawan" } ?: (matchedEmp?.role ?: "Karyawan")
                            val timeIn = row.getOrNull(inIdx)?.trim().orEmpty()
                            val statusIn = row.getOrNull(statusInIdx)?.trim()?.ifBlank { "HADIR" } ?: "HADIR"
                            val timeOut = row.getOrNull(outIdx)?.trim().orEmpty()
                            val statusOut = row.getOrNull(statusOutIdx)?.trim()?.ifBlank { if (timeOut.isNotBlank()) "PULANG" else "-" } ?: "-"

                            recordsToImport.add(
                                AttendanceRecordItem(
                                    employeeId = matchedEmp?.id ?: 0L,
                                    rfidUid = rfid,
                                    employeeName = empName,
                                    department = dept,
                                    role = role,
                                    photoUrl = matchedEmp?.photoUrl ?: "",
                                    date = date,
                                    timeIn = timeIn,
                                    timeOut = timeOut,
                                    statusIn = statusIn,
                                    statusOut = statusOut
                                )
                            )
                        }

                        val count = repository.importAttendanceRecords(recordsToImport)
                        sendJsonResponse(out, """{"status":"ok","success":true,"count":$count,"message":"Berhasil mengimpor $count catatan presensi!"}""")
                    }
                } catch (e: Exception) {
                    sendJsonResponse(out, """{"status":"error","success":false,"message":"Gagal memproses file CSV: ${e.message?.replace("\"", "'")}"}""")
                }
            }

            method == "POST" && path == "/api/attendance/config" -> {
                val params = parseParams(body)
                val current = repository.getConfig()
                val enableAttendance = if (params.containsKey("enableAttendance")) {
                    params["enableAttendance"] == "true" || params["enableAttendance"] == "1" || params["enableAttendance"] == "on"
                } else current.enableAttendance
                val checkInLateLimit = params["checkInLateLimit"]?.trim()?.ifBlank { current.checkInLateLimit } ?: current.checkInLateLimit
                val checkOutStart = params["checkOutStart"]?.trim()?.ifBlank { current.checkOutStart } ?: current.checkOutStart

                val updated = current.copy(
                    enableAttendance = enableAttendance,
                    checkInLateLimit = checkInLateLimit,
                    checkOutStart = checkOutStart
                )
                repository.updateConfig(updated)
                sendJsonResponse(out, """{"status":"ok","success":true,"message":"Jadwal presensi berhasil disimpan!"}""")
            }

            else -> {
                sendResponse(out, 404, "Not Found", "text/plain", "Endpoint not found".toByteArray())
            }
        }
    }


    private fun parseParams(body: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val trimmed = body.trim()
        if (trimmed.startsWith("{")) {
            try {
                val json = JSONObject(trimmed)
                val keys = json.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    map[k] = json.optString(k, "")
                }
                return map
            } catch (_: Exception) {}
        }
        val pairs = trimmed.split("&")
        for (p in pairs) {
            val idx = p.indexOf('=')
            if (idx > 0) {
                try {
                    val k = java.net.URLDecoder.decode(p.substring(0, idx), "UTF-8")
                    val v = java.net.URLDecoder.decode(p.substring(idx + 1), "UTF-8")
                    map[k] = v
                } catch (_: Exception) {}
            }
        }
        return map
    }

    private fun parseQuery(uri: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val query = uri.substringAfter("?", "")
        if (query.isNotBlank()) {
            val pairs = query.split("&")
            for (p in pairs) {
                val idx = p.indexOf('=')
                if (idx > 0) {
                    try {
                        val k = java.net.URLDecoder.decode(p.substring(0, idx), "UTF-8")
                        val v = java.net.URLDecoder.decode(p.substring(idx + 1), "UTF-8")
                        map[k] = v
                    } catch (_: Exception) {}
                }
            }
        }
        return map
    }

    private fun sendJsonResponse(out: OutputStream, json: String) {
        sendResponse(out, 200, "OK", "application/json; charset=UTF-8", json.toByteArray(Charsets.UTF_8))
    }

    private fun sendResponse(out: OutputStream, code: Int, message: String, contentType: String, body: ByteArray) {
        val header = "HTTP/1.1 $code $message\r\n" +
                "Content-Type: $contentType\r\n" +
                "Content-Length: ${body.size}\r\n" +
                "Cache-Control: no-cache, no-store, must-revalidate\r\n" +
                "Pragma: no-cache\r\n" +
                "Expires: 0\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT, DELETE\r\n" +
                "Access-Control-Allow-Headers: *\r\n" +
                "Connection: close\r\n\r\n"
        out.write(header.toByteArray(Charsets.UTF_8))
        out.write(body)
        out.flush()
    }

    private fun configToJson(c: SignageConfig): JSONObject {
        return JSONObject().apply {
            put("id", c.id)
            put("orgType", c.orgType)
            put("orgName", c.orgName)
            put("orgSubtitle", c.orgSubtitle)
            put("headerBannerText", c.headerBannerText)
            put("themeId", c.themeId)
            put("layoutMode", c.layoutMode)
            put("slideIntervalSeconds", c.slideIntervalSeconds)
            put("runningTextSpeed", c.runningTextSpeed)
            put("orgLogoUrl", c.orgLogoUrl)
            put("enableClock", c.enableClock)
            put("enableAgendas", c.enableAgendas)
            put("enableNotices", c.enableNotices)
            put("enableQuickInfo", c.enableQuickInfo)
            put("serverPort", c.serverPort)
            put("activeFlashAlert", c.activeFlashAlert)
            put("telegramBotEnabled", c.telegramBotEnabled)
            put("telegramBotToken", c.telegramBotToken)
            put("telegramBotAuthorizedUserIds", c.telegramBotAuthorizedUserIds)
            put("enableAttendance", c.enableAttendance)
            put("checkInLateLimit", c.checkInLateLimit)
            put("checkOutStart", c.checkOutStart)
            put("employeeRoles", c.employeeRoles)
            put("cityName", c.cityName)
            put("latitude", c.latitude)
            put("longitude", c.longitude)
            put("timezone", c.timezone)
            put("timezoneName", com.example.util.IndonesiaCityData.getTimezoneName(c.timezone))
        }
    }

    private fun slideToJson(s: SlideItem): JSONObject {
        return JSONObject().apply {
            put("id", s.id)
            put("title", s.title)
            put("caption", s.caption)
            put("category", s.category)
            put("imageUrl", s.imageUrl)
            put("mediaType", s.mediaType)
            put("displayOrder", s.displayOrder)
            put("isActive", s.isActive)
            put("durationSeconds", s.durationSeconds)
        }
    }

    private fun tickerToJson(t: RunningTickerItem): JSONObject {
        return JSONObject().apply {
            put("id", t.id)
            put("badge", t.badge)
            put("text", t.text)
            put("displayOrder", t.displayOrder)
            put("isActive", t.isActive)
        }
    }

    private fun agendaToJson(a: AgendaItem): JSONObject {
        return JSONObject().apply {
            put("id", a.id)
            put("time", a.time)
            put("title", a.title)
            put("location", a.location)
            put("pic", a.pic)
            put("status", a.status)
            put("displayOrder", a.displayOrder)
            put("isActive", a.isActive)
        }
    }

    private fun noticeToJson(n: NoticeItem): JSONObject {
        return JSONObject().apply {
            put("id", n.id)
            put("title", n.title)
            put("content", n.content)
            put("priority", n.priority)
            put("dateText", n.dateText)
            put("displayOrder", n.displayOrder)
            put("isActive", n.isActive)
        }
    }

    private fun employeeToJson(e: EmployeeItem): JSONObject {
        return JSONObject().apply {
            put("id", e.id)
            put("name", e.name)
            put("rfidUid", e.rfidUid)
            put("department", e.department)
            put("role", e.role)
            put("photoUrl", e.photoUrl)
            put("phone", e.phone)
            put("isActive", e.isActive)
            put("createdAt", e.createdAt)
        }
    }

    private fun attendanceToJson(a: AttendanceRecordItem): JSONObject {
        return JSONObject().apply {
            put("id", a.id)
            put("employeeId", a.employeeId)
            put("rfidUid", a.rfidUid)
            put("employeeName", a.employeeName)
            put("department", a.department)
            put("role", a.role)
            put("date", a.date)
            put("timeIn", a.timeIn)
            put("timeOut", a.timeOut)
            put("statusIn", a.statusIn)
            put("statusOut", a.statusOut)
            put("photoUrl", a.photoUrl)
            put("createdAt", a.createdAt)
        }
    }
}

