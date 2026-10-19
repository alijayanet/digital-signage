package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow


@Dao
interface SignageDao {

    // Config
    @Query("SELECT * FROM signage_config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<SignageConfig?>

    @Query("SELECT * FROM signage_config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): SignageConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: SignageConfig)

    @Update
    suspend fun updateConfig(config: SignageConfig): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertConfig(config: SignageConfig): Long

    // Slides
    @Query("SELECT * FROM slide_items ORDER BY displayOrder ASC, id ASC")
    fun getAllSlidesFlow(): Flow<List<SlideItem>>

    @Query("SELECT * FROM slide_items WHERE isActive = 1 ORDER BY displayOrder ASC, id ASC")
    fun getActiveSlidesFlow(): Flow<List<SlideItem>>

    @Query("SELECT * FROM slide_items ORDER BY displayOrder ASC, id ASC")
    suspend fun getAllSlides(): List<SlideItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlide(slide: SlideItem): Long

    @Update
    suspend fun updateSlide(slide: SlideItem)

    @Query("DELETE FROM slide_items WHERE id = :id")
    suspend fun deleteSlide(id: Long)

    @Query("DELETE FROM slide_items")
    suspend fun clearAllSlides()

    // Agendas
    @Query("SELECT * FROM agenda_items ORDER BY displayOrder ASC, id ASC")
    fun getAllAgendasFlow(): Flow<List<AgendaItem>>

    @Query("SELECT * FROM agenda_items WHERE isActive = 1 ORDER BY displayOrder ASC, id ASC")
    fun getActiveAgendasFlow(): Flow<List<AgendaItem>>

    @Query("SELECT * FROM agenda_items ORDER BY displayOrder ASC, id ASC")
    suspend fun getAllAgendas(): List<AgendaItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgenda(agenda: AgendaItem): Long

    @Update
    suspend fun updateAgenda(agenda: AgendaItem)

    @Query("DELETE FROM agenda_items WHERE id = :id")
    suspend fun deleteAgenda(id: Long)

    @Query("DELETE FROM agenda_items")
    suspend fun clearAllAgendas()

    // Notices
    @Query("SELECT * FROM notice_items ORDER BY displayOrder ASC, id ASC")
    fun getAllNoticesFlow(): Flow<List<NoticeItem>>

    @Query("SELECT * FROM notice_items WHERE isActive = 1 ORDER BY displayOrder ASC, id ASC")
    fun getActiveNoticesFlow(): Flow<List<NoticeItem>>

    @Query("SELECT * FROM notice_items ORDER BY displayOrder ASC, id ASC")
    suspend fun getAllNotices(): List<NoticeItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeItem): Long

    @Update
    suspend fun updateNotice(notice: NoticeItem)

    @Query("DELETE FROM notice_items WHERE id = :id")
    suspend fun deleteNotice(id: Long)

    @Query("DELETE FROM notice_items")
    suspend fun clearAllNotices()

    // Running Ticker
    @Query("SELECT * FROM running_ticker_items ORDER BY displayOrder ASC, id ASC")
    fun getAllTickersFlow(): Flow<List<RunningTickerItem>>

    @Query("SELECT * FROM running_ticker_items WHERE isActive = 1 ORDER BY displayOrder ASC, id ASC")
    fun getActiveTickersFlow(): Flow<List<RunningTickerItem>>

    @Query("SELECT * FROM running_ticker_items ORDER BY displayOrder ASC, id ASC")
    suspend fun getAllTickers(): List<RunningTickerItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicker(ticker: RunningTickerItem): Long

    @Update
    suspend fun updateTicker(ticker: RunningTickerItem)

    @Query("DELETE FROM running_ticker_items WHERE id = :id")
    suspend fun deleteTicker(id: Long)

    @Query("DELETE FROM running_ticker_items")
    suspend fun clearAllTickers()

    // Employees
    @Query("SELECT * FROM employee_items ORDER BY name ASC")
    fun getAllEmployeesFlow(): Flow<List<EmployeeItem>>

    @Query("SELECT * FROM employee_items WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveEmployeesFlow(): Flow<List<EmployeeItem>>

    @Query("SELECT * FROM employee_items ORDER BY name ASC")
    suspend fun getAllEmployees(): List<EmployeeItem>

    @Query("SELECT * FROM employee_items WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: Long): EmployeeItem?

    @Query("SELECT * FROM employee_items WHERE LOWER(TRIM(rfidUid)) = LOWER(TRIM(:rfidUid)) AND isActive = 1 LIMIT 1")
    suspend fun getEmployeeByRfid(rfidUid: String): EmployeeItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeItem): Long

    @Update
    suspend fun updateEmployee(employee: EmployeeItem)

    @Query("DELETE FROM employee_items WHERE id = :id")
    suspend fun deleteEmployee(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<EmployeeItem>): List<Long>

    // Attendance Records
    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY id DESC")
    fun getAttendanceRecordsByDateFlow(date: String): Flow<List<AttendanceRecordItem>>

    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY id DESC")
    suspend fun getAttendanceRecordsByDate(date: String): List<AttendanceRecordItem>

    @Query("SELECT * FROM attendance_records WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC, id DESC")
    suspend fun getAttendanceRecordsByMonth(monthPrefix: String): List<AttendanceRecordItem>

    @Query("SELECT * FROM attendance_records ORDER BY date DESC, id DESC")
    suspend fun getAllAttendanceRecords(): List<AttendanceRecordItem>

    @Query("SELECT * FROM attendance_records WHERE employeeId = :employeeId AND date = :date LIMIT 1")
    suspend fun getAttendanceByEmployeeAndDate(employeeId: Long, date: String): AttendanceRecordItem?

    @Query("SELECT * FROM attendance_records ORDER BY date DESC, id DESC LIMIT 500")
    suspend fun getRecentAttendanceRecords(): List<AttendanceRecordItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecordItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecordItem>): List<Long>

    @Update
    suspend fun updateAttendanceRecord(record: AttendanceRecordItem)

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteAttendanceRecord(id: Long)

    @Query("DELETE FROM attendance_records")
    suspend fun clearAllAttendanceRecords()
}


