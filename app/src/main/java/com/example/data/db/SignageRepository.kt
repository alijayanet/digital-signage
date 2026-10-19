package com.example.data.db

import android.content.Context
import com.example.data.entity.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignageRepository(
    private val dao: SignageDao,
    private val context: Context
) {

    val configFlow: Flow<SignageConfig?> = dao.getConfigFlow()
    val allSlidesFlow: Flow<List<SlideItem>> = dao.getAllSlidesFlow()
    val activeSlidesFlow: Flow<List<SlideItem>> = dao.getActiveSlidesFlow()
    val allAgendasFlow: Flow<List<AgendaItem>> = dao.getAllAgendasFlow()
    val activeAgendasFlow: Flow<List<AgendaItem>> = dao.getActiveAgendasFlow()
    val allNoticesFlow: Flow<List<NoticeItem>> = dao.getAllNoticesFlow()
    val activeNoticesFlow: Flow<List<NoticeItem>> = dao.getActiveNoticesFlow()
    val allTickersFlow: Flow<List<RunningTickerItem>> = dao.getAllTickersFlow()
    val activeTickersFlow: Flow<List<RunningTickerItem>> = dao.getActiveTickersFlow()
    val allEmployeesFlow: Flow<List<EmployeeItem>> = dao.getAllEmployeesFlow()
    val activeEmployeesFlow: Flow<List<EmployeeItem>> = dao.getActiveEmployeesFlow()

    fun getAttendanceRecordsByDateFlow(date: String): Flow<List<AttendanceRecordItem>> = dao.getAttendanceRecordsByDateFlow(date)

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val existingConfig = dao.getConfig()
        val existingSlides = dao.getAllSlides()
        if (existingConfig == null || existingSlides.isEmpty()) {
            val schoolPreset = DefaultDataSeeder.getSchoolPreset()
            applyPreset(schoolPreset)
        }
        val existingEmployees = dao.getAllEmployees()
        if (existingEmployees.isEmpty()) {
            DefaultDataSeeder.getDefaultEmployees().forEach { dao.insertEmployee(it) }
        }
        val existingNotices = dao.getAllNotices()
        if (existingNotices.isEmpty()) {
            DefaultDataSeeder.getSchoolPreset().notices.forEach { dao.insertNotice(it) }
        }
    }

    suspend fun applyPreset(preset: PresetData) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateConfig(preset.config)
        dao.clearAllSlides()
        preset.slides.forEach { dao.insertSlide(it) }
        dao.clearAllAgendas()
        preset.agendas.forEach { dao.insertAgenda(it) }
        dao.clearAllNotices()
        preset.notices.forEach { dao.insertNotice(it) }
        dao.clearAllTickers()
        preset.tickers.forEach { dao.insertTicker(it) }
    }

    suspend fun getConfig(): SignageConfig {
        return withContext(Dispatchers.IO) {
            dao.getConfig() ?: DefaultDataSeeder.getSchoolPreset().config
        }
    }

    suspend fun updateConfig(config: SignageConfig) = withContext(Dispatchers.IO) {
        val safeConfig = config.copy(id = 1)
        val rows = dao.updateConfig(safeConfig)
        if (rows == 0) {
            dao.insertOrUpdateConfig(safeConfig)
        }
    }

    suspend fun setFlashAlert(message: String) = withContext(Dispatchers.IO) {
        val current = getConfig()
        val updated = current.copy(
            activeFlashAlert = message,
            flashAlertTimestamp = if (message.isNotBlank()) System.currentTimeMillis() else 0L
        )
        dao.insertOrUpdateConfig(updated)
    }

    // Slides
    suspend fun getAllSlides(): List<SlideItem> = withContext(Dispatchers.IO) { dao.getAllSlides() }
    suspend fun addSlide(slide: SlideItem) = withContext(Dispatchers.IO) { dao.insertSlide(slide) }
    suspend fun updateSlide(slide: SlideItem) = withContext(Dispatchers.IO) { dao.updateSlide(slide) }
    suspend fun deleteSlide(id: Long) = withContext(Dispatchers.IO) { dao.deleteSlide(id) }

    // Agendas
    suspend fun getAllAgendas(): List<AgendaItem> = withContext(Dispatchers.IO) { dao.getAllAgendas() }
    suspend fun addAgenda(agenda: AgendaItem) = withContext(Dispatchers.IO) { dao.insertAgenda(agenda) }
    suspend fun updateAgenda(agenda: AgendaItem) = withContext(Dispatchers.IO) { dao.updateAgenda(agenda) }
    suspend fun deleteAgenda(id: Long) = withContext(Dispatchers.IO) { dao.deleteAgenda(id) }

    // Notices
    suspend fun getAllNotices(): List<NoticeItem> = withContext(Dispatchers.IO) { dao.getAllNotices() }
    suspend fun addNotice(notice: NoticeItem) = withContext(Dispatchers.IO) { dao.insertNotice(notice) }
    suspend fun updateNotice(notice: NoticeItem) = withContext(Dispatchers.IO) { dao.updateNotice(notice) }
    suspend fun deleteNotice(id: Long) = withContext(Dispatchers.IO) { dao.deleteNotice(id) }

    // Tickers
    suspend fun getAllTickers(): List<RunningTickerItem> = withContext(Dispatchers.IO) { dao.getAllTickers() }
    suspend fun addTicker(ticker: RunningTickerItem) = withContext(Dispatchers.IO) { dao.insertTicker(ticker) }
    suspend fun updateTicker(ticker: RunningTickerItem) = withContext(Dispatchers.IO) { dao.updateTicker(ticker) }
    suspend fun deleteTicker(id: Long) = withContext(Dispatchers.IO) { dao.deleteTicker(id) }

    // Employees
    suspend fun getAllEmployees(): List<EmployeeItem> = withContext(Dispatchers.IO) { dao.getAllEmployees() }
    suspend fun getEmployeeById(id: Long): EmployeeItem? = withContext(Dispatchers.IO) { dao.getEmployeeById(id) }
    suspend fun getEmployeeByRfid(rfidUid: String): EmployeeItem? = withContext(Dispatchers.IO) { dao.getEmployeeByRfid(rfidUid) }
    suspend fun addEmployee(employee: EmployeeItem): Long = withContext(Dispatchers.IO) { dao.insertEmployee(employee) }
    suspend fun updateEmployee(employee: EmployeeItem) = withContext(Dispatchers.IO) { dao.updateEmployee(employee) }
    suspend fun deleteEmployee(id: Long) = withContext(Dispatchers.IO) { dao.deleteEmployee(id) }

    // Attendance Records
    suspend fun getAttendanceRecordsByDate(date: String): List<AttendanceRecordItem> = withContext(Dispatchers.IO) { dao.getAttendanceRecordsByDate(date) }
    suspend fun getAttendanceRecordsByMonth(monthPrefix: String): List<AttendanceRecordItem> = withContext(Dispatchers.IO) { dao.getAttendanceRecordsByMonth(monthPrefix) }
    suspend fun getAllAttendanceRecords(): List<AttendanceRecordItem> = withContext(Dispatchers.IO) { dao.getAllAttendanceRecords() }
    suspend fun getRecentAttendanceRecords(): List<AttendanceRecordItem> = withContext(Dispatchers.IO) { dao.getRecentAttendanceRecords() }
    suspend fun deleteAttendanceRecord(id: Long) = withContext(Dispatchers.IO) { dao.deleteAttendanceRecord(id) }
    suspend fun clearAllAttendanceRecords() = withContext(Dispatchers.IO) { dao.clearAllAttendanceRecords() }

    suspend fun importEmployees(employees: List<EmployeeItem>): Int = withContext(Dispatchers.IO) {
        var count = 0
        employees.forEach { emp ->
            if (emp.name.isNotBlank() && emp.rfidUid.isNotBlank()) {
                val existing = dao.getEmployeeByRfid(emp.rfidUid)
                if (existing != null) {
                    dao.updateEmployee(emp.copy(id = existing.id))
                } else {
                    dao.insertEmployee(emp)
                }
                count++
            }
        }
        count
    }

    suspend fun importAttendanceRecords(records: List<AttendanceRecordItem>): Int = withContext(Dispatchers.IO) {
        var count = 0
        records.forEach { rec ->
            if (rec.rfidUid.isNotBlank() && rec.date.isNotBlank()) {
                val existing = if (rec.employeeId > 0) dao.getAttendanceByEmployeeAndDate(rec.employeeId, rec.date) else null
                if (existing != null) {
                    dao.updateAttendanceRecord(rec.copy(id = existing.id))
                } else {
                    dao.insertAttendanceRecord(rec)
                }
                count++
            }
        }
        count
    }

    suspend fun updateEmployeeRoles(rolesString: String) = withContext(Dispatchers.IO) {
        val current = getConfig()
        val updated = current.copy(employeeRoles = rolesString.trim())
        updateConfig(updated)
    }


    // Process RFID Attendance Scan
    suspend fun processAttendanceScan(rawUid: String): AttendanceScanResult = withContext(Dispatchers.IO) {
        val cleanUid = rawUid.trim()
        val employee = dao.getEmployeeByRfid(cleanUid)
            ?: return@withContext AttendanceScanResult.UnknownCard(cleanUid)

        val cfg = getConfig()
        val now = java.util.Date()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val timeFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        val hhmmFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())

        val todayDate = dateFormat.format(now)
        val currentTime = timeFormat.format(now)
        val currentHHmm = hhmmFormat.format(now)

        val checkOutThreshold = cfg.checkOutStart.ifBlank { "14:00" }
        val lateLimit = cfg.checkInLateLimit.ifBlank { "07:30" }

        val isCheckOutWindow = currentHHmm >= checkOutThreshold
        val existingRecord = dao.getAttendanceByEmployeeAndDate(employee.id, todayDate)

        if (!isCheckOutWindow) {
            // === CHECK-IN (MASUK) ===
            if (existingRecord != null && existingRecord.timeIn.isNotBlank()) {
                AttendanceScanResult.AlreadyRecorded(
                    employee = employee,
                    isCheckIn = true,
                    existingTime = existingRecord.timeIn
                )
            } else {
                val statusIn = if (currentHHmm <= lateLimit) "HADIR TEPAT WAKTU" else "TERLAMBAT"
                val record = existingRecord?.copy(
                    timeIn = currentTime,
                    statusIn = statusIn
                ) ?: AttendanceRecordItem(
                    employeeId = employee.id,
                    rfidUid = cleanUid,
                    employeeName = employee.name,
                    department = employee.department,
                    role = employee.role,
                    date = todayDate,
                    timeIn = currentTime,
                    timeOut = "",
                    statusIn = statusIn,
                    statusOut = "",
                    photoUrl = employee.photoUrl
                )

                if (existingRecord != null) {
                    dao.updateAttendanceRecord(record)
                } else {
                    dao.insertAttendanceRecord(record)
                }

                AttendanceScanResult.Success(
                    employee = employee,
                    isCheckIn = true,
                    statusText = statusIn,
                    timeFormatted = "$currentTime WIB"
                )
            }
        } else {
            // === CHECK-OUT (PULANG) ===
            if (existingRecord != null && existingRecord.timeOut.isNotBlank()) {
                AttendanceScanResult.AlreadyRecorded(
                    employee = employee,
                    isCheckIn = false,
                    existingTime = existingRecord.timeOut
                )
            } else {
                val record = existingRecord?.copy(
                    timeOut = currentTime,
                    statusOut = "PULANG"
                ) ?: AttendanceRecordItem(
                    employeeId = employee.id,
                    rfidUid = cleanUid,
                    employeeName = employee.name,
                    department = employee.department,
                    role = employee.role,
                    date = todayDate,
                    timeIn = "-",
                    timeOut = currentTime,
                    statusIn = "TIDAK MASUK",
                    statusOut = "PULANG",
                    photoUrl = employee.photoUrl
                )

                if (existingRecord != null) {
                    dao.updateAttendanceRecord(record)
                } else {
                    dao.insertAttendanceRecord(record)
                }

                AttendanceScanResult.Success(
                    employee = employee,
                    isCheckIn = false,
                    statusText = "PRESENSI PULANG",
                    timeFormatted = "$currentTime WIB"
                )
            }
        }
    }
}

