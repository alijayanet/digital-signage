# 📺 Digital Signage TV & Smart Attendance System

[![Platform](https://img.shields.io/badge/Platform-Android%20TV%20%7C%20TV%20Box-blue.svg)](https://developer.android.com/tv)
[![Package](https://img.shields.io/badge/Package-com.alijayanet.signage-green.svg)](app/build.gradle.kts)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Android%20TV-orange.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](#kontak--dukungan)

**Digital Signage TV** adalah aplikasi papan informasi digital (*smart display*) berbasis Android TV / Android TV Box modern yang dirancang untuk instansi, sekolah, perkantoran, dan masjid. Aplikasi ini menggabungkan penayangan konten multimedia berkualitas tinggi, papan informasi real-time, jadwal sholat 5 waktu akurat, serta sistem presensi RFID karyawan mandiri (*standalone*) langsung di layar TV.

---

## 👨‍💻 Pembuat & Pengembang

* **Nama Pengembang / Studio:** **ALIJAYA-NET**
* **Kontak / WhatsApp:** **081947215703** (Hp. [0819-4721-5703](https://wa.me/6281947215703))
* **Repositori Resmi:** [github.com/alijayanet/digital-signage](https://github.com/alijayanet/digital-signage)

---

## 📥 Download File APK Siap Pasang

File APK yang sudah dikompilasi siap diunduh dan dipasang pada Android TV / TV Box:

* 📦 **File APK:** [**`Digital-Signage-TV.apk`**](./Digital-Signage-TV.apk) *(~26.8 MB)*
* 🏷️ **Application ID:** `com.alijayanet.signage`
* 📱 **Minimum OS:** Android 7.0 (Nougat / API 24) hingga Android 14+ (Google TV / Android TV)

---

## ✨ Fitur-Fitur Utama

### 1. 🖼️ Tampilan Multimedia Widescreen 16:9 Utuh (*Full Fit*)
- Proporsi layar optimal **70% Konten Media : 30% Kalender & Info**.
- Slide Carousel untuk gambar (JPG, PNG, WebP) dengan rendering *Full Fit* tanpa terpotong dan efek ambient glow lembut.
- Pemutar Video Terintegrasi (MP4, MKV) menggunakan Google ExoPlayer / AndroidX Media3 dengan aspect ratio presisi.
- Konten media dapat diunggah langsung melalui jaringan Wi-Fi/LAN via Web Remote.

### 2. 📅 Kalender Masehi & Jadwal Waktu Sholat 5 Waktu
- Tampilan kalender bulanan lengkap dengan penanda tanggal hari ini dan hari Minggu berwarna merah kontras.
- Strip jadwal sholat 5 waktu mandiri (**SUBUH**, **DZUHUR**, **ASHAR**, **MAGHRIB**, **ISYA**).
- Algoritma astronomis Kemenag RI yang menghitung waktu sholat secara presisi berdasarkan posisi koordinat TV.
- Penanda waktu sholat berikutnya (*Next Prayer Highlight*) berwarna aksen emas elegan.

### 3. 📍 Pengaturan Lokasi Peta Interaktif & Zona Waktu (WIB, WITA, WIT)
- Dilengkapi **Peta Interaktif Leaflet / OpenStreetMap** pada Web Remote dengan pin penanda yang dapat digeser (*draggable*).
- **Pencarian Kota Indonesia:** Database daftar kota dan kabupaten di seluruh provinsi Indonesia yang otomatis memusatkan koordinat dan mendeteksi zona waktu.
- Pilihan zona waktu resmi:
  - `WIB` (Waktu Indonesia Barat - GMT+7)
  - `WITA` (Waktu Indonesia Tengah - GMT+8)
  - `WIT` (Waktu Indonesia Timur - GMT+9)
- Nama kota dan zona waktu langsung ditampilkan di header jadwal TV (misal: `WAKTU SHOLAT • KOTA BANDUNG (WIB)`).
- Tetap dapat berfungsi 100% secara offline tanpa koneksi internet.

### 4. 🪪 Sistem Presensi RFID Standalone (Mandiri di Layar TV)
- Mendukung USB RFID Card Reader (125kHz / 13.56MHz) standar keyboard emulation / HID.
- **Deteksi Rentang Waktu Otomatis**:
  - **Jam Masuk:** Scan sebelum pukul `14:00` (Tepat Waktu $\le$ `07:30`, Terlambat $>$ `07:30`).
  - **Jam Pulang:** Scan mulai pukul `14:00` ke atas.
- **Audio Chime Bell Harmonis:** Bunyi bel sintesis PCM 44.1kHz murni dua nada (*Ding-Dong*) tanpa suara robot TTS yang mengganggu.
- **Floating Corner Overlay:** Pop-up notifikasi kehadiran elegan di sudut kanan bawah TV berdurasi 4,5 detik yang menampilkan foto pegawai, nama, role, waktu scan, dan status kehadiran tanpa menutupi slide utama.
- **Manajemen Pegawai & Log Presensi di Web Remote**:
  - Penambahan & pengeditan Role/Kategori pegawai secara dinamis.
  - Upload foto profil pegawai langsung dari perangkat lokal (HP/Laptop).
  - Ekspor & Impor database Pegawai serta Log Presensi dalam format CSV / Excel.
  - Rekapitulasi presensi harian dan bulanan lengkap.

### 5. 📢 Papan Pengumuman Resmi (*Auto-Rotating Crossfade*)
- Kartu pengumuman dinamis di bawah kalender dengan transisi *Crossfade* halus yang berganti secara otomatis setiap **8 detik**.
- Dilengkapi indikator halaman (*pager dots*) dan animasi *progress bar*.
- Tipografi tajam dan nyaman dibaca dari kejauhan dengan label kategori (*PENTING*, *HIMBAUAN*, *INFORMASI*).

### 6. 📜 Running Text (Marquee) Kustom
- Teks berjalan di bagian bawah layar TV dengan kecepatan dan warna yang dapat diatur sesuka hati melalui Web Remote.

### 7. 🌐 Web Remote Management Dashboard (`http://<IP-TV>:8080`)
- Server HTTP lokal mandiri tertanam (*Embedded Ktor Server*) di port `8080`.
- Administrator dapat mengelola seluruh konten TV dari browser HP, tablet, atau laptop yang terhubung ke satu jaringan Wi-Fi/LAN tanpa perlu menginstal aplikasi tambahan di HP.
- Terdapat QR Code di layar TV yang dapat dipindai langsung dengan kamera HP untuk membuka Web Remote.

---

## 🛠️ Arsitektur & Teknologi

* **Bahasa:** Kotlin 100%
* **UI Framework:** Jetpack Compose for TV & Material 3
* **Arsitektur:** MVVM (Model-View-ViewModel) + Repository Pattern
* **Database:** Android Room Database (v6) dengan Coroutines & Flow
* **Embedded Server:** Ktor HTTP Server (Netty Engine) + HTML5 Responsive Dashboard
* **Multimedia:** AndroidX Media3 / ExoPlayer 1.4 + Coil Image Loading
* **Peta:** Leaflet.js + OpenStreetMap Tile Layer
* **Audio:** Android AudioTrack Native Synthesizer (PCM 16-bit 44.1 kHz)

---

## 🚀 Panduan Instalasi & Penggunaan

### Langkah 1: Pemasangan APK di Android TV / TV Box
1. Unduh file [**`Digital-Signage-TV.apk`**](./Digital-Signage-TV.apk).
2. Salin file APK ke dalam Flashdisk USB.
3. Tancapkan Flashdisk ke port USB di Android TV / TV Box.
4. Buka aplikasi **File Manager** di TV Anda, cari file `Digital-Signage-TV.apk`, lalu klik **Install**.
5. Setelah terpasang, buka aplikasi **Digital Signage TV**.

### Langkah 2: Menghubungkan RFID Reader
1. Tancapkan USB RFID Card Reader ke salah satu port USB di Android TV Box.
2. RFID Reader akan otomatis terbaca sebagai input keyboard tanpa perlu driver khusus.
3. Tempelkan kartu RFID pegawai ke reader; layar TV akan berbunyi bel dan menampilkan kartu presensi pegawai di sudut kanan bawah.

### Langkah 3: Mengakses Web Remote Control
1. Pastikan HP atau Laptop Anda terhubung ke jaringan Wi-Fi yang sama dengan Android TV Box.
2. Di pojok layar TV akan tertera alamat IP dan QR Code (contoh: `http://192.168.1.100:8080`).
3. Scan QR Code dengan kamera HP atau buka browser di laptop dan ketikkan alamat URL tersebut.
4. Pada dashboard web, Anda dapat:
   - Mengunggah foto slide dan video baru.
   - Mengatur teks berjalan (*running text*).
   - Memilih lokasi kota dan zona waktu (WIB/WITA/WIT) melalui peta.
   - Mengatur data pegawai (tambah, edit, import/export CSV, upload foto).
   - Melihat dan mengunduh rekap presensi bulanan/harian.

---

## 📂 Struktur Repositori

```text
Digital-Signage-TV/
├── app/
│   ├── src/main/java/com/example/
│   │   ├── MainActivity.kt                # Main Entry Point & RFID Key Interceptor
│   │   ├── data/
│   │   │   ├── db/                        # Room Database, DAO, & Repository
│   │   │   └── entity/                    # Entities (Slide, Notice, Employee, Attendance, Config)
│   │   ├── server/                        # Embedded Ktor HTTP Server & Web Dashboard HTML
│   │   ├── ui/
│   │   │   ├── components/                # Slide Carousel, Kalender, Sholat, Overlay, dsb.
│   │   │   ├── screens/                   # SignageScreen layout 70:30
│   │   │   └── theme/                     # Color palette, Typography, & Theme
│   │   └── util/                          # PrayerCalculator, Chime Player, City Data, CSV Utils
│   └── build.gradle.kts                   # Konfigurasi Gradle aplikasi
├── gradle/                                # Gradle Wrapper & Version Catalog
├── Digital-Signage-TV.apk                 # Binary APK Siap Pasang
├── build.gradle.kts                       # Root build configuration
├── settings.gradle.kts                    # Root settings
└── README.md                              # Dokumentasi Proyek
```

---

## 📞 Kontak & Dukungan

Untuk informasi lebih lanjut, pemesanan kustomisasi, instalasi onsite, atau integrasi perangkat keras, silakan hubungi:

* **Pengembang:** **ALIJAYA-NET**
* **No. Handphone / WhatsApp:** [**081947215703**](https://wa.me/6281947215703) *(0819-4721-5703)*
* **GitHub Repository:** [alijayanet/digital-signage](https://github.com/alijayanet/digital-signage)

---
*© 2026 ALIJAYA-NET. Seluruh hak cipta dilindungi undang-undang.*
