package com.example.util

import java.util.Calendar
import java.util.Locale
import kotlin.math.*

data class PrayerSchedule(
    val subuh: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String
)

data class PrayerTimeItem(
    val name: String,
    val time: String,
    val isNext: Boolean
)

object PrayerCalculator {

    private fun d2r(d: Double): Double = d * Math.PI / 180.0
    private fun r2d(r: Double): Double = r * 180.0 / Math.PI

    private fun fixHour(a: Double): Double {
        var res = a - 24.0 * floor(a / 24.0)
        if (res < 0) res += 24.0
        return res
    }

    private fun fixAngle(a: Double): Double {
        var res = a - 360.0 * floor(a / 360.0)
        if (res < 0) res += 360.0
        return res
    }

    /**
     * Calculates astronomical prayer times (Kemenag standard: Fajr 20°, Isha 18°).
     * Defaults to WIB (Jakarta / Jawa / Sumatera -6.2088, 106.8456, GMT+7) with safety buffer.
     */
    fun calculatePrayerTimes(
        calendar: Calendar = Calendar.getInstance(),
        lat: Double = -6.2088,
        lng: Double = 106.8456,
        timezone: Double = 7.0
    ): PrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian date
        val a = floor((14.0 - month) / 12.0)
        val y = year + 4800.0 - a
        val m = month + 12.0 * a - 3.0
        val julianDate = day + floor((153.0 * m + 2.0) / 5.0) + 365.0 * y + floor(y / 4.0) - floor(y / 100.0) + floor(y / 400.0) - 32045.0
        val d = julianDate - 2451545.0

        // Sun position
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(d2r(g)) + 0.020 * sin(d2r(2.0 * g)))

        val e = 23.439 - 0.00000036 * d
        val dd = r2d(asin(sin(d2r(e)) * sin(d2r(l))))
        val ra = fixAngle(r2d(atan2(cos(d2r(e)) * sin(d2r(l)), cos(d2r(l))))) / 15.0

        val eqt = q / 15.0 - ra
        val noon = fixHour(12.0 + timezone - lng / 15.0 - eqt)

        fun sunAngleTime(angle: Double, isMorning: Boolean): Double {
            val cosH = (sin(d2r(-angle)) - sin(d2r(lat)) * sin(d2r(dd))) / (cos(d2r(lat)) * cos(d2r(dd)))
            if (cosH > 1.0 || cosH < -1.0) return noon
            val h = r2d(acos(cosH)) / 15.0
            return fixHour(if (isMorning) noon - h else noon + h)
        }

        val fajr = sunAngleTime(20.0, true) // Subuh (Kemenag 20°)
        val asrAlt = r2d(atan(1.0 / (1.0 + tan(d2r(abs(lat - dd))))))
        val asr = sunAngleTime(-asrAlt, false) // Ashar
        val maghrib = sunAngleTime(0.833, false) // Maghrib (Sunset)
        val isha = sunAngleTime(18.0, false) // Isya (Kemenag 18°)

        // Add standard 2-minute ihtiyat (safety buffer) recommended by Kemenag RI
        fun formatWithBuffer(hours: Double, bufferMinutes: Int = 2): String {
            val totalMinutes = (hours * 60.0).roundToInt() + bufferMinutes
            val adjustedMinutes = (totalMinutes % (24 * 60) + (24 * 60)) % (24 * 60)
            val h = adjustedMinutes / 60
            val min = adjustedMinutes % 60
            return String.format(Locale.getDefault(), "%02d:%02d", h, min)
        }

        return PrayerSchedule(
            subuh = formatWithBuffer(fajr, 2),
            dzuhur = formatWithBuffer(noon, 2),
            ashar = formatWithBuffer(asr, 2),
            maghrib = formatWithBuffer(maghrib, 2),
            isya = formatWithBuffer(isha, 2)
        )
    }

    /**
     * Determines which prayer time is the next upcoming prayer.
     */
    fun getPrayerTimeItems(
        calendar: Calendar = Calendar.getInstance(),
        schedule: PrayerSchedule
    ): List<PrayerTimeItem> {
        val currentMin = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        fun toMinutes(timeStr: String): Int {
            val parts = timeStr.split(":")
            if (parts.size == 2) {
                return (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
            }
            return 0
        }

        val list = listOf(
            "SUBUH" to toMinutes(schedule.subuh),
            "DZUHUR" to toMinutes(schedule.dzuhur),
            "ASHAR" to toMinutes(schedule.ashar),
            "MAGHRIB" to toMinutes(schedule.maghrib),
            "ISYA" to toMinutes(schedule.isya)
        )

        // Next prayer is the first prayer whose minute > currentMin
        val nextPair = list.firstOrNull { it.second > currentMin } ?: list.first() // If past Isya, next is Subuh

        return listOf(
            PrayerTimeItem("SUBUH", schedule.subuh, nextPair.first == "SUBUH"),
            PrayerTimeItem("DZUHUR", schedule.dzuhur, nextPair.first == "DZUHUR"),
            PrayerTimeItem("ASHAR", schedule.ashar, nextPair.first == "ASHAR"),
            PrayerTimeItem("MAGHRIB", schedule.maghrib, nextPair.first == "MAGHRIB"),
            PrayerTimeItem("ISYA", schedule.isya, nextPair.first == "ISYA")
        )
    }
}
