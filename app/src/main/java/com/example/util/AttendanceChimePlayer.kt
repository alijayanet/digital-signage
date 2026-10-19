package com.example.util

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

/**
 * Player nada bel sintetis untuk presensi RFID (Check-In & Check-Out).
 * Menghasilkan nada airport/lobby chime berkelas via PCM 16-bit AudioTrack,
 * dengan fallback aman ke ToneGenerator.
 * Strictly Bell/Chime only — NO Text-To-Speech (TTS).
 */
class AttendanceChimePlayer(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    companion object {
        private const val TAG = "AttendanceChime"
        private const val SAMPLE_RATE = 44100
    }

    private var toneGen: ToneGenerator? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (t: Throwable) {
            Log.w(TAG, "ToneGenerator init notice: ${t.message}")
        }
    }

    /**
     * Bel Masuk (Check-In): Dua nada harmoni D5 (587.33 Hz) lalu A4 (440.00 Hz)
     */
    fun playCheckInChime() {
        scope.launch {
            try {
                playDualToneChime(
                    freq1 = 587.33, dur1Sec = 0.22, decay1 = 3.2,
                    freq2 = 440.00, dur2Sec = 0.38, decay2 = 2.6
                )
            } catch (e: Throwable) {
                playFallbackTone(ToneGenerator.TONE_PROP_BEEP2, 350)
            }
        }
    }

    /**
     * Bel Pulang (Check-Out): Nada harmoni A4 (440.00 Hz) lalu D5 (587.33 Hz)
     */
    fun playCheckOutChime() {
        scope.launch {
            try {
                playDualToneChime(
                    freq1 = 440.00, dur1Sec = 0.20, decay1 = 3.0,
                    freq2 = 587.33, dur2Sec = 0.40, decay2 = 2.5
                )
            } catch (e: Throwable) {
                playFallbackTone(ToneGenerator.TONE_PROP_ACK, 350)
            }
        }
    }

    /**
     * Nada Peringatan: Sudah absen masuk/pulang sebelumnya (Double tap)
     */
    fun playWarningChime() {
        scope.launch {
            try {
                playDualToneChime(
                    freq1 = 659.25, dur1Sec = 0.15, decay1 = 4.0,
                    freq2 = 659.25, dur2Sec = 0.18, decay2 = 3.5
                )
            } catch (e: Throwable) {
                playFallbackTone(ToneGenerator.TONE_PROP_BEEP, 250)
            }
        }
    }

    /**
     * Nada Kartu Tidak Dikenal: Soft alert tone
     */
    fun playUnknownCardChime() {
        scope.launch {
            try {
                playDualToneChime(
                    freq1 = 400.00, dur1Sec = 0.18, decay1 = 3.5,
                    freq2 = 320.00, dur2Sec = 0.25, decay2 = 3.0
                )
            } catch (e: Throwable) {
                playFallbackTone(ToneGenerator.TONE_PROP_NACK, 300)
            }
        }
    }

    private fun playDualToneChime(
        freq1: Double, dur1Sec: Double, decay1: Double,
        freq2: Double, dur2Sec: Double, decay2: Double
    ) {
        val n1 = (SAMPLE_RATE * dur1Sec).toInt()
        val n2 = (SAMPLE_RATE * dur2Sec).toInt()
        val totalSamples = n1 + n2
        val buffer = ShortArray(totalSamples)

        // Tone 1
        for (i in 0 until n1) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-decay1 * (i.toDouble() / n1))
            val sample = (sin(2.0 * Math.PI * freq1 * t) + 0.28 * sin(4.0 * Math.PI * freq1 * t)) * env
            buffer[i] = (sample * 24000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        // Tone 2
        for (i in 0 until n2) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-decay2 * (i.toDouble() / n2))
            val sample = (sin(2.0 * Math.PI * freq2 * t) + 0.28 * sin(4.0 * Math.PI * freq2 * t)) * env
            buffer[n1 + i] = (sample * 24000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        val minBuf = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            (buffer.size * 2).coerceAtLeast(minBuf),
            AudioTrack.MODE_STATIC
        )
        try {
            track.write(buffer, 0, buffer.size)
            track.play()
            val totalSleep = ((dur1Sec + dur2Sec) * 1000).toLong() + 50L
            Thread.sleep(totalSleep.coerceAtMost(1000L))
        } finally {
            try { track.stop() } catch (_: Throwable) {}
            try { track.release() } catch (_: Throwable) {}
        }
    }

    private fun playFallbackTone(toneType: Int, durationMs: Int) {
        try {
            toneGen?.startTone(toneType, durationMs)
        } catch (_: Throwable) {}
    }

    fun release() {
        try {
            toneGen?.release()
            toneGen = null
        } catch (_: Throwable) {}
    }
}
