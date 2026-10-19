package com.example.server

object WebDashboardHtml {

    fun getHtml(serverIp: String, port: Int): String {
        return """
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="0" />
    <title>Panel Kontrol Digital Signage TV</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
    <style>
        :root {
            --bg-body: #0F172A;
            --bg-card: #1E293B;
            --bg-card-hover: #334155;
            --primary: #38BDF8;
            --primary-dark: #0284C7;
            --accent: #F59E0B;
            --success: #10B981;
            --danger: #EF4444;
            --telegram: #0088CC;
            --text-main: #F8FAFC;
            --text-muted: #94A3B8;
            --border: #334155;
            --radius: 14px;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Plus Jakarta Sans', sans-serif; -webkit-tap-highlight-color: transparent; }
        body { background: var(--bg-body); color: var(--text-main); min-height: 100vh; padding-bottom: 60px; }
        
        .header {
            background: linear-gradient(135deg, #1E293B, #0F172A);
            border-bottom: 1px solid var(--border);
            padding: 18px 20px;
            position: sticky; top: 0; z-index: 100;
            display: flex; align-items: center; justify-content: space-between;
        }
        .header-title { display: flex; align-items: center; gap: 12px; }
        .header-icon {
            width: 42px; height: 42px; border-radius: 10px;
            background: linear-gradient(135deg, #0284C7, #38BDF8);
            display: flex; align-items: center; justify-content: center;
            font-size: 22px; box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
        }
        .header h1 { font-size: 18px; font-weight: 800; color: #fff; }
        .header p { font-size: 12px; color: var(--text-muted); }
        .live-badge {
            display: inline-flex; align-items: center; gap: 6px;
            background: rgba(16, 185, 129, 0.15); color: #34D399;
            padding: 6px 12px; border-radius: 20px; font-size: 12px; font-weight: 600;
            border: 1px solid rgba(16, 185, 129, 0.3);
        }
        .live-dot { width: 8px; height: 8px; border-radius: 50%; background: #34D399; animation: pulse 1.5s infinite; }
        @keyframes pulse { 0%, 100% { opacity: 1; transform: scale(1); } 50% { opacity: 0.4; transform: scale(0.8); } }

        .nav-tabs {
            display: flex; overflow-x: auto; gap: 8px; padding: 12px 16px;
            background: #151F32; border-bottom: 1px solid var(--border);
            scrollbar-width: none;
        }
        .nav-tabs::-webkit-scrollbar { display: none; }
        .tab-btn {
            background: transparent; border: 1px solid transparent; color: var(--text-muted);
            padding: 9px 16px; border-radius: 10px; font-size: 13px; font-weight: 600;
            white-space: nowrap; cursor: pointer; transition: all 0.2s;
        }
        .tab-btn.active {
            background: var(--primary); color: #0F172A; font-weight: 700;
            box-shadow: 0 2px 10px rgba(56, 189, 248, 0.4);
        }

        .container { max-width: 860px; margin: 0 auto; padding: 16px; }
        .tab-content { display: none; }
        .tab-content.active { display: block; animation: fadeIn 0.25s ease; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }

        .card {
            background: var(--bg-card); border: 1px solid var(--border);
            border-radius: var(--radius); padding: 18px; margin-bottom: 16px;
        }
        .card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
        .card-title { font-size: 15px; font-weight: 700; color: #F1F5F9; display: flex; align-items: center; gap: 8px; }
        
        .form-group { margin-bottom: 14px; }
        .form-label { display: block; font-size: 13px; font-weight: 600; color: var(--text-muted); margin-bottom: 6px; }
        .form-control {
            width: 100%; background: #0F172A; border: 1px solid var(--border);
            color: #fff; padding: 11px 14px; border-radius: 10px; font-size: 14px;
            outline: none; transition: border-color 0.2s;
        }
        .form-control:focus { border-color: var(--primary); box-shadow: 0 0 0 2px rgba(56, 189, 248, 0.2); }
        textarea.form-control { resize: vertical; min-height: 80px; }
        
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
        @media (max-width: 600px) { .grid-2 { grid-template-columns: 1fr; } }

        .btn {
            display: inline-flex; align-items: center; justify-content: center; gap: 8px;
            padding: 11px 18px; border-radius: 10px; font-size: 13px; font-weight: 700;
            cursor: pointer; border: none; transition: all 0.2s;
        }
        .btn-primary { background: var(--primary); color: #0F172A; }
        .btn-primary:hover { background: #7DD3FC; }
        .btn-telegram { background: #0088CC; color: #fff; }
        .btn-telegram:hover { background: #0099E6; }
        .btn-accent { background: var(--accent); color: #000; }
        .btn-success { background: var(--success); color: #fff; }
        .btn-danger { background: var(--danger); color: #fff; }
        .btn-outline { background: transparent; border: 1px solid var(--border); color: #CBD5E1; }
        .btn-outline:hover { background: var(--bg-card-hover); }
        .btn-sm { padding: 6px 12px; font-size: 12px; border-radius: 8px; }
        .btn-block { width: 100%; }

        .preset-box {
            display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 16px;
        }
        .preset-card {
            background: #111C30; border: 2px solid var(--border); border-radius: 12px;
            padding: 14px; cursor: pointer; transition: all 0.2s; text-align: center;
        }
        .preset-card:hover { border-color: var(--primary); transform: translateY(-2px); }
        .preset-card h3 { font-size: 14px; margin: 6px 0 4px; }
        .preset-card p { font-size: 11px; color: var(--text-muted); }

        .item-row {
            background: #152238; border: 1px solid var(--border); border-radius: 12px;
            padding: 14px; margin-bottom: 10px; display: flex; align-items: center; justify-content: space-between; gap: 12px;
        }
        .item-row.editing {
            border-color: var(--primary);
            background: #1E3A5F;
        }
        .item-info { flex: 1; min-width: 0; }
        .item-badge {
            display: inline-block; font-size: 10px; font-weight: 700; padding: 2px 8px;
            border-radius: 6px; margin-bottom: 4px; background: rgba(56, 189, 248, 0.2); color: var(--primary);
        }
        .item-title { font-size: 14px; font-weight: 700; color: #fff; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .item-sub { font-size: 12px; color: var(--text-muted); margin-top: 2px; }
        .item-thumb { width: 64px; height: 48px; border-radius: 8px; object-fit: cover; background: #0F172A; }

        .theme-select-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(130px, 1fr)); gap: 10px; }
        .theme-opt {
            border: 2px solid var(--border); border-radius: 10px; padding: 10px; text-align: center;
            cursor: pointer; transition: all 0.2s; background: #111C30;
        }
        .theme-opt.selected { border-color: var(--primary); background: rgba(56, 189, 248, 0.1); }
        .theme-color-bar { height: 16px; border-radius: 4px; margin-bottom: 8px; }

        .toast {
            position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%);
            background: #10B981; color: #fff; padding: 12px 24px; border-radius: 30px;
            font-size: 13px; font-weight: 700; box-shadow: 0 6px 20px rgba(0,0,0,0.4);
            display: none; z-index: 999; animation: slideUp 0.2s ease;
        }
        @keyframes slideUp { from { transform: translate(-50%, 20px); opacity: 0; } to { transform: translate(-50%, 0); opacity: 1; } }

        .preview-img { width: 100%; max-height: 180px; object-fit: cover; border-radius: 10px; margin-top: 8px; display: none; }
        
        .code-box {
            background: #090E17; border: 1px solid #334155; border-radius: 8px;
            padding: 10px 14px; font-family: monospace; font-size: 12px; color: #38BDF8;
            margin: 6px 0; overflow-x: auto;
        }

        .modal-backdrop {
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(15, 23, 42, 0.8); backdrop-filter: blur(4px);
            display: none; align-items: center; justify-content: center; z-index: 1000;
        }
        .modal-content {
            background: var(--bg-card); border: 1px solid var(--border);
            border-radius: var(--radius); width: 90%; max-width: 500px;
            padding: 24px; box-shadow: 0 10px 25px rgba(0,0,0,0.5);
            max-height: 85vh; overflow-y: auto;
        }
        .filter-btn-group {
            display: flex; gap: 4px; background: #0F172A; padding: 4px;
            border-radius: 10px; border: 1px solid var(--border);
        }
        .filter-btn {
            background: transparent; border: none; color: var(--text-muted);
            padding: 6px 12px; border-radius: 8px; font-size: 12px; font-weight: 700;
            cursor: pointer; transition: all 0.2s;
        }
        .filter-btn.active {
            background: var(--primary); color: #0F172A;
        }
    </style>
</head>
<body>

    <header class="header">
        <div class="header-title">
            <div class="header-icon">📺</div>
            <div>
                <h1>Digital Signage TV Control</h1>
                <p>IP TV: <span id="tv-ip-display">$serverIp:$port</span></p>
            </div>
        </div>
        <div class="live-badge">
            <div class="live-dot"></div>
            <span>TERHUBUNG</span>
        </div>
    </header>

    <div class="nav-tabs">
        <button class="tab-btn active" onclick="switchTab('tab-general', this)">⚙️ Profil & Tema</button>
        <button class="tab-btn" onclick="switchTab('tab-attendance', this)">🏷️ Presensi & Pegawai</button>
        <button class="tab-btn" onclick="switchTab('tab-telegram', this)">🤖 Bot Telegram</button>
        <button class="tab-btn" onclick="switchTab('tab-slides', this)">🖼️ Slide & Video</button>
        <button class="tab-btn" onclick="switchTab('tab-ticker', this)">📜 Teks Berjalan</button>
        <button class="tab-btn" onclick="switchTab('tab-agenda', this)">📅 Agenda & Jadwal</button>
        <button class="tab-btn" onclick="switchTab('tab-notices', this)">📢 Pengumuman</button>
        <button class="tab-btn" onclick="switchTab('tab-alert', this)">🚨 Pesan Kilat TV</button>
    </div>


    <main class="container">

        <!-- TAB 1: GENERAL & THEME -->
        <section id="tab-general" class="tab-content active">
            
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">✨ Template Instan (1-Klik)</h2>
                </div>
                <div class="preset-box">
                    <div class="preset-card" onclick="applyPreset('school')">
                        <div style="font-size:28px">🏫</div>
                        <h3>Template Sekolah</h3>
                        <p>Format Kurikulum, Prestasi Siswa, ANBK, & Agenda Belajar</p>
                        <button class="btn btn-primary btn-sm" style="margin-top:8px">Terapkan</button>
                    </div>
                    <div class="preset-card" onclick="applyPreset('gov')">
                        <div style="font-size:28px">🏛️</div>
                        <h3>Template Pemerintahan</h3>
                        <p>Format PTSP, Zona Integritas, Adminduk & Jam Pelayanan</p>
                        <button class="btn btn-accent btn-sm" style="margin-top:8px">Terapkan</button>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">🏢 Identitas Instansi & Sekolah</h2>
                </div>
                <form id="form-settings" onsubmit="saveSettings(event)">
                    <div class="form-group">
                        <label class="form-label">Tipe Instansi</label>
                        <select id="cfg-orgType" class="form-control">
                            <option value="Sekolah">Sekolah / Kampus</option>
                            <option value="Pemerintahan">Kantor Pemerintahan / PTSP</option>
                            <option value="Umum">Umum / Rumah Sakit / Korporat</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Nama Lembaga / Sekolah / Kantor (Tampil di Header TV)</label>
                        <input type="text" id="cfg-orgName" class="form-control" placeholder="Contoh: SMA NEGERI 1 TELADAN" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Slogan / Visi Misi Lembaga</label>
                        <input type="text" id="cfg-orgSubtitle" class="form-control" placeholder="Contoh: Berakhlak Mulia, Unggul & Berdaya Saing Global">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Teks Badge Status / Akreditasi (Header TV)</label>
                        <input type="text" id="cfg-headerBannerText" class="form-control" placeholder="Contoh: TERAKREDITASI A • SEKOLAH PENGGERAK">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Pilihan Tema Warna Layar TV</label>
                        <div class="theme-select-grid" id="theme-options">
                            <div class="theme-opt" data-theme="school_modern" onclick="selectTheme('school_modern')">
                                <div class="theme-color-bar" style="background: linear-gradient(90deg, #059669, #06B6D4);"></div>
                                <b style="font-size:12px">Sekolah Modern</b>
                            </div>
                            <div class="theme-opt" data-theme="gov_nusantara" onclick="selectTheme('gov_nusantara')">
                                <div class="theme-color-bar" style="background: linear-gradient(90deg, #1E3A8A, #D97706);"></div>
                                <b style="font-size:12px">Gov Nusantara</b>
                            </div>
                            <div class="theme-opt" data-theme="cyber_blue" onclick="selectTheme('cyber_blue')">
                                <div class="theme-color-bar" style="background: linear-gradient(90deg, #00F0FF, #3B82F6);"></div>
                                <b style="font-size:12px">Cyber Tech</b>
                            </div>
                            <div class="theme-opt" data-theme="minimal_clean" onclick="selectTheme('minimal_clean')">
                                <div class="theme-color-bar" style="background: linear-gradient(90deg, #F8FAFC, #6366F1);"></div>
                                <b style="font-size:12px">Minimal Clean</b>
                            </div>
                            <div class="theme-opt" data-theme="sunset_warm" onclick="selectTheme('sunset_warm')">
                                <div class="theme-color-bar" style="background: linear-gradient(90deg, #E11D48, #F59E0B);"></div>
                                <b style="font-size:12px">Sunset Gold</b>
                            </div>
                            <div class="theme-opt" data-theme="high_density" onclick="selectTheme('high_density')">
                                <div class="theme-color-bar" style="background: linear-gradient(90deg, #6750A4, #B3261E);"></div>
                                <b style="font-size:12px">High Density (M3)</b>
                            </div>
                        </div>
                        <input type="hidden" id="cfg-themeId" value="school_modern">
                    </div>

                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">Durasi Pergantian Slide</label>
                            <select id="cfg-slideIntervalSeconds" class="form-control">
                                <option value="5">5 Detik</option>
                                <option value="7">7 Detik (Standar)</option>
                                <option value="10">10 Detik</option>
                                <option value="15">15 Detik</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Kecepatan Teks Berjalan</label>
                            <select id="cfg-runningTextSpeed" class="form-control">
                                <option value="0">Lambat (Mudah Dibaca)</option>
                                <option value="1">Sedang / Normal</option>
                                <option value="2">Cepat</option>
                            </select>
                        </div>
                    </div>

                    <!-- LOKASI GEOGRAFIS & WAKTU SHOLAT -->
                    <div style="margin-top:20px; padding-top:16px; border-top:1px dashed var(--border)">
                        <div style="display:flex;align-items:center;gap:10px;margin-bottom:14px">
                            <span style="font-size:24px">📍</span>
                            <div>
                                <h3 style="font-size:15px;font-weight:700;color:var(--text-main)">Lokasi TV & Jadwal Waktu Sholat</h3>
                                <p style="font-size:11px;color:var(--text-muted)">Pilih kota, geser titik peta, dan pilih zona waktu (WIB, WITA, WIT) agar jadwal sholat di layar TV akurat.</p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">🔍 Pilih Kota / Kabupaten Indonesia (Otomatis Isi Koordinat & Zona)</label>
                            <select id="cfg-citySelect" class="form-control" onchange="onCitySelected(this.value)">
                                <option value="">-- Memuat daftar kota... --</option>
                            </select>
                        </div>

                        <div class="grid-2">
                            <div class="form-group">
                                <label class="form-label">Nama Wilayah / Kota (Tampil di Layar TV)</label>
                                <input type="text" id="cfg-cityName" class="form-control" placeholder="Contoh: Jakarta Pusat, Bandung, Makassar" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Zona Waktu Indonesia</label>
                                <select id="cfg-timezone" class="form-control" onchange="onTimezoneSelectChanged()">
                                    <option value="7.0">WIB (GMT+7) - Waktu Indonesia Barat</option>
                                    <option value="8.0">WITA (GMT+8) - Waktu Indonesia Tengah</option>
                                    <option value="9.0">WIT (GMT+9) - Waktu Indonesia Timur</option>
                                </select>
                            </div>
                        </div>

                        <div class="grid-2">
                            <div class="form-group">
                                <label class="form-label">Latitude (Garis Lintang)</label>
                                <input type="number" step="any" id="cfg-latitude" class="form-control" placeholder="-6.2088" oninput="onCoordManualInput()">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Longitude (Garis Bujur)</label>
                                <input type="number" step="any" id="cfg-longitude" class="form-control" placeholder="106.8456" oninput="onCoordManualInput()">
                            </div>
                        </div>

                        <!-- Peta Interaktif Leaflet OSM -->
                        <div class="form-group">
                            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px">
                                <label class="form-label" style="margin-bottom:0">🗺️ Peta Interaktif (Geser Pin ke Lokasi Bangunan Anda)</label>
                                <span style="font-size:11px;color:var(--primary);font-weight:700">📍 Geser Pin / Klik Peta</span>
                            </div>
                            <div id="locationMap" style="height:270px;width:100%;border-radius:12px;border:1px solid var(--border);background:#1e293b;position:relative;z-index:10"></div>
                        </div>
                    </div>

                    <button type="submit" id="btn-save-settings" class="btn btn-primary btn-block">💾 Simpan Perubahan ke Layar TV</button>
                </form>
            </div>
        </section>

        <!-- TAB ATTENDANCE & EMPLOYEES -->
        <section id="tab-attendance" class="tab-content">
            <!-- 1. Attendance Configuration -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">⏰ Pengaturan Jam Presensi (RFID)</h2>
                </div>
                <form id="form-attendance-config" onsubmit="saveAttendanceConfig(event)">
                    <div class="form-group" style="display:flex;align-items:center;gap:10px;margin-bottom:14px">
                        <input type="checkbox" id="cfg-enableAttendance" style="width:18px;height:18px;accent-color:var(--primary)" checked>
                        <label for="cfg-enableAttendance" style="font-size:14px;font-weight:700;color:#fff;cursor:pointer">
                            Aktifkan Fitur Presensi RFID Pegawai di Layar TV
                        </label>
                    </div>
                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">Batas Jam Terlambat Masuk (HH:mm)</label>
                            <input type="text" id="cfg-checkInLateLimit" class="form-control" placeholder="07:30" value="07:30" required>
                            <span style="font-size:11px;color:var(--text-muted)">Scan sebelum jam ini = Hadir Tepat Waktu. Setelah jam ini = Terlambat.</span>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Jam Mulai Presensi Pulang (HH:mm)</label>
                            <input type="text" id="cfg-checkOutStart" class="form-control" placeholder="14:00" value="14:00" required>
                            <span style="font-size:11px;color:var(--text-muted)">Scan pada atau setelah jam ini dihitung Presensi Pulang.</span>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">💾 Simpan Jadwal Presensi</button>
                </form>
            </div>

            <!-- 2. Form Tambah / Edit Pegawai -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title" id="form-employee-title">👤 Tambah Pegawai / Karyawan Baru</h2>
                </div>
                <form id="form-employee" onsubmit="saveEmployee(event)">
                    <input type="hidden" id="emp-id" value="0">
                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">Nama Lengkap Pegawai</label>
                            <input type="text" id="emp-name" class="form-control" placeholder="Contoh: Drs. H. Bambang Sutrisno, M.Pd" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">UID Kartu RFID (Tap kartu ke alat / ketik manual)</label>
                            <input type="text" id="emp-rfidUid" class="form-control" placeholder="Contoh: 12345678 / A1B2C3D4" required>
                        </div>
                    </div>
                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">Jabatan / Bagian / Divisi</label>
                            <input type="text" id="emp-department" class="form-control" placeholder="Contoh: Dewan Guru / Tata Usaha / IT" required>
                        </div>
                        <div class="form-group">
                            <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:6px">
                                <label class="form-label" style="margin-bottom:0">Role / Kategori Pegawai</label>
                                <button type="button" class="btn btn-outline btn-sm" style="padding:2px 8px;font-size:11px" onclick="openRoleModal()">⚙️ Kelola Kategori</button>
                            </div>
                            <select id="emp-role" class="form-control">
                                <option value="Karyawan">Karyawan</option>
                                <option value="Guru">Guru</option>
                                <option value="Staf TU">Staf TU</option>
                                <option value="Pimpinan">Pimpinan</option>
                                <option value="Teknisi">Teknisi</option>
                                <option value="Keamanan">Keamanan</option>
                                <option value="Kebersihan">Kebersihan</option>
                            </select>
                        </div>
                    </div>
                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">No. HP / WhatsApp (Opsional)</label>
                            <input type="text" id="emp-phone" class="form-control" placeholder="Contoh: 081234567890">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Upload Foto Profil Pegawai</label>
                            <div style="display:flex;align-items:center;gap:12px;background:#0F172A;padding:10px;border-radius:10px;border:1px solid var(--border)">
                                <div id="emp-photo-box" style="width:52px;height:52px;border-radius:50%;background:#1E293B;border:2px solid var(--border);display:flex;align-items:center;justify-content:center;overflow:hidden;flex-shrink:0">
                                    <img id="emp-photo-preview" src="" style="width:100%;height:100%;object-fit:cover;display:none" alt="Foto">
                                    <span id="emp-photo-placeholder" style="font-size:22px">📷</span>
                                </div>
                                <div style="flex:1">
                                    <input type="file" id="emp-photo-file" accept="image/*" style="display:none" onchange="handleEmpPhotoPick(this)">
                                    <div style="display:flex;gap:6px">
                                        <button type="button" class="btn btn-outline btn-sm" onclick="document.getElementById('emp-photo-file').click()">📁 Pilih / Ambil Foto</button>
                                        <button type="button" id="btn-remove-emp-photo" class="btn btn-danger btn-sm" style="display:none" onclick="removeEmpPhoto()">✕ Hapus</button>
                                    </div>
                                    <span style="font-size:11px;color:var(--text-muted);display:block;margin-top:4px">Upload dari galeri / kamera HP</span>
                                </div>
                            </div>
                            <input type="hidden" id="emp-photoUrl" value="">
                        </div>
                    </div>
                    <button type="submit" id="btn-submit-employee" class="btn btn-success btn-block">➕ Simpan Data Pegawai</button>
                    <button type="button" id="btn-cancel-employee" class="btn btn-outline btn-block" style="display:none;margin-top:8px" onclick="cancelEditEmployee()">✕ Batal Edit</button>
                </form>
            </div>

            <!-- 3. Daftar Pegawai Terdaftar -->
            <div class="card">
                <div class="card-header" style="flex-wrap:wrap;gap:10px">
                    <h2 class="card-title">📋 Daftar Pegawai Terdaftar (<span id="emp-count">0</span>)</h2>
                    <div style="display:flex;flex-wrap:wrap;gap:6px">
                        <button type="button" class="btn btn-outline btn-sm" onclick="exportEmployeeCsv()">📥 Export CSV</button>
                        <button type="button" class="btn btn-outline btn-sm" onclick="document.getElementById('emp-csv-file').click()">📤 Import CSV</button>
                        <button type="button" class="btn btn-outline btn-sm" onclick="downloadEmployeeTemplate()">📄 Template CSV</button>
                        <input type="file" id="emp-csv-file" accept=".csv,.txt" style="display:none" onchange="handleEmployeeCsvUpload(this)">
                    </div>
                </div>
                <div id="employees-list-container">
                    <p style="color:var(--text-muted)">Memuat data pegawai...</p>
                </div>
            </div>

            <!-- 4. Rekapitulasi Presensi & Filter -->
            <div class="card">
                <div class="card-header" style="flex-wrap:wrap;gap:10px">
                    <h2 class="card-title">📊 Rekap Presensi & Log Absensi</h2>
                    <div style="display:flex;flex-wrap:wrap;gap:6px">
                        <button type="button" class="btn btn-accent btn-sm" onclick="exportAttendanceCsv()">📥 Export CSV Rekap</button>
                        <button type="button" class="btn btn-outline btn-sm" onclick="document.getElementById('att-csv-file').click()">📤 Import CSV</button>
                        <button type="button" class="btn btn-outline btn-sm" onclick="downloadAttendanceTemplate()">📄 Template Presensi</button>
                        <input type="file" id="att-csv-file" accept=".csv,.txt" style="display:none" onchange="handleAttendanceCsvUpload(this)">
                    </div>
                </div>

                <!-- Filter Controls: Harian, Bulanan, Semua -->
                <div style="background:#111C30;border:1px solid var(--border);border-radius:12px;padding:12px;margin-bottom:14px">
                    <div style="display:flex;flex-wrap:wrap;gap:10px;align-items:center;justify-content:space-between">
                        <div class="filter-btn-group">
                            <button type="button" id="att-mode-daily" class="filter-btn active" onclick="setAttendanceFilterMode('daily')">📅 Harian</button>
                            <button type="button" id="att-mode-monthly" class="filter-btn" onclick="setAttendanceFilterMode('monthly')">🗓️ Bulanan</button>
                            <button type="button" id="att-mode-all" class="filter-btn" onclick="setAttendanceFilterMode('all')">📋 Semua Riwayat</button>
                        </div>
                        <div style="display:flex;gap:8px;align-items:center;flex:1;max-width:340px">
                            <div id="att-daily-input-wrap" style="flex:1">
                                <input type="date" id="attendance-filter-date" class="form-control" onchange="loadAttendanceList()">
                            </div>
                            <div id="att-monthly-input-wrap" style="flex:1;display:none">
                                <input type="month" id="attendance-filter-month" class="form-control" onchange="loadAttendanceList()">
                            </div>
                            <button type="button" class="btn btn-primary btn-sm" onclick="loadAttendanceList()">🔄 Refresh</button>
                        </div>
                    </div>
                </div>

                <div id="attendance-summary-badges" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(120px,1fr));gap:10px;margin-bottom:14px">
                </div>

                <div id="attendance-list-container" style="overflow-x:auto">
                    <p style="color:var(--text-muted)">Memuat rekaman absensi...</p>
                </div>
            </div>
        </section>

        <!-- MODAL KELOLA KATEGORI ROLE PEGAWAI -->
        <div id="modal-roles" class="modal-backdrop">
            <div class="modal-content">
                <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:14px">
                    <h3 style="font-size:16px;font-weight:700;color:#fff">⚙️ Kelola Kategori / Role Pegawai</h3>
                    <button type="button" class="btn btn-outline btn-sm" onclick="closeRoleModal()">✕</button>
                </div>
                <p style="font-size:12px;color:var(--text-muted);margin-bottom:12px">
                    Tuliskan nama kategori/role pegawai dipisahkan tanda koma (,), atau gunakan input cepat di bawah:
                </p>
                <div class="form-group">
                    <label class="form-label">Daftar Kategori / Role (Pisahkan koma):</label>
                    <textarea id="roles-textarea" class="form-control" style="min-height:90px" placeholder="Karyawan, Guru, Staf TU, Pimpinan, Teknisi, Keamanan, Kebersihan"></textarea>
                </div>
                <div class="form-group">
                    <label class="form-label">Tambah Kategori Baru Cepat:</label>
                    <div style="display:flex;gap:8px">
                        <input type="text" id="role-quick-input" class="form-control" placeholder="Contoh: Satpam / Tenaga Ahli">
                        <button type="button" class="btn btn-primary" onclick="quickAddRole()">➕ Tambah</button>
                    </div>
                </div>
                <div style="display:flex;gap:8px;margin-top:16px">
                    <button type="button" class="btn btn-success btn-block" onclick="saveRolesList()">💾 Simpan Kategori Role</button>
                    <button type="button" class="btn btn-outline" onclick="closeRoleModal()">Batal</button>
                </div>
            </div>
        </div>

        <!-- TAB 2: TELEGRAM BOT INTEGRATION -->
        <section id="tab-telegram" class="tab-content">

            <div class="card" style="border-color: rgba(0, 136, 204, 0.4);">
                <div class="card-header">
                    <h2 class="card-title" style="color: #38BDF8">🤖 Konfigurasi Remote Kontrol Telegram Bot</h2>
                </div>
                <p style="font-size:13px; color:var(--text-muted); margin-bottom:14px; line-height: 1.5;">
                    Kontrol dan ubah nama, tema, slide poster, teks berjalan, agenda, pengumuman, serta pesan kilat TV secara real-time langsung dari aplikasi <b>Telegram</b> di HP Anda!
                </p>

                <form id="form-telegram" onsubmit="saveTelegramSettings(event)">
                    <div class="form-group">
                        <label style="display:flex; align-items:center; gap:10px; cursor:pointer; font-weight:700; color:#fff;">
                            <input type="checkbox" id="cfg-telegramBotEnabled" style="width:20px; height:20px; accent-color:#0088CC;">
                            <span>Aktifkan Remote Telegram Bot untuk TV Ini</span>
                        </label>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Token Bot Telegram (Dibuat via @BotFather)</label>
                        <input type="text" id="cfg-telegramBotToken" class="form-control" placeholder="Contoh: 1234567890:ABCdefGHIjklMNOpqrsTUVwxyz">
                    </div>

                    <div class="form-group">
                        <label class="form-label">User ID / Chat ID Admin yang Diizinkan (Whitelist)</label>
                        <input type="text" id="cfg-telegramBotAuthorizedUserIds" class="form-control" placeholder="Contoh: 123456789 (Kosongkan untuk otomatis daftarkan akun pengirim pertama)">
                        <small style="color:var(--text-muted); font-size:11px; margin-top:4px; display:block;">
                            Ketik <code>/myid</code> di bot @userinfobot pada Telegram untuk mengetahui ID akun Telegram Anda. Jika dikosongkan, akun pertama yang mengirim <code>/start</code> akan otomatis didaftarkan.
                        </small>
                    </div>

                    <div class="grid-2">
                        <button type="submit" class="btn btn-telegram btn-block">💾 Simpan Pengaturan Telegram</button>
                        <button type="button" class="btn btn-outline btn-block" onclick="testTelegramBot()">🚀 Kirim Pesan Tes ke Telegram</button>
                    </div>
                </form>
            </div>

            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">📖 Panduan Perintah Kontrol Telegram</h2>
                </div>
                <div style="font-size:13px; color:#CBD5E1; line-height:1.7;">
                    <p>Setelah bot aktif, kirimkan perintah berikut ke bot Telegram Anda:</p>
                    <div class="code-box">/status</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Melihat status TV, IP address, nama lembaga, dan jumlah slide aktif.</p>

                    <div class="code-box">/nama [Nama Lembaga Baru]</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Contoh: <code>/nama SMP NEGERI 2 SURABAYA</code></p>

                    <div class="code-box">/slogan [Slogan Baru]</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Contoh: <code>/slogan Berprestasi, Mandiri & Berwawasan Lingkungan</code></p>

                    <div class="code-box">/banner [Teks Banner Akreditasi]</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Contoh: <code>/banner TERAKREDITASI A • ADIWIYATA MANDIRI</code></p>

                    <div class="code-box">/tema [school|gov|cyber|minimal|sunset|m3]</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Mengubah skema warna tema layar TV seketika.</p>

                    <div class="code-box">/pesan [Pesan Siaga Darurat]</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Broadcast pesan kilat yang langsung menimpa seluruh layar TV. Hapus dengan <code>/hapuspesan</code>.</p>

                    <div class="code-box">Kirim Foto Langsung dari Galeri Telegram</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Foto otomatis didownload oleh TV dan dijadikan slide poster informasi!</p>

                    <div class="code-box">/text [Isi Teks Berjalan]</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Menambahkan kalimat teks berjalan di bilah bawah TV.</p>

                    <div class="code-box">/agenda [Waktu] | [Judul] | [Lokasi] | [PIC]</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Contoh: <code>/agenda 09:00 - 11:30 | Workshop ANBK | Lab Komputer | Pak Rudi</code></p>

                    <div class="code-box">/pengumuman [Judul] | [Isi Maklumat]</div>
                    <p style="margin-left:8px; font-size:12px; color:var(--text-muted);">Menambahkan maklumat ke papan pengumuman TV.</p>
                </div>
            </div>
        </section>

        <!-- TAB 3: SLIDES & VIDEOS -->
        <section id="tab-slides" class="tab-content">
            <div class="card" id="card-slide-form">
                <div class="card-header">
                    <h2 class="card-title" id="slide-form-title">➕ Tambah Slide Gambar atau Video Baru</h2>
                </div>
                <form id="form-add-slide" onsubmit="addSlide(event)">
                    <div class="form-group">
                        <label class="form-label">Tipe Media Slide</label>
                        <div class="grid-2">
                            <button type="button" id="btn-media-image" class="btn btn-primary btn-block" onclick="setMediaType('IMAGE')">🖼️ Gambar / Poster</button>
                            <button type="button" id="btn-media-video" class="btn btn-outline btn-block" onclick="setMediaType('VIDEO')">🎬 Video (MP4 / Web)</button>
                        </div>
                        <input type="hidden" id="slide-media-type" value="IMAGE">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Judul Slide / Video</label>
                        <input type="text" id="slide-title" class="form-control" placeholder="Contoh: Video Profil Sekolah / Prestasi Juara 1" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Keterangan / Deskripsi Lengkap</label>
                        <textarea id="slide-caption" class="form-control" placeholder="Tuliskan isi informasi atau ringkasan video yang akan tampil di TV"></textarea>
                    </div>
                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">Kategori / Badge</label>
                            <select id="slide-category" class="form-control">
                                <option value="INFORMASI">INFORMASI</option>
                                <option value="VIDEO PROFIL">VIDEO PROFIL</option>
                                <option value="PRESTASI">PRESTASI</option>
                                <option value="KEGIATAN">KEGIATAN</option>
                                <option value="PELAYANAN">PELAYANAN</option>
                                <option value="HIMBAUAN">HIMBAUAN</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Durasi Tampil di Layar TV</label>
                            <select id="slide-duration" class="form-control">
                                <option value="6">6 Detik</option>
                                <option value="8" selected>8 Detik</option>
                                <option value="12">12 Detik</option>
                                <option value="15">15 Detik</option>
                                <option value="20">20 Detik</option>
                                <option value="30">30 Detik</option>
                                <option value="60">60 Detik (1 Menit)</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label" id="media-input-label">Pilih File: Upload dari Galeri HP atau Link URL</label>
                        <input type="file" id="slide-file-input" class="form-control" accept="image/*,video/mp4,video/webm" onchange="handleMediaPick(this)">
                        <input type="text" id="slide-image-url" class="form-control" style="margin-top:8px" placeholder="Atau masukkan Link URL Video / Gambar (https://.../video.mp4 atau preset_school_1)">
                        <img id="slide-preview" class="preview-img" alt="Preview Gambar">
                        <video id="slide-preview-video" class="preview-img" controls autoplay loop muted></video>
                    </div>

                    <button type="submit" id="btn-submit-slide" class="btn btn-success btn-block">➕ Tambahkan ke Layar TV</button>
                    <button type="button" id="btn-cancel-slide" class="btn btn-outline btn-block" style="display:none;margin-top:8px" onclick="cancelEditSlide()">✕ Batal Edit</button>
                </form>
            </div>

            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">📋 Daftar Slide & Video yang Sedang Tampil di TV</h2>
                </div>
                <div id="slides-list-container">
                    <p style="color:var(--text-muted)">Memuat data slide...</p>
                </div>
            </div>
        </section>

        <!-- TAB 4: TICKER -->
        <section id="tab-ticker" class="tab-content">
            <div class="card" id="card-ticker-form">
                <div class="card-header">
                    <h2 class="card-title" id="ticker-form-title">➕ Tambah Teks Berjalan (Running Text)</h2>
                </div>
                <form id="form-add-ticker" onsubmit="addTicker(event)">
                    <div class="form-group">
                        <label class="form-label">Label Kategori</label>
                        <select id="ticker-badge" class="form-control">
                            <option value="PENGUMUMAN">📢 PENGUMUMAN</option>
                            <option value="INFO SEKOLAH">🏫 INFO SEKOLAH</option>
                            <option value="LAYANAN PUBLIK">🏛️ LAYANAN PUBLIK</option>
                            <option value="HIMBAUAN">📌 HIMBAUAN</option>
                            <option value="PRESTASI">⭐ PRESTASI</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Isi Teks Berjalan</label>
                        <textarea id="ticker-text" class="form-control" placeholder="Tulis teks berjalan yang akan bergeser di bagian bawah layar TV..." required></textarea>
                    </div>
                    <button type="submit" id="btn-submit-ticker" class="btn btn-success btn-block">➕ Tambahkan Running Text</button>
                    <button type="button" id="btn-cancel-ticker" class="btn btn-outline btn-block" style="display:none;margin-top:8px" onclick="cancelEditTicker()">✕ Batal Edit</button>
                </form>
            </div>

            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">📜 Daftar Teks Berjalan Aktif</h2>
                </div>
                <div id="tickers-list-container">
                    <p style="color:var(--text-muted)">Memuat data...</p>
                </div>
            </div>
        </section>

        <!-- TAB 5: AGENDA -->
        <section id="tab-agenda" class="tab-content">
            <div class="card" id="card-agenda-form">
                <div class="card-header">
                    <h2 class="card-title" id="agenda-form-title">➕ Tambah Agenda / Jadwal Kegiatan</h2>
                </div>
                <form id="form-add-agenda" onsubmit="addAgenda(event)">
                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">Waktu Kegiatan</label>
                            <input type="text" id="agenda-time" class="form-control" placeholder="Contoh: 08:30 - 11:00 WIB" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Status</label>
                            <select id="agenda-status" class="form-control">
                                <option value="UPCOMING">Akan Datang</option>
                                <option value="ONGOING">Sedang Berlangsung</option>
                                <option value="COMPLETED">Selesai</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Nama Kegiatan</label>
                        <input type="text" id="agenda-title" class="form-control" placeholder="Contoh: Rapat Koordinasi Kurikulum / Musrenbang" required>
                    </div>
                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">Ruangan / Tempat</label>
                            <input type="text" id="agenda-location" class="form-control" placeholder="Contoh: Aula Lt. 2 / Lab Komputer">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Penanggung Jawab / Pemateri</label>
                            <input type="text" id="agenda-pic" class="form-control" placeholder="Contoh: Kepala Sekolah / Tim Teknis">
                        </div>
                    </div>
                    <button type="submit" id="btn-submit-agenda" class="btn btn-success btn-block">➕ Tambah ke Agenda TV</button>
                    <button type="button" id="btn-cancel-agenda" class="btn btn-outline btn-block" style="display:none;margin-top:8px" onclick="cancelEditAgenda()">✕ Batal Edit</button>
                </form>
            </div>

            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">📅 Daftar Agenda Hari Ini</h2>
                </div>
                <div id="agendas-list-container">
                    <p style="color:var(--text-muted)">Memuat agenda...</p>
                </div>
            </div>
        </section>

        <!-- TAB 6: NOTICES -->
        <section id="tab-notices" class="tab-content">
            <div class="card" id="card-notice-form">
                <div class="card-header">
                    <h2 class="card-title" id="notice-form-title">➕ Buat Pengumuman / Bulletin Resmi</h2>
                </div>
                <form id="form-add-notice" onsubmit="addNotice(event)">
                    <div class="grid-2">
                        <div class="form-group">
                            <label class="form-label">Prioritas Pengumuman</label>
                            <select id="notice-priority" class="form-control">
                                <option value="IMPORTANT">⚠️ Penting (Kuning)</option>
                                <option value="URGENT">🚨 Sangat Penting (Merah)</option>
                                <option value="INFO">ℹ️ Informasi Biasa (Biru)</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Keterangan Tanggal</label>
                            <input type="text" id="notice-date" class="form-control" placeholder="Contoh: Hari Ini / 25 Ags 2026">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Judul Pengumuman</label>
                        <input type="text" id="notice-title" class="form-control" placeholder="Contoh: Jam Pelayanan Selama Ujian / Cuti Bersama" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Isi Pengumuman</label>
                        <textarea id="notice-content" class="form-control" placeholder="Tulis rincian pengumuman yang jelas dan mudah dipahami..." required></textarea>
                    </div>
                    <button type="submit" id="btn-submit-notice" class="btn btn-success btn-block">➕ Terbitkan Pengumuman</button>
                    <button type="button" id="btn-cancel-notice" class="btn btn-outline btn-block" style="display:none;margin-top:8px" onclick="cancelEditNotice()">✕ Batal Edit</button>
                </form>
            </div>

            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">📢 Papan Pengumuman di TV</h2>
                </div>
                <div id="notices-list-container">
                    <p style="color:var(--text-muted)">Memuat pengumuman...</p>
                </div>
            </div>
        </section>

        <!-- TAB 7: FLASH ALERT -->
        <section id="tab-alert" class="tab-content">
            <div class="card" style="border-color: rgba(239, 68, 68, 0.4);">
                <div class="card-header">
                    <h2 class="card-title" style="color: #F87171">🚨 Broadcast Pesan Kilat / Siaga TV</h2>
                </div>
                <p style="font-size:13px; color:var(--text-muted); margin-bottom:14px">
                    Kirimkan pesan mendesak atau pengumuman darurat yang langsung menimpa seluruh layar TV secara real-time.
                </p>
                <div class="form-group">
                    <label class="form-label">Isi Pesan Siaga / Kilat</label>
                    <textarea id="flash-message-input" class="form-control" placeholder="Contoh: PERHATIAN: Seluruh guru dan staf dimohon segera berkumpul di Ruang Rapat Utama sekarang juga."></textarea>
                </div>
                <div class="grid-2">
                    <button type="button" class="btn btn-danger" onclick="sendFlashAlert(true)">🚨 Kirim Pesan Kilat ke Layar TV</button>
                    <button type="button" class="btn btn-outline" onclick="sendFlashAlert(false)">❌ Hapus Pesan Kilat di TV</button>
                </div>
            </div>
        </section>

    </main>

    <div id="toast" class="toast">Data berhasil disimpan ke TV!</div>

    <script>
        let appData = null;
        let selectedBase64Media = "";
        let currentMediaType = "IMAGE";

        let editingSlideId = null;
        let editingTickerId = null;
        let editingAgendaId = null;
        let editingNoticeId = null;

        function switchTab(tabId, btn) {
            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));
            document.getElementById(tabId).classList.add('active');
            if (btn) btn.classList.add('active');
            if (tabId === 'tab-general' && locationMap) {
                setTimeout(() => { locationMap.invalidateSize(); }, 300);
            }
        }

        function showToast(msg = "Berhasil diperbarui di layar TV!") {
            const toast = document.getElementById('toast');
            toast.textContent = msg;
            toast.style.display = 'block';
            setTimeout(() => { toast.style.display = 'none'; }, 3200);
        }

        function selectTheme(themeId) {
            document.querySelectorAll('.theme-opt').forEach(el => el.classList.remove('selected'));
            const target = document.querySelector(`[data-theme="${'$'}{themeId}"]`);
            if (target) target.classList.add('selected');
            document.getElementById('cfg-themeId').value = themeId;
        }

        // ================= LOCATION MAP & INDONESIA CITIES =================
        let locationMap = null;
        let mapMarker = null;
        let citiesData = [];

        async function loadCitiesList() {
            try {
                const res = await fetch('/api/cities');
                if (res.ok) {
                    citiesData = await res.json();
                    const sel = document.getElementById('cfg-citySelect');
                    if (sel) {
                        sel.innerHTML = '<option value="">-- Pilih Kota / Kabupaten di Indonesia --</option>' + 
                            citiesData.map((c, i) => `<option value="${'$'}{i}">${'$'}{escapeHtml(c.displayName)}</option>`).join('');
                    }
                }
            } catch (e) {
                console.error("Gagal load cities:", e);
            }
        }

        function onCitySelected(cityIndex) {
            if (cityIndex === "" || !citiesData[cityIndex]) return;
            const city = citiesData[cityIndex];
            document.getElementById('cfg-cityName').value = city.name;
            document.getElementById('cfg-latitude').value = city.latitude.toFixed(6);
            document.getElementById('cfg-longitude').value = city.longitude.toFixed(6);
            document.getElementById('cfg-timezone').value = city.timezone.toFixed(1);
            updateMapPin(city.latitude, city.longitude, true);
        }

        function initLocationMap(lat, lng) {
            const mapContainer = document.getElementById('locationMap');
            if (!mapContainer) return;

            if (typeof L === 'undefined') {
                mapContainer.innerHTML = '<div style="padding:40px 15px;text-align:center;color:var(--text-muted)"><p style="font-weight:700">⚠️ Peta Memerlukan Akses Internet</p><p style="font-size:12px;margin-top:6px">Jika TV di jaringan offline, Anda tetap dapat memilih dari dropdown daftar kota atau mengetik koordinat manual di atas.</p></div>';
                return;
            }

            const initialLat = parseFloat(lat) || -6.2088;
            const initialLng = parseFloat(lng) || 106.8456;

            if (!locationMap) {
                try {
                    locationMap = L.map('locationMap').setView([initialLat, initialLng], 14);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        maxZoom: 19,
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(locationMap);

                    mapMarker = L.marker([initialLat, initialLng], {
                        draggable: true
                    }).addTo(locationMap);

                    mapMarker.bindPopup("<b>📍 Titik Lokasi TV</b><br>Geser pin tepat di atas gedung.").openPopup();

                    mapMarker.on('dragend', function() {
                        const pos = mapMarker.getLatLng();
                        updateCoordsFromMap(pos.lat, pos.lng);
                    });

                    locationMap.on('click', function(e) {
                        mapMarker.setLatLng(e.latlng);
                        updateCoordsFromMap(e.latlng.lat, e.latlng.lng);
                        locationMap.panTo(e.latlng);
                    });

                    setTimeout(() => { locationMap.invalidateSize(); }, 400);
                } catch(e) {
                    console.error("Leaflet init error:", e);
                }
            } else {
                locationMap.setView([initialLat, initialLng], 14);
                if (mapMarker) mapMarker.setLatLng([initialLat, initialLng]);
            }
        }

        function updateCoordsFromMap(lat, lng) {
            const fixedLat = parseFloat(lat).toFixed(6);
            const fixedLng = parseFloat(lng).toFixed(6);
            document.getElementById('cfg-latitude').value = fixedLat;
            document.getElementById('cfg-longitude').value = fixedLng;

            // Deteksi otomatis zona waktu WIB, WITA, WIT berdasarkan bujur koordinat
            const numLng = parseFloat(fixedLng);
            const numLat = parseFloat(fixedLat);
            const isWit = (numLng >= 127.0) || (numLng >= 125.0 && numLat <= 0.2);
            const isWita = !isWit && (
                numLng >= 115.0 ||
                (numLat <= -7.5 && numLng >= 114.4) ||
                (numLat >= -4.5 && numLat <= -1.0 && numLng >= 114.3)
            );

            let tz = 7.0;
            if (isWit) tz = 9.0;
            else if (isWita) tz = 8.0;

            document.getElementById('cfg-timezone').value = tz.toFixed(1);
        }

        function updateMapPin(lat, lng, doPan) {
            const numLat = parseFloat(lat);
            const numLng = parseFloat(lng);
            if (isNaN(numLat) || isNaN(numLng)) return;

            if (mapMarker) {
                mapMarker.setLatLng([numLat, numLng]);
            }
            if (locationMap && doPan) {
                locationMap.setView([numLat, numLng], 14);
                setTimeout(() => { locationMap.invalidateSize(); }, 200);
            }
        }

        function onCoordManualInput() {
            const lat = parseFloat(document.getElementById('cfg-latitude').value);
            const lng = parseFloat(document.getElementById('cfg-longitude').value);
            if (!isNaN(lat) && !isNaN(lng)) {
                updateMapPin(lat, lng, false);
                updateCoordsFromMap(lat, lng);
            }
        }

        function onTimezoneSelectChanged() {
            // User manually selected timezone, keep it
        }

        function setMediaType(type) {
            currentMediaType = type;
            document.getElementById('slide-media-type').value = type;
            const btnImg = document.getElementById('btn-media-image');
            const btnVid = document.getElementById('btn-media-video');
            const catSelect = document.getElementById('slide-category');
            const durationSelect = document.getElementById('slide-duration');
            const fileInput = document.getElementById('slide-file-input');
            const urlInput = document.getElementById('slide-image-url');
            const lbl = document.getElementById('media-input-label');

            if (type === 'VIDEO') {
                btnVid.className = 'btn btn-primary btn-block';
                btnImg.className = 'btn btn-outline btn-block';
                lbl.textContent = 'Pilih Video: Upload File MP4 dari HP atau Masukkan Link URL';
                urlInput.placeholder = 'Contoh URL Video: https://domain.com/video.mp4';
                if (!editingSlideId) {
                    catSelect.value = 'VIDEO PROFIL';
                    durationSelect.value = '15';
                }
            } else {
                btnImg.className = 'btn btn-primary btn-block';
                btnVid.className = 'btn btn-outline btn-block';
                lbl.textContent = 'Pilih Gambar: Upload dari Galeri HP atau Link URL';
                urlInput.placeholder = 'Atau tempel Link URL gambar / Preset (preset_school_1, preset_gov_1)';
                if (!editingSlideId) {
                    catSelect.value = 'INFORMASI';
                    durationSelect.value = '8';
                }
            }
        }

        function handleMediaPick(input) {
            if (input.files && input.files[0]) {
                const file = input.files[0];
                const isVideo = file.type.startsWith('video/') || file.name.endsWith('.mp4') || file.name.endsWith('.webm');
                const reader = new FileReader();
                reader.onload = function(e) {
                    selectedBase64Media = e.target.result;
                    const previewImg = document.getElementById('slide-preview');
                    const previewVid = document.getElementById('slide-preview-video');
                    if (isVideo) {
                        setMediaType('VIDEO');
                        previewVid.src = selectedBase64Media;
                        previewVid.style.display = 'block';
                        previewImg.style.display = 'none';
                    } else {
                        setMediaType('IMAGE');
                        previewImg.src = selectedBase64Media;
                        previewImg.style.display = 'block';
                        previewVid.style.display = 'none';
                    }
                };
                reader.readAsDataURL(file);
            }
        }

        async function loadAllData() {
            try {
                const res = await fetch('/api/all');
                if (res.ok) {
                    appData = await res.json();
                    renderSettings(appData.config);
                    renderSlides(appData.slides || []);
                    renderTickers(appData.tickers || []);
                    renderAgendas(appData.agendas || []);
                    renderNotices(appData.notices || []);
                    renderEmployees(appData.employees || []);
                    renderAttendanceList(appData.todayAttendances || []);
                }
            } catch(e) {
                console.error("Gagal load data:", e);
            }
        }

        function renderSettings(cfg) {
            if (!cfg) return;
            document.getElementById('cfg-orgType').value = cfg.orgType || 'Sekolah';
            document.getElementById('cfg-orgName').value = cfg.orgName || '';
            document.getElementById('cfg-orgSubtitle').value = cfg.orgSubtitle || '';
            document.getElementById('cfg-headerBannerText').value = cfg.headerBannerText || '';
            document.getElementById('cfg-slideIntervalSeconds').value = cfg.slideIntervalSeconds || 7;
            document.getElementById('cfg-runningTextSpeed').value = cfg.runningTextSpeed !== undefined ? cfg.runningTextSpeed : 1;
            selectTheme(cfg.themeId || 'school_modern');

            // Telegram settings
            document.getElementById('cfg-telegramBotEnabled').checked = !!cfg.telegramBotEnabled;
            document.getElementById('cfg-telegramBotToken').value = cfg.telegramBotToken || '';
            document.getElementById('cfg-telegramBotAuthorizedUserIds').value = cfg.telegramBotAuthorizedUserIds || '';

            // Attendance settings
            const enableAttEl = document.getElementById('cfg-enableAttendance');
            if (enableAttEl) enableAttEl.checked = cfg.enableAttendance !== false;
            const lateLimitEl = document.getElementById('cfg-checkInLateLimit');
            if (lateLimitEl) lateLimitEl.value = cfg.checkInLateLimit || '07:30';
            const checkOutStartEl = document.getElementById('cfg-checkOutStart');
            if (checkOutStartEl) checkOutStartEl.value = cfg.checkOutStart || '14:00';
            const filterDateEl = document.getElementById('attendance-filter-date');
            if (filterDateEl && !filterDateEl.value) {
                filterDateEl.value = new Date().toISOString().split('T')[0];
            }
            const filterMonthEl = document.getElementById('attendance-filter-month');
            if (filterMonthEl && !filterMonthEl.value) {
                filterMonthEl.value = new Date().toISOString().substring(0, 7);
            }
            if (cfg && cfg.employeeRoles) {
                renderRoleDropdown(cfg.employeeRoles);
            } else {
                renderRoleDropdown("");
            }

            // Location & Timezone settings
            if (document.getElementById('cfg-cityName')) document.getElementById('cfg-cityName').value = cfg.cityName || 'Jakarta Pusat';
            if (document.getElementById('cfg-latitude')) document.getElementById('cfg-latitude').value = (cfg.latitude !== undefined ? cfg.latitude : -6.2088).toFixed(6);
            if (document.getElementById('cfg-longitude')) document.getElementById('cfg-longitude').value = (cfg.longitude !== undefined ? cfg.longitude : 106.8456).toFixed(6);
            if (document.getElementById('cfg-timezone')) {
                const tzVal = (cfg.timezone !== undefined ? cfg.timezone : 7.0).toFixed(1);
                document.getElementById('cfg-timezone').value = tzVal;
            }
            initLocationMap(cfg.latitude || -6.2088, cfg.longitude || 106.8456);
        }


        function renderSlides(slides) {
            const container = document.getElementById('slides-list-container');
            if (!slides || slides.length === 0) {
                container.innerHTML = '<p style="color:var(--text-muted); padding:10px">Belum ada slide atau video.</p>';
                return;
            }
            container.innerHTML = slides.map(s => {
                const isVideo = s.mediaType === 'VIDEO' || (s.imageUrl && (s.imageUrl.endsWith('.mp4') || s.imageUrl.startsWith('/videos/')));
                let mediaHtml = '';
                if (isVideo) {
                    mediaHtml = `<div class="item-thumb" style="display:flex;align-items:center;justify-content:center;background:#1E293B;color:#EF4444;font-size:22px;border:1px solid rgba(239,68,68,0.4)">🎬</div>`;
                } else if (s.imageUrl && (s.imageUrl.startsWith('data:') || s.imageUrl.startsWith('http') || s.imageUrl.startsWith('/images/'))) {
                    mediaHtml = `<img src="${'$'}{s.imageUrl}" class="item-thumb" alt="thumb">`;
                } else {
                    mediaHtml = `<div class="item-thumb" style="display:flex;align-items:center;justify-content:center;background:#1E293B;color:#38BDF8;font-size:20px">🖼️</div>`;
                }
                const badgeColor = isVideo ? 'background:rgba(239,68,68,0.2);color:#F87171' : 'background:rgba(56,189,248,0.2);color:#38BDF8';
                const isEditing = editingSlideId === s.id;
                return `
                <div class="item-row ${'$'}{isEditing ? 'editing' : ''}">
                    ${'$'}{mediaHtml}
                    <div class="item-info">
                        <span class="item-badge" style="${'$'}{badgeColor}">${'$'}{isVideo ? '🎬 ' : ''}${'$'}{s.category || (isVideo ? 'VIDEO' : 'INFO')}</span>
                        <div class="item-title">${'$'}{escapeHtml(s.title)}</div>
                        <div class="item-sub">${'$'}{escapeHtml(s.caption || '')} (${'$'}{s.durationSeconds || (isVideo ? 15 : 7)}s)</div>
                    </div>
                    <div style="display:flex;gap:6px">
                        <button class="btn btn-primary btn-sm" onclick="editSlide(${'$'}{s.id})">✏️ Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteSlide(${'$'}{s.id})">Hapus</button>
                    </div>
                </div>`;
            }).join('');
        }

        function renderTickers(tickers) {
            const container = document.getElementById('tickers-list-container');
            if (!tickers || tickers.length === 0) {
                container.innerHTML = '<p style="color:var(--text-muted); padding:10px">Belum ada teks berjalan.</p>';
                return;
            }
            container.innerHTML = tickers.map(t => {
                const isEditing = editingTickerId === t.id;
                return `
                <div class="item-row ${'$'}{isEditing ? 'editing' : ''}">
                    <div class="item-info">
                        <span class="item-badge">${'$'}{t.badge || 'INFO'}</span>
                        <div class="item-title" style="white-space:normal">${'$'}{escapeHtml(t.text)}</div>
                    </div>
                    <div style="display:flex;gap:6px">
                        <button class="btn btn-primary btn-sm" onclick="editTicker(${'$'}{t.id})">✏️ Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteTicker(${'$'}{t.id})">Hapus</button>
                    </div>
                </div>
            `;
            }).join('');
        }

        function renderAgendas(agendas) {
            const container = document.getElementById('agendas-list-container');
            if (!agendas || agendas.length === 0) {
                container.innerHTML = '<p style="color:var(--text-muted); padding:10px">Belum ada agenda hari ini.</p>';
                return;
            }
            container.innerHTML = agendas.map(a => {
                const isEditing = editingAgendaId === a.id;
                return `
                <div class="item-row ${'$'}{isEditing ? 'editing' : ''}">
                    <div class="item-info">
                        <span class="item-badge" style="background:rgba(245,158,11,0.2);color:#F59E0B">${'$'}{a.time} • ${'$'}{a.status}</span>
                        <div class="item-title">${'$'}{escapeHtml(a.title)}</div>
                        <div class="item-sub">📍 ${'$'}{escapeHtml(a.location || '-')} ${'$'}{a.pic ? '• 👤 ' + escapeHtml(a.pic) : ''}</div>
                    </div>
                    <div style="display:flex;gap:6px">
                        <button class="btn btn-primary btn-sm" onclick="editAgenda(${'$'}{a.id})">✏️ Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteAgenda(${'$'}{a.id})">Hapus</button>
                    </div>
                </div>
            `;
            }).join('');
        }

        function renderNotices(notices) {
            const container = document.getElementById('notices-list-container');
            if (!notices || notices.length === 0) {
                container.innerHTML = '<p style="color:var(--text-muted); padding:10px">Belum ada pengumuman.</p>';
                return;
            }
            container.innerHTML = notices.map(n => {
                const isEditing = editingNoticeId === n.id;
                return `
                <div class="item-row ${'$'}{isEditing ? 'editing' : ''}">
                    <div class="item-info">
                        <span class="item-badge" style="background:${'$'}{n.priority==='URGENT'?'rgba(239,68,68,0.2)':n.priority==='IMPORTANT'?'rgba(245,158,11,0.2)':'rgba(56,189,248,0.2)'};color:${'$'}{n.priority==='URGENT'?'#F87171':n.priority==='IMPORTANT'?'#FBBF24':'#38BDF8'}">${'$'}{n.priority} • ${'$'}{n.dateText}</span>
                        <div class="item-title">${'$'}{escapeHtml(n.title)}</div>
                        <div class="item-sub" style="white-space:normal">${'$'}{escapeHtml(n.content)}</div>
                    </div>
                    <div style="display:flex;gap:6px">
                        <button class="btn btn-primary btn-sm" onclick="editNotice(${'$'}{n.id})">✏️ Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteNotice(${'$'}{n.id})">Hapus</button>
                    </div>
                </div>
            `;
            }).join('');
        }

        // ================= EDIT FUNCTIONS =================

        function editSlide(id) {
            if (!appData || !appData.slides) return;
            const slide = appData.slides.find(s => s.id === id);
            if (!slide) return;

            editingSlideId = id;
            setMediaType(slide.mediaType || 'IMAGE');
            document.getElementById('slide-title').value = slide.title || '';
            document.getElementById('slide-caption').value = slide.caption || '';
            document.getElementById('slide-category').value = slide.category || 'INFORMASI';
            document.getElementById('slide-duration').value = slide.durationSeconds || 8;
            document.getElementById('slide-image-url').value = slide.imageUrl || '';
            selectedBase64Media = "";

            const previewImg = document.getElementById('slide-preview');
            const previewVid = document.getElementById('slide-preview-video');
            if (slide.mediaType === 'VIDEO' || (slide.imageUrl && slide.imageUrl.endsWith('.mp4'))) {
                previewVid.src = slide.imageUrl;
                previewVid.style.display = 'block';
                previewImg.style.display = 'none';
            } else if (slide.imageUrl && (slide.imageUrl.startsWith('data:') || slide.imageUrl.startsWith('http') || slide.imageUrl.startsWith('/images/'))) {
                previewImg.src = slide.imageUrl;
                previewImg.style.display = 'block';
                previewVid.style.display = 'none';
            } else {
                previewImg.style.display = 'none';
                previewVid.style.display = 'none';
            }

            document.getElementById('slide-form-title').textContent = "✏️ Edit Slide / Video (#" + id + ")";
            document.getElementById('btn-submit-slide').textContent = "💾 Simpan Pembaruan Slide / Video";
            document.getElementById('btn-submit-slide').className = "btn btn-primary btn-block";
            document.getElementById('btn-cancel-slide').style.display = 'block';

            document.getElementById('card-slide-form').scrollIntoView({ behavior: 'smooth' });
            renderSlides(appData.slides);
        }

        function cancelEditSlide() {
            editingSlideId = null;
            document.getElementById('form-add-slide').reset();
            selectedBase64Media = "";
            document.getElementById('slide-preview').style.display = 'none';
            document.getElementById('slide-preview-video').style.display = 'none';
            setMediaType('IMAGE');
            document.getElementById('slide-form-title').textContent = "➕ Tambah Slide Gambar atau Video Baru";
            document.getElementById('btn-submit-slide').textContent = "➕ Tambahkan ke Layar TV";
            document.getElementById('btn-submit-slide').className = "btn btn-success btn-block";
            document.getElementById('btn-cancel-slide').style.display = 'none';
            if (appData) renderSlides(appData.slides);
        }

        function editTicker(id) {
            if (!appData || !appData.tickers) return;
            const ticker = appData.tickers.find(t => t.id === id);
            if (!ticker) return;

            editingTickerId = id;
            document.getElementById('ticker-badge').value = ticker.badge || 'PENGUMUMAN';
            document.getElementById('ticker-text').value = ticker.text || '';

            document.getElementById('ticker-form-title').textContent = "✏️ Edit Teks Berjalan (#" + id + ")";
            document.getElementById('btn-submit-ticker').textContent = "💾 Simpan Pembaruan Teks";
            document.getElementById('btn-submit-ticker').className = "btn btn-primary btn-block";
            document.getElementById('btn-cancel-ticker').style.display = 'block';

            document.getElementById('card-ticker-form').scrollIntoView({ behavior: 'smooth' });
            renderTickers(appData.tickers);
        }

        function cancelEditTicker() {
            editingTickerId = null;
            document.getElementById('form-add-ticker').reset();
            document.getElementById('ticker-form-title').textContent = "➕ Tambah Teks Berjalan (Running Text)";
            document.getElementById('btn-submit-ticker').textContent = "➕ Tambahkan Running Text";
            document.getElementById('btn-submit-ticker').className = "btn btn-success btn-block";
            document.getElementById('btn-cancel-ticker').style.display = 'none';
            if (appData) renderTickers(appData.tickers);
        }

        function editAgenda(id) {
            if (!appData || !appData.agendas) return;
            const agenda = appData.agendas.find(a => a.id === id);
            if (!agenda) return;

            editingAgendaId = id;
            document.getElementById('agenda-time').value = agenda.time || '';
            document.getElementById('agenda-status').value = agenda.status || 'UPCOMING';
            document.getElementById('agenda-title').value = agenda.title || '';
            document.getElementById('agenda-location').value = agenda.location || '';
            document.getElementById('agenda-pic').value = agenda.pic || '';

            document.getElementById('agenda-form-title').textContent = "✏️ Edit Agenda (#" + id + ")";
            document.getElementById('btn-submit-agenda').textContent = "💾 Simpan Pembaruan Agenda";
            document.getElementById('btn-submit-agenda').className = "btn btn-primary btn-block";
            document.getElementById('btn-cancel-agenda').style.display = 'block';

            document.getElementById('card-agenda-form').scrollIntoView({ behavior: 'smooth' });
            renderAgendas(appData.agendas);
        }

        function cancelEditAgenda() {
            editingAgendaId = null;
            document.getElementById('form-add-agenda').reset();
            document.getElementById('agenda-form-title').textContent = "➕ Tambah Agenda / Jadwal Kegiatan";
            document.getElementById('btn-submit-agenda').textContent = "➕ Tambah ke Agenda TV";
            document.getElementById('btn-submit-agenda').className = "btn btn-success btn-block";
            document.getElementById('btn-cancel-agenda').style.display = 'none';
            if (appData) renderAgendas(appData.agendas);
        }

        function editNotice(id) {
            if (!appData || !appData.notices) return;
            const notice = appData.notices.find(n => n.id === id);
            if (!notice) return;

            editingNoticeId = id;
            document.getElementById('notice-priority').value = notice.priority || 'INFO';
            document.getElementById('notice-date').value = notice.dateText || '';
            document.getElementById('notice-title').value = notice.title || '';
            document.getElementById('notice-content').value = notice.content || '';

            document.getElementById('notice-form-title').textContent = "✏️ Edit Pengumuman (#" + id + ")";
            document.getElementById('btn-submit-notice').textContent = "💾 Simpan Pembaruan Pengumuman";
            document.getElementById('btn-submit-notice').className = "btn btn-primary btn-block";
            document.getElementById('btn-cancel-notice').style.display = 'block';

            document.getElementById('card-notice-form').scrollIntoView({ behavior: 'smooth' });
            renderNotices(appData.notices);
        }

        function cancelEditNotice() {
            editingNoticeId = null;
            document.getElementById('form-add-notice').reset();
            document.getElementById('notice-form-title').textContent = "➕ Buat Pengumuman / Bulletin Resmi";
            document.getElementById('btn-submit-notice').textContent = "➕ Terbitkan Pengumuman";
            document.getElementById('btn-submit-notice').className = "btn btn-success btn-block";
            document.getElementById('btn-cancel-notice').style.display = 'none';
            if (appData) renderNotices(appData.notices);
        }

        // ================= SAVE & SUBMIT HANDLERS =================

        async function saveSettings(e) {
            if (e) e.preventDefault();
            const btn = document.getElementById('btn-save-settings');
            const originalText = btn.textContent;
            btn.textContent = "⏳ Menyimpan ke TV...";
            btn.disabled = true;

            try {
                const orgNameVal = document.getElementById('cfg-orgName').value.trim();
                if (!orgNameVal) {
                    alert("Nama Lembaga / Sekolah tidak boleh kosong!");
                    btn.textContent = originalText;
                    btn.disabled = false;
                    return;
                }

                const payload = {
                    orgType: document.getElementById('cfg-orgType').value,
                    orgName: orgNameVal,
                    orgSubtitle: document.getElementById('cfg-orgSubtitle').value.trim(),
                    headerBannerText: document.getElementById('cfg-headerBannerText').value.trim(),
                    themeId: document.getElementById('cfg-themeId').value,
                    slideIntervalSeconds: parseInt(document.getElementById('cfg-slideIntervalSeconds').value) || 7,
                    runningTextSpeed: parseInt(document.getElementById('cfg-runningTextSpeed').value) || 1,
                    cityName: (document.getElementById('cfg-cityName').value || '').trim() || 'Jakarta Pusat',
                    latitude: parseFloat(document.getElementById('cfg-latitude').value) || -6.2088,
                    longitude: parseFloat(document.getElementById('cfg-longitude').value) || 106.8456,
                    timezone: parseFloat(document.getElementById('cfg-timezone').value) || 7.0
                };

                const res = await fetch('/api/save-config', {
                    method: 'POST',
                    headers: { 
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                if (res.ok) {
                    const data = await res.json().catch(() => ({}));
                    showToast(data.message || "✅ Pengaturan Nama & Profil berhasil diupdate di TV!");
                    await loadAllData();
                } else {
                    const errText = await res.text().catch(() => "");
                    alert("Gagal menyimpan ke TV. Status server: " + res.status + "\n" + errText);
                }
            } catch (err) {
                alert("Koneksi ke TV terputus / gagal: " + err.message);
            } finally {
                btn.textContent = originalText;
                btn.disabled = false;
            }
        }

        async function saveTelegramSettings(e) {
            if (e) e.preventDefault();
            const btn = document.getElementById('btn-save-telegram');
            if (btn) btn.disabled = true;

            const payload = {
                telegramBotEnabled: document.getElementById('cfg-telegramBotEnabled').checked,
                telegramBotToken: document.getElementById('cfg-telegramBotToken').value.trim(),
                telegramBotAuthorizedUserIds: document.getElementById('cfg-telegramBotAuthorizedUserIds').value.trim()
            };

            try {
                const res = await fetch('/api/save-config', {
                    method: 'POST',
                    headers: { 
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });
                if (res.ok) {
                    showToast("✅ Konfigurasi Bot Telegram berhasil disimpan!");
                    await loadAllData();
                } else {
                    alert("Gagal menyimpan ke TV. Status: " + res.status);
                }
            } catch (err) {
                alert("Koneksi gagal: " + err.message);
            } finally {
                if (btn) btn.disabled = false;
            }
        }

        async function testTelegramBot() {
            const token = document.getElementById('cfg-telegramBotToken').value.trim();
            const chatIdStr = document.getElementById('cfg-telegramBotAuthorizedUserIds').value.trim();
            const chatId = parseInt(chatIdStr.split(/[\s,]+/)[0]) || 0;

            if (!token) {
                alert("Masukkan Token Bot Telegram terlebih dahulu.");
                return;
            }

            try {
                showToast("⏳ Mengirim pesan tes ke Telegram...");
                const res = await fetch('/api/telegram/test', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ token: token, chatId: chatId })
                });
                const data = await res.json();
                if (data.success) {
                    showToast("✅ " + data.message);
                } else {
                    alert("⚠️ " + (data.message || "Gagal mengirim pesan tes."));
                }
            } catch (err) {
                alert("Error uji koneksi Telegram: " + err.message);
            }
        }

        async function applyPreset(type) {
            if (!confirm(`Terapkan template instan ${'$'}{type === 'school' ? 'Sekolah' : 'Pemerintahan'}? Semua data slide, agenda, & teks berjalan akan disesuaikan.`)) return;
            const res = await fetch('/api/preset', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ type: type })
            });
            if (res.ok) {
                showToast(`Template ${'$'}{type} berhasil diterapkan!`);
                loadAllData();
            }
        }

        async function addSlide(e) {
            e.preventDefault();
            let mediaUrl = selectedBase64Media || document.getElementById('slide-image-url').value;
            if (!mediaUrl && !editingSlideId) {
                mediaUrl = currentMediaType === 'VIDEO' ? '' : 'preset_school_1';
            }
            if (!mediaUrl && !editingSlideId) {
                alert("Pilih file video/gambar atau masukkan link URL terlebih dahulu.");
                return;
            }
            const payload = {
                title: document.getElementById('slide-title').value,
                caption: document.getElementById('slide-caption').value,
                category: document.getElementById('slide-category').value,
                mediaType: currentMediaType,
                durationSeconds: parseInt(document.getElementById('slide-duration').value) || (currentMediaType === 'VIDEO' ? 15 : 8),
                imageUrl: mediaUrl
            };

            const endpoint = editingSlideId ? '/api/slide/update' : '/api/slide/add';
            if (editingSlideId) {
                payload.id = editingSlideId;
            }

            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                showToast(editingSlideId ? "Slide/Video berhasil diperbarui di TV!" : "Slide/Video berhasil ditambahkan ke TV!");
                cancelEditSlide();
                loadAllData();
            }
        }

        async function deleteSlide(id) {
            if (!confirm("Hapus slide ini dari layar TV?")) return;
            const res = await fetch('/api/slide/delete', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id })
            });
            if (res.ok) { 
                if (editingSlideId === id) cancelEditSlide();
                showToast("Slide dihapus"); 
                loadAllData(); 
            }
        }

        async function addTicker(e) {
            e.preventDefault();
            const payload = {
                badge: document.getElementById('ticker-badge').value,
                text: document.getElementById('ticker-text').value
            };
            const endpoint = editingTickerId ? '/api/ticker/update' : '/api/ticker/add';
            if (editingTickerId) {
                payload.id = editingTickerId;
            }
            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                showToast(editingTickerId ? "Running text diperbarui!" : "Running text ditambahkan!");
                cancelEditTicker();
                loadAllData();
            }
        }

        async function deleteTicker(id) {
            const res = await fetch('/api/ticker/delete', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id })
            });
            if (res.ok) { 
                if (editingTickerId === id) cancelEditTicker();
                showToast("Running text dihapus"); 
                loadAllData(); 
            }
        }

        async function addAgenda(e) {
            e.preventDefault();
            const payload = {
                time: document.getElementById('agenda-time').value,
                title: document.getElementById('agenda-title').value,
                location: document.getElementById('agenda-location').value,
                pic: document.getElementById('agenda-pic').value,
                status: document.getElementById('agenda-status').value
            };
            const endpoint = editingAgendaId ? '/api/agenda/update' : '/api/agenda/add';
            if (editingAgendaId) {
                payload.id = editingAgendaId;
            }
            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                showToast(editingAgendaId ? "Agenda diperbarui!" : "Agenda ditambahkan!");
                cancelEditAgenda();
                loadAllData();
            }
        }

        async function deleteAgenda(id) {
            const res = await fetch('/api/agenda/delete', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id })
            });
            if (res.ok) { 
                if (editingAgendaId === id) cancelEditAgenda();
                showToast("Agenda dihapus"); 
                loadAllData(); 
            }
        }

        async function addNotice(e) {
            e.preventDefault();
            const payload = {
                title: document.getElementById('notice-title').value,
                content: document.getElementById('notice-content').value,
                priority: document.getElementById('notice-priority').value,
                dateText: document.getElementById('notice-date').value || 'Hari Ini'
            };
            const endpoint = editingNoticeId ? '/api/notice/update' : '/api/notice/add';
            if (editingNoticeId) {
                payload.id = editingNoticeId;
            }
            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                showToast(editingNoticeId ? "Pengumuman diperbarui!" : "Pengumuman ditambahkan!");
                cancelEditNotice();
                loadAllData();
            }
        }

        async function deleteNotice(id) {
            const res = await fetch('/api/notice/delete', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id })
            });
            if (res.ok) { 
                if (editingNoticeId === id) cancelEditNotice();
                showToast("Pengumuman dihapus"); 
                loadAllData(); 
            }
        }

        async function sendFlashAlert(isSend) {
            const msg = isSend ? document.getElementById('flash-message-input').value : "";
            if (isSend && !msg.trim()) { alert("Masukkan pesan kilat terlebih dahulu."); return; }
            const res = await fetch('/api/flash-alert', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: msg })
            });
            if (res.ok) {
                showToast(isSend ? "🚨 Pesan Kilat telah dikirim ke TV!" : "Pesan kilat TV dinonaktifkan.");
            }
        }

        let editingEmployeeId = null;
        let currentAttendanceMode = 'daily';

        function renderRoleDropdown(rolesStr) {
            const defaultRoles = "Karyawan, Guru, Staf TU, Pimpinan, Teknisi, Keamanan, Kebersihan";
            const currentStr = rolesStr || (appData && appData.config && appData.config.employeeRoles) || defaultRoles;
            const roles = currentStr.split(/[,;\n]+/).map(r => r.trim()).filter(r => r.length > 0);
            const selectEl = document.getElementById('emp-role');
            if (!selectEl) return;
            const currentVal = selectEl.value;
            selectEl.innerHTML = roles.map(r => `<option value="${'$'}{escapeHtml(r)}">${'$'}{escapeHtml(r)}</option>`).join('');
            if (currentVal && roles.includes(currentVal)) {
                selectEl.value = currentVal;
            } else if (roles.length > 0) {
                selectEl.value = roles[0];
            }
        }

        function openRoleModal() {
            const modal = document.getElementById('modal-roles');
            if (!modal) return;
            const defaultRoles = "Karyawan, Guru, Staf TU, Pimpinan, Teknisi, Keamanan, Kebersihan";
            const currentStr = (appData && appData.config && appData.config.employeeRoles) || defaultRoles;
            document.getElementById('roles-textarea').value = currentStr;
            document.getElementById('role-quick-input').value = '';
            modal.style.display = 'flex';
        }

        function closeRoleModal() {
            const modal = document.getElementById('modal-roles');
            if (modal) modal.style.display = 'none';
        }

        function quickAddRole() {
            const input = document.getElementById('role-quick-input');
            const val = input.value.trim();
            if (!val) return;
            const textarea = document.getElementById('roles-textarea');
            const current = textarea.value.trim();
            if (current) {
                textarea.value = current + ', ' + val;
            } else {
                textarea.value = val;
            }
            input.value = '';
        }

        async function saveRolesList() {
            const textarea = document.getElementById('roles-textarea');
            const roles = textarea.value.trim();
            if (!roles) { alert("Daftar role tidak boleh kosong!"); return; }
            try {
                const res = await fetch('/api/employee/roles/save', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ roles: roles })
                });
                const data = await res.json();
                if (res.ok && data.success) {
                    showToast("✅ Kategori role berhasil diperbarui!");
                    if (appData && appData.config) appData.config.employeeRoles = roles;
                    renderRoleDropdown(roles);
                    closeRoleModal();
                } else {
                    alert("Gagal menyimpan role: " + (data.message || "Error server"));
                }
            } catch (err) {
                alert("Koneksi gagal: " + err.message);
            }
        }

        function handleEmpPhotoPick(input) {
            if (input.files && input.files[0]) {
                const file = input.files[0];
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('emp-photoUrl').value = e.target.result;
                    const preview = document.getElementById('emp-photo-preview');
                    const placeholder = document.getElementById('emp-photo-placeholder');
                    const btnRemove = document.getElementById('btn-remove-emp-photo');
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                    placeholder.style.display = 'none';
                    btnRemove.style.display = 'inline-flex';
                };
                reader.readAsDataURL(file);
            }
        }

        function removeEmpPhoto() {
            document.getElementById('emp-photoUrl').value = '';
            document.getElementById('emp-photo-file').value = '';
            const preview = document.getElementById('emp-photo-preview');
            const placeholder = document.getElementById('emp-photo-placeholder');
            const btnRemove = document.getElementById('btn-remove-emp-photo');
            preview.src = '';
            preview.style.display = 'none';
            placeholder.style.display = 'block';
            btnRemove.style.display = 'none';
        }

        function renderEmployees(employees) {
            const container = document.getElementById('employees-list-container');
            const countEl = document.getElementById('emp-count');
            if (countEl) countEl.textContent = employees ? employees.length : 0;
            if (!employees || employees.length === 0) {
                container.innerHTML = '<p style="color:var(--text-muted); padding:10px">Belum ada pegawai terdaftar. Tambahkan atau import CSV di atas.</p>';
                return;
            }
            container.innerHTML = employees.map(emp => {
                const isEditing = editingEmployeeId === emp.id;
                const initials = (emp.name || 'K').trim().substring(0, 2).toUpperCase();
                let photoHtml = '';
                if (emp.photoUrl && (emp.photoUrl.startsWith('/images/') || emp.photoUrl.startsWith('data:') || emp.photoUrl.startsWith('http'))) {
                    photoHtml = `<img src="${'$'}{emp.photoUrl}" style="width:42px;height:42px;border-radius:50%;object-fit:cover;border:2px solid rgba(56,189,248,0.4)" alt="Foto">`;
                } else {
                    photoHtml = `<div style="width:42px;height:42px;border-radius:50%;background:#0284C7;display:flex;align-items:center;justify-content:center;font-weight:700;color:#fff;font-size:14px">${'$'}{escapeHtml(initials)}</div>`;
                }

                return `
                <div class="item-row ${'$'}{isEditing ? 'editing' : ''}">
                    ${'$'}{photoHtml}
                    <div class="item-info">
                        <span class="item-badge">${'$'}{escapeHtml(emp.role || 'Karyawan')}</span>
                        <span class="item-badge" style="background:rgba(245,158,11,0.2);color:#FBBF24">UID: ${'$'}{escapeHtml(emp.rfidUid)}</span>
                        <div class="item-title">${'$'}{escapeHtml(emp.name)}</div>
                        <div class="item-sub">${'$'}{escapeHtml(emp.department || 'Umum')}${'$'}{emp.phone ? ' • 📞 ' + escapeHtml(emp.phone) : ''}</div>
                    </div>
                    <div style="display:flex;gap:6px">
                        <button class="btn btn-outline btn-sm" onclick="editEmployee(${'$'}{emp.id})">✏️ Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteEmployee(${'$'}{emp.id})">🗑️</button>
                    </div>
                </div>`;
            }).join('');
        }

        function editEmployee(id) {
            const emp = (appData.employees || []).find(e => e.id === id);
            if (!emp) return;
            editingEmployeeId = id;
            document.getElementById('emp-id').value = emp.id;
            document.getElementById('emp-name').value = emp.name;
            document.getElementById('emp-rfidUid').value = emp.rfidUid;
            document.getElementById('emp-department').value = emp.department;
            document.getElementById('emp-role').value = emp.role || 'Karyawan';
            document.getElementById('emp-phone').value = emp.phone || '';
            document.getElementById('emp-photoUrl').value = emp.photoUrl || '';

            const preview = document.getElementById('emp-photo-preview');
            const placeholder = document.getElementById('emp-photo-placeholder');
            const btnRemove = document.getElementById('btn-remove-emp-photo');
            if (emp.photoUrl && (emp.photoUrl.startsWith('/images/') || emp.photoUrl.startsWith('data:') || emp.photoUrl.startsWith('http'))) {
                preview.src = emp.photoUrl;
                preview.style.display = 'block';
                placeholder.style.display = 'none';
                btnRemove.style.display = 'inline-flex';
            } else {
                preview.src = '';
                preview.style.display = 'none';
                placeholder.style.display = 'block';
                btnRemove.style.display = 'none';
            }

            document.getElementById('form-employee-title').textContent = '✏️ Edit Pegawai: ' + emp.name;
            document.getElementById('btn-submit-employee').textContent = '💾 Simpan Perubahan Pegawai';
            document.getElementById('btn-cancel-employee').style.display = 'block';
            document.getElementById('form-employee').scrollIntoView({ behavior: 'smooth' });
            renderEmployees(appData.employees || []);
        }

        function cancelEditEmployee() {
            editingEmployeeId = null;
            document.getElementById('emp-id').value = 0;
            document.getElementById('emp-name').value = '';
            document.getElementById('emp-rfidUid').value = '';
            document.getElementById('emp-department').value = '';
            document.getElementById('emp-phone').value = '';
            removeEmpPhoto();

            document.getElementById('form-employee-title').textContent = '👤 Tambah Pegawai / Karyawan Baru';
            document.getElementById('btn-submit-employee').textContent = '➕ Simpan Data Pegawai';
            document.getElementById('btn-cancel-employee').style.display = 'none';
            renderEmployees(appData.employees || []);
        }

        async function saveEmployee(e) {
            e.preventDefault();
            const payload = {
                id: parseInt(document.getElementById('emp-id').value) || 0,
                name: document.getElementById('emp-name').value,
                rfidUid: document.getElementById('emp-rfidUid').value,
                department: document.getElementById('emp-department').value,
                role: document.getElementById('emp-role').value,
                phone: document.getElementById('emp-phone').value,
                photoUrl: document.getElementById('emp-photoUrl').value,
                isActive: true
            };
            const res = await fetch('/api/employee/save', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                showToast(payload.id > 0 ? "Data pegawai diperbarui!" : "Pegawai berhasil ditambahkan!");
                cancelEditEmployee();
                loadAllData();
            } else {
                alert("Gagal menyimpan data pegawai.");
            }
        }

        async function deleteEmployee(id) {
            if (!confirm("Yakin ingin menghapus data pegawai ini?")) return;
            const res = await fetch('/api/employee/delete', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id })
            });
            if (res.ok) {
                if (editingEmployeeId === id) cancelEditEmployee();
                showToast("Pegawai dihapus");
                loadAllData();
            }
        }

        function exportEmployeeCsv() {
            window.location.href = '/api/employee/export-csv';
        }

        function downloadEmployeeTemplate() {
            window.location.href = '/api/employee/template-csv';
        }

        function handleEmployeeCsvUpload(input) {
            if (!input.files || !input.files[0]) return;
            const file = input.files[0];
            const reader = new FileReader();
            reader.onload = async function(e) {
                try {
                    const res = await fetch('/api/employee/import-csv', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ csvData: e.target.result })
                    });
                    const data = await res.json();
                    if (res.ok && data.success) {
                        showToast(data.message || "Data pegawai berhasil diimpor!");
                        loadAllData();
                    } else {
                        alert("Gagal impor CSV: " + (data.message || "Format data tidak sesuai"));
                    }
                } catch (err) {
                    alert("Error saat impor CSV: " + err.message);
                }
                input.value = "";
            };
            reader.readAsText(file, "UTF-8");
        }

        async function saveAttendanceConfig(e) {
            e.preventDefault();
            const payload = {
                enableAttendance: document.getElementById('cfg-enableAttendance').checked,
                checkInLateLimit: document.getElementById('cfg-checkInLateLimit').value,
                checkOutStart: document.getElementById('cfg-checkOutStart').value
            };
            const res = await fetch('/api/attendance/config', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                showToast("Jadwal presensi TV diperbarui!");
                loadAllData();
            }
        }

        function setAttendanceFilterMode(mode) {
            currentAttendanceMode = mode;
            document.getElementById('att-mode-daily').className = (mode === 'daily') ? 'filter-btn active' : 'filter-btn';
            document.getElementById('att-mode-monthly').className = (mode === 'monthly') ? 'filter-btn active' : 'filter-btn';
            document.getElementById('att-mode-all').className = (mode === 'all') ? 'filter-btn active' : 'filter-btn';

            document.getElementById('att-daily-input-wrap').style.display = (mode === 'daily') ? 'block' : 'none';
            document.getElementById('att-monthly-input-wrap').style.display = (mode === 'monthly') ? 'block' : 'none';

            loadAttendanceList();
        }

        async function loadAttendanceList() {
            let url = '/api/attendance/list';
            if (currentAttendanceMode === 'monthly') {
                const month = document.getElementById('attendance-filter-month').value || new Date().toISOString().substring(0, 7);
                url += '?mode=month&month=' + encodeURIComponent(month);
            } else if (currentAttendanceMode === 'all') {
                url += '?mode=all';
            } else {
                const date = document.getElementById('attendance-filter-date').value || new Date().toISOString().split('T')[0];
                url += '?mode=daily&date=' + encodeURIComponent(date);
            }

            try {
                const res = await fetch(url);
                if (res.ok) {
                    const data = await res.json();
                    renderAttendanceList(data.records || []);
                }
            } catch(e) {
                console.error("Gagal load attendance:", e);
            }
        }

        function renderAttendanceList(records) {
            const container = document.getElementById('attendance-list-container');
            const summaryContainer = document.getElementById('attendance-summary-badges');

            const total = records ? records.length : 0;
            const onTime = records ? records.filter(r => r.statusIn && r.statusIn.includes('TEPAT WAKTU')).length : 0;
            const late = records ? records.filter(r => r.statusIn && r.statusIn.includes('TERLAMBAT')).length : 0;
            const checkOut = records ? records.filter(r => r.timeOut && r.timeOut.trim() !== '' && r.timeOut.trim() !== '-').length : 0;

            if (summaryContainer) {
                summaryContainer.innerHTML = `
                    <div style="background:#111C30;border:1px solid var(--border);border-radius:10px;padding:10px;text-align:center">
                        <div style="font-size:18px;font-weight:800;color:#38BDF8">${'$'}{total}</div>
                        <div style="font-size:11px;color:var(--text-muted)">Total Presensi</div>
                    </div>
                    <div style="background:#111C30;border:1px solid rgba(16,185,129,0.3);border-radius:10px;padding:10px;text-align:center">
                        <div style="font-size:18px;font-weight:800;color:#34D399">${'$'}{onTime}</div>
                        <div style="font-size:11px;color:var(--text-muted)">Tepat Waktu</div>
                    </div>
                    <div style="background:#111C30;border:1px solid rgba(245,158,11,0.3);border-radius:10px;padding:10px;text-align:center">
                        <div style="font-size:18px;font-weight:800;color:#FBBF24">${'$'}{late}</div>
                        <div style="font-size:11px;color:var(--text-muted)">Terlambat</div>
                    </div>
                    <div style="background:#111C30;border:1px solid rgba(56,189,248,0.3);border-radius:10px;padding:10px;text-align:center">
                        <div style="font-size:18px;font-weight:800;color:#7DD3FC">${'$'}{checkOut}</div>
                        <div style="font-size:11px;color:var(--text-muted)">Sudah Pulang</div>
                    </div>
                `;
            }

            if (!records || records.length === 0) {
                container.innerHTML = '<p style="color:var(--text-muted); padding:10px">Belum ada catatan presensi pada filter ini.</p>';
                return;
            }

            let html = `
            <table style="width:100%;border-collapse:collapse;font-size:13px;text-align:left">
                <thead>
                    <tr style="border-bottom:1px solid var(--border);color:var(--text-muted)">
                        <th style="padding:10px 8px">No</th>
                        <th style="padding:10px 8px">Nama Pegawai</th>
                        <th style="padding:10px 8px">Jabatan / Role</th>
                        <th style="padding:10px 8px">Tanggal</th>
                        <th style="padding:10px 8px">Jam Masuk</th>
                        <th style="padding:10px 8px">Status Masuk</th>
                        <th style="padding:10px 8px">Jam Pulang</th>
                    </tr>
                </thead>
                <tbody>
            `;

            records.forEach((r, idx) => {
                const statusBadge = (r.statusIn || '').includes('TEPAT WAKTU')
                    ? '<span style="background:rgba(16,185,129,0.2);color:#34D399;padding:3px 8px;border-radius:6px;font-weight:700;font-size:11px">TEPAT WAKTU</span>'
                    : ((r.statusIn || '').includes('TERLAMBAT')
                        ? '<span style="background:rgba(245,158,11,0.2);color:#FBBF24;padding:3px 8px;border-radius:6px;font-weight:700;font-size:11px">TERLAMBAT</span>'
                        : '<span style="background:rgba(148,163,184,0.2);color:#94A3B8;padding:3px 8px;border-radius:6px;font-weight:700;font-size:11px">' + escapeHtml(r.statusIn || '-') + '</span>');

                html += `
                <tr style="border-bottom:1px solid rgba(51,65,85,0.4)">
                    <td style="padding:10px 8px;color:var(--text-muted)">${'$'}{idx + 1}</td>
                    <td style="padding:10px 8px;font-weight:700;color:#fff">${'$'}{escapeHtml(r.employeeName)}</td>
                    <td style="padding:10px 8px;color:var(--text-muted)">${'$'}{escapeHtml(r.department)} • ${'$'}{escapeHtml(r.role)}</td>
                    <td style="padding:10px 8px;color:#94A3B8;font-family:monospace">${'$'}{escapeHtml(r.date || '-')}</td>
                    <td style="padding:10px 8px;color:#38BDF8;font-family:monospace;font-weight:600">${'$'}{escapeHtml(r.timeIn || '-')}</td>
                    <td style="padding:10px 8px">${'$'}{statusBadge}</td>
                    <td style="padding:10px 8px;color:#7DD3FC;font-family:monospace;font-weight:600">${'$'}{escapeHtml(r.timeOut || '-')}</td>
                </tr>`;
            });

            html += `</tbody></table>`;
            container.innerHTML = html;
        }

        function exportAttendanceCsv() {
            let url = '/api/attendance/export-csv';
            if (currentAttendanceMode === 'monthly') {
                const month = document.getElementById('attendance-filter-month').value || new Date().toISOString().substring(0, 7);
                url += '?mode=month&month=' + encodeURIComponent(month);
            } else if (currentAttendanceMode === 'all') {
                url += '?mode=all';
            } else {
                const date = document.getElementById('attendance-filter-date').value || new Date().toISOString().split('T')[0];
                url += '?mode=daily&date=' + encodeURIComponent(date);
            }
            window.location.href = url;
        }

        function downloadAttendanceTemplate() {
            window.location.href = '/api/attendance/template-csv';
        }

        function handleAttendanceCsvUpload(input) {
            if (!input.files || !input.files[0]) return;
            const file = input.files[0];
            const reader = new FileReader();
            reader.onload = async function(e) {
                try {
                    const res = await fetch('/api/attendance/import-csv', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ csvData: e.target.result })
                    });
                    const data = await res.json();
                    if (res.ok && data.success) {
                        showToast(data.message || "Presensi berhasil diimpor!");
                        loadAttendanceList();
                    } else {
                        alert("Gagal impor CSV presensi: " + (data.message || "Format tidak sesuai"));
                    }
                } catch (err) {
                    alert("Error saat impor CSV presensi: " + err.message);
                }
                input.value = "";
            };
            reader.readAsText(file, "UTF-8");
        }

        function escapeHtml(str) {
            if (!str) return '';
            return str.replace(/[&<>"']/g, function(m) {
                return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[m];
            });
        }

        window.addEventListener('DOMContentLoaded', () => {
            loadAllData();
            loadCitiesList();
        });
    </script>
</body>
</html>
        """.trimIndent()
    }
}
