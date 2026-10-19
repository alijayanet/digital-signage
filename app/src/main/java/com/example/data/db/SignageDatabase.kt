package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.entity.*


@Database(
    entities = [
        SignageConfig::class,
        SlideItem::class,
        AgendaItem::class,
        NoticeItem::class,
        RunningTickerItem::class,
        EmployeeItem::class,
        AttendanceRecordItem::class
    ],
    version = 6,
    exportSchema = false
)
abstract class SignageDatabase : RoomDatabase() {
    abstract fun signageDao(): SignageDao

    companion object {
        @Volatile
        private var INSTANCE: SignageDatabase? = null

        fun getDatabase(context: Context): SignageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SignageDatabase::class.java,
                    "digital_signage.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
