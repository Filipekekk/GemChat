/**
 * GemChatApplication.kt to główna klasa aplikacji, uruchamiana jako pierwsza.
 * Służy do inicjalizacji globalnych obiektów, takich jak baza danych i repozytorium.
 */
package com.gemchat.app

import android.app.Application
import com.gemchat.app.data.GemChatDatabase
import com.gemchat.app.data.repository.ChatRepository

class GemChatApplication : Application() {
    /** Leniwa inicjalizacja bazy danych Room. */
    val database by lazy { GemChatDatabase.getDatabase(this) }
    
    /** Leniwa inicjalizacja repozytorium dostępnego dla całej aplikacji. */
    val repository by lazy { ChatRepository(database.chatDao()) }
}