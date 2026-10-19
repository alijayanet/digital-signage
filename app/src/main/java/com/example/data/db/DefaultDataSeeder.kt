package com.example.data.db

import com.example.data.entity.AgendaItem
import com.example.data.entity.NoticeItem
import com.example.data.entity.RunningTickerItem
import com.example.data.entity.SignageConfig
import com.example.data.entity.SlideItem

object DefaultDataSeeder {

    fun getSchoolPreset(): PresetData {
        return PresetData(
            config = SignageConfig(
                id = 1,
                orgType = "Sekolah",
                orgName = "SMA NEGERI 1 TELADAN NUSANTARA",
                orgSubtitle = "Berakhlak Mulia, Unggul Akademik & Berwawasan Global",
                headerBannerText = "TERAKREDITASI A • SEKOLAH PENGGERAK ANGKATAN 2026",
                themeId = "high_density",
                layoutMode = "split_standard",
                slideIntervalSeconds = 7,
                runningTextSpeed = 1,
                orgLogoUrl = "",
                enableClock = true,
                enableAgendas = true,
                enableNotices = true,
                enableQuickInfo = true
            ),
            slides = listOf(
                SlideItem(
                    title = "Juara 1 Olimpiade Sains Nasional 2026",
                    caption = "Selamat kepada Ananda Rizky Pratama (Kelas XII MIPA 1) atas raihan Medali Emas Bidang Fisika tingkat Nasional.",
                    category = "PRESTASI SISWA",
                    imageUrl = "preset_school_1",
                    displayOrder = 1,
                    isActive = true,
                    durationSeconds = 8
                ),
                SlideItem(
                    title = "Pendaftaran Peserta Didik Baru (PPDB) 2026/2027",
                    caption = "Jalur Zonasi, Prestasi, dan Afirmasi dibuka serentak secara online melalui portal resmi sekolah mulai 1 Juli 2026.",
                    category = "INFORMASI PPDB",
                    imageUrl = "preset_school_2",
                    displayOrder = 2,
                    isActive = true,
                    durationSeconds = 8
                ),
                SlideItem(
                    title = "Gerakan Literasi & Digital Smart School",
                    caption = "Pemanfaatan Perpustakaan Digital & e-Learning untuk mewujudkan ekosistem belajar interaktif dan ramah lingkungan.",
                    category = "PROGRAM UNGGULAN",
                    imageUrl = "preset_school_3",
                    displayOrder = 3,
                    isActive = true,
                    durationSeconds = 7
                ),
                SlideItem(
                    title = "Pekan Seni & Gelar Karya P5",
                    caption = "Saksikan pameran inovasi proyek penguatan profil pelajar pancasila dan pentas seni kreasi nusantara di lapangan indoor.",
                    category = "KEGIATAN SISWA",
                    imageUrl = "preset_school_4",
                    displayOrder = 4,
                    isActive = true,
                    durationSeconds = 7
                ),
                SlideItem(
                    title = "Video Profil: Smart School Digital Signage",
                    caption = "Dokumentasi fasilitas modern, lab riset AI, dan kegiatan belajar mengajar interaktif.",
                    category = "VIDEO PROFIL",
                    imageUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    mediaType = "VIDEO",
                    displayOrder = 5,
                    isActive = true,
                    durationSeconds = 15
                )
            ),
            agendas = listOf(
                AgendaItem(
                    time = "07:00 - 07:45 WIB",
                    title = "Upacara Bendera & Apresiasi Prestasi Siswa",
                    location = "Lapangan Utama",
                    pic = "Pembina: Kepala Sekolah",
                    status = "COMPLETED",
                    displayOrder = 1
                ),
                AgendaItem(
                    time = "09:30 - 11:30 WIB",
                    title = "Simulasi Ujian Berbasis Komputer (ANBK)",
                    location = "Lab Komputer 1 & 2",
                    pic = "Tim Proktor & Teknisi",
                    status = "ONGOING",
                    displayOrder = 2
                ),
                AgendaItem(
                    time = "13:00 - 15:00 WIB",
                    title = "Workshop Kurikulum Merdeka & Pembelajaran AI",
                    location = "Ruang Multimedia",
                    pic = "Narasumber Diknas Prov.",
                    status = "UPCOMING",
                    displayOrder = 3
                ),
                AgendaItem(
                    time = "15:30 - 17:00 WIB",
                    title = "Latihan Rutin Tim Robotik & Futsal Sekolah",
                    location = "Gedung Olahraga & Lab Robotika",
                    pic = "Pembina Ekstrakurikuler",
                    status = "UPCOMING",
                    displayOrder = 4
                )
            ),
            notices = listOf(
                NoticeItem(
                    title = "Jadwal Pengambilan Raport Tengah Semester",
                    content = "Pengambilan raport oleh Orang Tua/Wali Murid dilaksanakan hari Jumat pukul 08:30 WIB di ruang kelas masing-masing.",
                    priority = "IMPORTANT",
                    dateText = "Jumat Ini",
                    displayOrder = 1
                ),
                NoticeItem(
                    title = "Himbauan Membawa Tumbler & Kotak Makan",
                    content = "Menuju Sekolah Adiwiyata Mandiri bebas sampah plastik sekali pakai di seluruh area kantin sekolah.",
                    priority = "INFO",
                    dateText = "Program Tetap",
                    displayOrder = 2
                ),
                NoticeItem(
                    title = "Batas Akhir Pengumpulan Tugas Portofolio P5",
                    content = "Seluruh kelompok wajib mengunggah video dokumentasi proyek ke e-Learning sekolah sebelum hari Sabtu pukul 23:59.",
                    priority = "URGENT",
                    dateText = "Sabtu, 23:59 WIB",
                    displayOrder = 3
                )
            ),
            tickers = listOf(
                RunningTickerItem(
                    badge = "INFO KELAS",
                    text = "Selamat datang di Smart Campus SMA Negeri 1 Teladan Nusantara • Tingkatkan kedisiplinan dan semangat belajar!",
                    displayOrder = 1
                ),
                RunningTickerItem(
                    badge = "PRESTASI",
                    text = "Selamat kepada Tim Basket Putra atas Juara 1 DBL Regional 2026 • Kebanggaan warga sekolah!",
                    displayOrder = 2
                ),
                RunningTickerItem(
                    badge = "PENGUMUMAN",
                    text = "Buku pelajaran semester ganjil dapat dipinjam di Perpustakaan Lantai 2 setiap jam istirahat.",
                    displayOrder = 3
                ),
                RunningTickerItem(
                    badge = "HIMBAUAN",
                    text = "Parkir kendaraan siswa hanya diizinkan di Area Parkir Timur dengan menunjukkan kartu pelajar aktif.",
                    displayOrder = 4
                )
            )
        )
    }

