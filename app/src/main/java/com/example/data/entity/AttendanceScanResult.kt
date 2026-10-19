package com.example.data.entity

sealed class AttendanceScanResult {
    data class Success(
        val employee: EmployeeItem,
        val isCheckIn: Boolean,
        val statusText: String, // "HADIR TEPAT WAKTU", "TERLAMBAT", "PULANG"
        val timeFormatted: String, // "07:15:20 WIB"
        val timestamp: Long = System.currentTimeMillis()
    ) : AttendanceScanResult()

    data class AlreadyRecorded(
        val employee: EmployeeItem,
        val isCheckIn: Boolean,
        val existingTime: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : AttendanceScanResult()

    data class UnknownCard(
        val rfidUid: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : AttendanceScanResult()
}
