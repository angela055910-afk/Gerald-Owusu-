package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CallRecord
import com.example.data.model.Chat
import com.example.data.model.Contact
import com.example.data.model.Message
import com.example.data.model.StatusUpdate

@Database(
    entities = [
        Chat::class,
        Message::class,
        StatusUpdate::class,
        CallRecord::class,
        Contact::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JungleDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: JungleDatabase? = null

        fun getDatabase(context: Context): JungleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JungleDatabase::class.java,
                    "jungle_encrypted.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
