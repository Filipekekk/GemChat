package com.gemchat.app

import android.app.Application
import com.gemchat.app.data.GemChatDatabase
import com.gemchat.app.data.repository.ChatRepository

class GemChatApplication : Application() {
    val database by lazy { GemChatDatabase.getDatabase(this) }
    val repository by lazy { ChatRepository(database.chatDao()) }
}