    fun getGovPreset(): PresetData {
        return PresetData(
            config = SignageConfig(
                id = 1,
                orgType = "Pemerintahan",
                orgName = "KANTOR KECAMATAN PRAJA NUSANTARA",
                orgSubtitle = "Pusat Pelayanan Terpadu Satu Pintu (PTSP) & Administrasi Kependudukan",
                headerBannerText = "ZONA INTEGRITAS • WILAYAH BEBAS DARI KORUPSI (WBK) & WBBM",
                themeId = "gov_nusantara",
                layoutMode = "split_standard",
                slideIntervalSeconds = 8,
                runningTextSpeed = 1,
                orgLogoUrl = "",
                enableClock = true,
                enableAgendas = true,
                enableNotices = true,
                enableQuickInfo = true
            ),
            slides = listOf(
                SlideItem(
                    title = "Layanan Administrasi Kependudukan Terpadu (Adminduk)",
                    caption = "Pembuatan KTP Elektronik, Kartu Keluarga, dan Akta Kelahiran kini lebih cepat dan bebas biaya retribusi (GRATIS).",
                    category = "PELAYANAN PUBLIK",
                    imageUrl = "preset_gov_1",
                    displayOrder = 1,
                    isActive = true,
                    durationSeconds = 8
                ),
                SlideItem(
                    title = "Penerapan Aplikasi Surat Online 'Warga Mandiri'",
                    caption = "Pengurusan surat keterangan domisili, SKTM, dan pengantar nikah dapat diakses langsung dari smartphone dari rumah.",
                    category = "INOVASI DIGITAL",
                    imageUrl = "preset_gov_2",
                    displayOrder = 2,
                    isActive = true,
                    durationSeconds = 8
                ),
                SlideItem(
                    title = "Komitmen Anti Pungli & Tolak Gratifikasi",
                    caption = "Seluruh pelayanan di lingkungan Kantor Kecamatan tidak dipungut biaya apapun. Laporkan jika menemukan indikasi pelanggaran.",
                    category = "ZONA INTEGRITAS",
                    imageUrl = "preset_gov_3",
                    displayOrder = 3,
                    isActive = true,
                    durationSeconds = 8
                ),
                SlideItem(
                    title = "Sosialisasi Pajak Bumi & Bangunan (PBB-P2)",
                    caption = "Manfaatkan loket pembayaran keliling Bank BJB/Bank Mandiri di halaman kantor kecamatan setiap hari kerja.",
                    category = "LAYANAN MASYARAKAT",
                    imageUrl = "preset_gov_4",
                    displayOrder = 4,
                    isActive = true,
                    durationSeconds = 7
                ),
                SlideItem(
                    title = "Video Layanan: Maklumat & Standar Pelayanan Publik",
                    caption = "Komitmen transparansi dan kemudahan birokrasi bagi seluruh lapisan masyarakat nusantara.",
                    category = "VIDEO LAYANAN",
                    imageUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    mediaType = "VIDEO",
                    displayOrder = 5,
                    isActive = true,
                    durationSeconds = 15
                )
            ),
            agendas = listOf(
                AgendaItem(
                    time = "08:00 - 08:30 WIB",
                    title = "Apel Pagi Pegawai & Briefing Pelayanan Prima",
                    location = "Halaman Kantor",
                    pic = "Pimpinan: Bapak Camat",
                    status = "COMPLETED",
                    displayOrder = 1
                ),
                AgendaItem(
                    time = "09:00 - 12:00 WIB",
                    title = "Pelayanan Adminduk & Verifikasi Berkas PTSP",
                    location = "Loket 1 - 6 Ruang PTSP",
                    pic = "Petugas Front Office",
                    status = "ONGOING",
                    displayOrder = 2
                ),
                AgendaItem(
                    time = "13:30 - 15:30 WIB",
                    title = "Musyawarah Perencanaan Pembangunan (Musrenbang)",
                    location = "Ruang Rapat Utama Lt. 2",
                    pic = "Bappeda & Tokoh Masyarakat",
                    status = "UPCOMING",
                    displayOrder = 3
                ),
                AgendaItem(
                    time = "15:30 - 16:00 WIB",
                    title = "Rekapitulasi Harian & Evaluasi Indeks Kepuasan",
                    location = "Ruang Data & IT",
                    pic = "Kasie Pelayanan Umum",
                    status = "UPCOMING",
                    displayOrder = 4
                )
            ),
            notices = listOf(
                NoticeItem(
                    title = "Jam Operasional Pelayanan Masyarakat",
                    content = "Senin s/d Kamis: 08.00 - 15.30 WIB (Istirahat 12.00 - 13.00 WIB) | Jumat: 08.00 - 16.00 WIB (Istirahat 11.30 - 13.00 WIB).",
                    priority = "IMPORTANT",
                    dateText = "Jam Resmi",
                    displayOrder = 1
                ),
                NoticeItem(
                    title = "Prioritas Khusus Lansia, Ibu Hamil & Disabilitas",
                    content = "Tersedia Loket Khusus Ramah Rentan (Loket 1) dan fasilitas kursi roda di lobi depan.",
                    priority = "INFO",
                    dateText = "Fasilitas Prioritas",
                    displayOrder = 2
                ),
                NoticeItem(
                    title = "Perekaman e-KTP Pemilih Pemula (Usia 17 Th)",
                    content = "Bagi pelajar/pemuda yang menginjak usia 17 tahun dapat langsung membawa fotokopi KK ke loket perekaman biometrik.",
                    priority = "IMPORTANT",
                    dateText = "Program Khusus",
                    displayOrder = 3
                )
            ),
            tickers = listOf(
                RunningTickerItem(
                    badge = "LAYANAN",
                    text = "Selamat datang di Kantor Kecamatan Praja Nusantara • Melayani dengan Sepenuh Hati, Cepat, Tepat, dan Transparan.",
                    displayOrder = 1
                ),
                RunningTickerItem(
                    badge = "HIMBAUAN",
                    text = "Seluruh pelayanan GRATIS tanpa pungutan liar. Hubungi Hotline Pengaduan: 0812-9988-7766 jika ada pungli.",
                    displayOrder = 2
                ),
                RunningTickerItem(
                    badge = "INFO PAJAK",
                    text = "Jatuh Tempo Pembayaran PBB-P2 tanggal 31 Agustus 2026. Lakukan pembayaran tepat waktu untuk pembangunan daerah kita.",
                    displayOrder = 3
                ),
                RunningTickerItem(
                    badge = "SIAP SIAGA",
                    text = "Call Center Tanggap Darurat & Bencana Kecamatan: 112 (Bebas Pulsa 24 Jam).",
                    displayOrder = 4
                )
            )
        )
    }

    fun getDefaultEmployees(): List<com.example.data.entity.EmployeeItem> {
        return listOf(
            com.example.data.entity.EmployeeItem(
                name = "Drs. H. Bambang Sutrisno, M.Pd",
                rfidUid = "12345678",
                department = "Pimpinan",
                role = "Kepala Sekolah",
                isActive = true
            ),
            com.example.data.entity.EmployeeItem(
                name = "Siti Rahmawati, S.Pd",
                rfidUid = "87654321",
                department = "Dewan Guru",
                role = "Guru",
                isActive = true
            ),
            com.example.data.entity.EmployeeItem(
                name = "Ahmad Fauzi, S.Kom",
                rfidUid = "A1B2C3D4",
                department = "Laboratorium & IT",
                role = "Staf IT",
                isActive = true
            ),
            com.example.data.entity.EmployeeItem(
                name = "Rudi Hartono",
                rfidUid = "11223344",
                department = "Tata Usaha",
                role = "Staf Administrasi",
                isActive = true
            )
        )
    }
}

data class PresetData(
    val config: SignageConfig,
    val slides: List<SlideItem>,
    val agendas: List<AgendaItem>,
    val notices: List<NoticeItem>,
    val tickers: List<RunningTickerItem>
)

