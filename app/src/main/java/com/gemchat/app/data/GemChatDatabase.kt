package com.gemchat.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gemchat.app.data.dao.ChatDao
import com.gemchat.app.data.model.Conversation
import com.gemchat.app.data.model.Message

@Database(
    entities = [Conversation::class, Message::class],
    version = 1,
    exportSchema = false
)
abstract class GemChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: GemChatDatabase? = null

        fun getDatabase(context: Context): GemChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GemChatDatabase::class.java,
                    "gemchat_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}