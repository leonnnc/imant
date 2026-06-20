package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CustomDesign::class,
        Order::class,
        SupportMessage::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MagnetDatabase : RoomDatabase() {
    abstract fun magnetDao(): MagnetDao

    companion object {
        @Volatile
        private var INSTANCE: MagnetDatabase? = null

        fun getDatabase(context: Context): MagnetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MagnetDatabase::class.java,
                    "magnet_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
