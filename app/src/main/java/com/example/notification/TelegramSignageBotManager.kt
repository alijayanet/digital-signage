package com.example.notification

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object TelegramSignageBotManager {

    private const val TAG = "TelegramBotManager"
    private const val BASE_URL = "https://api.telegram.org/bot"

    /**
     * Constructs the interactive Inline Keyboard JSON for main menu.
     */
    fun buildMainMenuKeyboard(): String {
        return JSONObject().apply {
            put("inline_keyboard", JSONArray().apply {
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🏢 Profil & Instansi"); put("callback_data", "menu_profil") })
                    put(JSONObject().apply { put("text", "🎨 Tema Layar TV"); put("callback_data", "menu_tema") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🖼️ Daftar Slide"); put("callback_data", "menu_slide") })
                    put(JSONObject().apply { put("text", "📜 Running Text"); put("callback_data", "menu_ticker") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "📅 Agenda Hari Ini"); put("callback_data", "menu_agenda") })
                    put(JSONObject().apply { put("text", "📢 Pengumuman"); put("callback_data", "menu_notice") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🚨 Pesan Kilat TV"); put("callback_data", "menu_alert") })
                    put(JSONObject().apply { put("text", "✨ Template 1-Klik"); put("callback_data", "menu_preset") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🔄 Cek Status TV & IP"); put("callback_data", "cmd_status") })
                    put(JSONObject().apply { put("text", "❓ Bantuan Perintah"); put("callback_data", "cmd_help") })
                })
            })
        }.toString()
    }

    fun buildThemeMenuKeyboard(): String {
        return JSONObject().apply {
            put("inline_keyboard", JSONArray().apply {
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🏫 Sekolah Modern (Hijau/Cyan)"); put("callback_data", "set_theme_school") })
                    put(JSONObject().apply { put("text", "🏛️ Gov Nusantara (Biru/Emas)"); put("callback_data", "set_theme_gov") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "⚡ Cyber Tech (Cyan/Biru)"); put("callback_data", "set_theme_cyber") })
                    put(JSONObject().apply { put("text", "✨ Minimal Clean (Indigo)"); put("callback_data", "set_theme_minimal") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🌅 Sunset Gold (Merah/Emas)"); put("callback_data", "set_theme_sunset") })
                    put(JSONObject().apply { put("text", "🌌 High Density M3"); put("callback_data", "set_theme_m3") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🔙 Kembali ke Menu Utama"); put("callback_data", "menu_main") })
                })
            })
        }.toString()
    }

    fun buildPresetMenuKeyboard(): String {
        return JSONObject().apply {
            put("inline_keyboard", JSONArray().apply {
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🏫 Terapkan Template Sekolah"); put("callback_data", "apply_preset_school") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🏛️ Terapkan Template Pemerintahan"); put("callback_data", "apply_preset_gov") })
                })
                put(JSONArray().apply {
                    put(JSONObject().apply { put("text", "🔙 Kembali ke Menu Utama"); put("callback_data", "menu_main") })
                })
            })
        }.toString()
    }

    /**
     * Deletes webhook to ensure getUpdates (long polling) works smoothly.
     */
    suspend fun deleteWebhook(token: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL$token/deleteWebhook?drop_pending_updates=false")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
            }
            val ok = conn.responseCode == 200
            Log.i(TAG, "deleteWebhook result code: ${conn.responseCode}")
            ok
        } catch (e: Exception) {
            Log.e(TAG, "deleteWebhook error: ${e.message}")
            false
        }
    }

    /**
     * Calls getUpdates using long-polling.
     */
    suspend fun getUpdates(token: String, offset: Long, timeoutSeconds: Int = 20): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL$token/getUpdates?offset=$offset&timeout=$timeoutSeconds")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = (timeoutSeconds + 15) * 1000
            }

            val code = conn.responseCode
            if (code == 200) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                val err = conn.errorStream?.bufferedReader()?.readText() ?: ""
                Log.w(TAG, "getUpdates error $code: $err")
                if (code == 409) {
                    Log.i(TAG, "Detected webhook conflict (409). Clearing webhook...")
                    deleteWebhook(token)
                }
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "getUpdates poll notice: ${e.message}")
            null
        }
    }

    /**
     * Sends a text message to a specific Telegram chat with optional Inline Keyboard.
     */
    suspend fun sendMessage(
        token: String,
        chatId: Long,
        text: String,
        replyMarkupJson: String? = null,
        parseMode: String = "Markdown"
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL$token/sendMessage")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }

            val payload = JSONObject().apply {
                put("chat_id", chatId)
                put("text", text)
                if (parseMode.isNotBlank()) put("parse_mode", parseMode)
                if (!replyMarkupJson.isNullOrBlank()) {
                    put("reply_markup", JSONObject(replyMarkupJson))
                }
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use {
                it.write(payload.toString())
                it.flush()
            }

            val code = conn.responseCode
            if (code != 200) {
                val err = conn.errorStream?.bufferedReader()?.readText() ?: ""
                Log.w(TAG, "sendMessage error: $code - $err")
                // If failed due to Markdown formatting error (HTTP 400), retry as plain text!
                if (code == 400 && parseMode.isNotBlank()) {
                    return@withContext sendPlainMessage(token, chatId, text, replyMarkupJson)
                }
            }
            code == 200
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sendMessage: ${e.message}", e)
            false
        }
    }

    private suspend fun sendPlainMessage(
        token: String,
        chatId: Long,
        text: String,
        replyMarkupJson: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val cleanText = text.replace("*", "").replace("`", "").replace("_", "")
            val url = URL("$BASE_URL$token/sendMessage")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }
            val payload = JSONObject().apply {
                put("chat_id", chatId)
                put("text", cleanText)
                if (!replyMarkupJson.isNullOrBlank()) {
                    put("reply_markup", JSONObject(replyMarkupJson))
                }
            }
            OutputStreamWriter(conn.outputStream, "UTF-8").use {
                it.write(payload.toString())
                it.flush()
            }
            conn.responseCode == 200
        } catch (e: Exception) {
            Log.e(TAG, "sendPlainMessage error: ${e.message}")
            false
        }
    }

    /**
     * Answers callback query (removes loading state in Telegram UI).
     */
    suspend fun answerCallbackQuery(
        token: String,
        callbackQueryId: String,
        text: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL$token/answerCallbackQuery")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                doOutput = true
                connectTimeout = 8000
                readTimeout = 8000
            }

            val payload = JSONObject().apply {
                put("callback_query_id", callbackQueryId)
                if (!text.isNullOrBlank()) put("text", text)
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use {
                it.write(payload.toString())
                it.flush()
            }

            conn.responseCode == 200
        } catch (e: Exception) {
            Log.e(TAG, "answerCallbackQuery error: ${e.message}")
            false
        }
    }

    /**
     * Downloads a photo sent via Telegram to app internal storage.
     */
    suspend fun downloadTelegramPhoto(
        context: Context,
        token: String,
        fileId: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            // 1. getFile
            val getFileUrl = URL("$BASE_URL$token/getFile?file_id=$fileId")
            val conn1 = (getFileUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
            }
            if (conn1.responseCode != 200) return@withContext null
            val respStr = conn1.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(respStr)
            if (!json.optBoolean("ok", false)) return@withContext null
            val filePath = json.getJSONObject("result").getString("file_path")

            // 2. Download file stream
            val downloadUrl = URL("https://api.telegram.org/file/bot$token/$filePath")
            val conn2 = (downloadUrl.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 30000
            }
            if (conn2.responseCode != 200) return@withContext null

            val imagesDir = File(context.filesDir, "media/images").apply { if (!exists()) mkdirs() }
            val ext = filePath.substringAfterLast(".", "jpg")
            val filename = "tg_slide_${UUID.randomUUID()}.$ext"
            val localFile = File(imagesDir, filename)

            conn2.inputStream.use { input ->
                FileOutputStream(localFile).use { output ->
                    input.copyTo(output)
                }
            }
            "/images/$filename"
        } catch (e: Exception) {
            Log.e(TAG, "downloadTelegramPhoto error: ${e.message}", e)
            null
        }
    }
}
