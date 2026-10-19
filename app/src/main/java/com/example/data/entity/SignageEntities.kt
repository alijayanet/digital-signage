package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signage_config")
data class SignageConfig(
    @PrimaryKey val id: Int = 1,
    val orgType: String = "Sekolah", // "Sekolah", "Pemerintahan", "Umum"
    val orgName: String = "SMA NEGERI 1 TELADAN",
    val orgSubtitle: String = "Mewujudkan Generasi Cerdas, Berkarakter & Berdaya Saing Global",
    val headerBannerText: String = "TERAKREDITASI A • SEKOLAH PENGGERAK",
    val themeId: String = "school_modern", // "school_modern", "gov_nusantara", "cyber_blue", "minimal_clean", "sunset_warm"
    val layoutMode: String = "split_standard", // "split_standard", "hero_full"
    val slideIntervalSeconds: Int = 7,
    val runningTextSpeed: Int = 1, // 0 = lambat, 1 = normal, 2 = cepat
    val orgLogoUrl: String = "",
    val enableClock: Boolean = true,
    val enableAgendas: Boolean = true,
    val enableNotices: Boolean = true,
    val enableQuickInfo: Boolean = true,
    val serverPort: Int = 8080,
    val activeFlashAlert: String = "",
    val flashAlertTimestamp: Long = 0L,
    val telegramBotEnabled: Boolean = false,
    val telegramBotToken: String = "",
    val telegramBotAuthorizedUserIds: String = "",
    val enableAttendance: Boolean = true,
    val checkInLateLimit: String = "07:30",
    val checkOutStart: String = "14:00",
    val employeeRoles: String = "Karyawan, Guru, Staf TU, Pimpinan, Teknisi, Keamanan, Kebersihan",
    val cityName: String = "Jakarta Pusat",
    val latitude: Double = -6.2088,
    val longitude: Double = 106.8456,
    val timezone: Double = 7.0
) {
    fun getTelegramAuthorizedIdList(): List<Long> {
        if (telegramBotAuthorizedUserIds.isBlank()) return emptyList()
        return telegramBotAuthorizedUserIds.split(",", ";", " ", "\n")
            .mapNotNull { it.trim().toLongOrNull() }
    }

    fun getEmployeeRoleList(): List<String> {
        if (employeeRoles.isBlank()) return listOf("Karyawan", "Guru", "Staf TU", "Pimpinan", "Teknisi", "Keamanan", "Kebersihan")
        return employeeRoles.split(",", ";", "\n").map { it.trim() }.filter { it.isNotBlank() }
    }
}


@Entity(tableName = "slide_items")
data class SlideItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val caption: String,
    val category: String = "INFORMASI", // "INFORMASI", "PRESTASI", "KEGIATAN", "LAYANAN", "HIMBAUAN"
    val imageUrl: String, // URL, local file path, or preset ID
    val mediaType: String = "IMAGE", // "IMAGE", "VIDEO"
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val durationSeconds: Int = 7
)

@Entity(tableName = "agenda_items")
data class AgendaItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: String, // e.g. "08:00 - 10:00 WIB"
    val title: String,
    val location: String = "Aula Utama",
    val pic: String = "", // Penanggung jawab / Pemateri
    val status: String = "UPCOMING", // "ONGOING", "UPCOMING", "COMPLETED"
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "notice_items")
data class NoticeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val priority: String = "INFO", // "URGENT", "IMPORTANT", "INFO"
    val dateText: String = "Hari Ini",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "running_ticker_items")
data class RunningTickerItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val badge: String = "PENGUMUMAN", // "PENGUMUMAN", "BERITA", "HIMBAUAN", "LAYANAN"
    val text: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "employee_items")
data class EmployeeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val rfidUid: String,
    val department: String = "Umum", // Divisi / Bagian / Jabatan / Guru / TU
    val role: String = "Karyawan", // Karyawan, Guru, Staf, Magang
    val photoUrl: String = "",
    val phone: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long = 0,
    val rfidUid: String,
    val employeeName: String,
    val department: String = "",
    val role: String = "",
    val date: String, // yyyy-MM-dd
    val timeIn: String = "", // HH:mm:ss
    val timeOut: String = "", // HH:mm:ss
    val statusIn: String = "HADIR", // HADIR, TERLAMBAT, TIDAK MASUK
    val statusOut: String = "", // PULANG
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

