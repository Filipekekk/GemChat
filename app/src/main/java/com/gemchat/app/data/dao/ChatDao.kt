/**
 * Interfejs ChatDao (Data Access Object) definiuje metody dostępu do danych w bazie SQLite.
 * Room implementuje te metody automatycznie na podstawie adnotacji SQL.
 */
package com.gemchat.app.data.dao

import androidx.room.*
import com.gemchat.app.data.model.Conversation
import com.gemchat.app.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // --- OPERACJE NA KONWERSACJACH ---

    /** Pobiera wszystkie konwersacje posortowane od najnowszych. Używa Flow do automatycznego odświeżania UI. */
    @Query("SELECT * FROM conversations ORDER BY timestamp DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    /** Wstawia nową konwersację lub zastępuje istniejącą w razie konfliktu ID. Zwraca ID wstawionego wiersza. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation): Long

    /** Usuwa konkretną konwersację z bazy (wiadomości zostaną usunięte kaskadowo). */
    @Delete
    suspend fun deleteConversation(conversation: Conversation)

    /** Aktualizuje skrót ostatniej wiadomości i czas aktywności dla konkretnego czatu. */
    @Query("UPDATE conversations SET lastMessage = :lastMessage, timestamp = :timestamp WHERE id = :id")
    suspend fun updateConversationLastMessage(id: Long, lastMessage: String, timestamp: Long)

    /** Zmienia tytuł konwersacji (np. przy wysłaniu pierwszej wiadomości). */
    @Query("UPDATE conversations SET title = :title WHERE id = :id")
    suspend fun updateConversationTitle(id: Long, title: String)

    /** Czyści całą tabelę konwersacji. */
    @Query("DELETE FROM conversations")
    suspend fun deleteAllConversations()

    // --- OPERACJE NA WIADOMOŚCIACH ---

    /** Pobiera wszystkie wiadomości dla danej konwersacji chronologicznie. */
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<Message>>

    /** Zapisuje nową wiadomość (wysłaną przez użytkownika lub AI) w bazie. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long

    /** Usuwa wszystkie wiadomości należące do konkretnej konwersacji. */
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: Long)

    /** Czyści całą tabelę wiadomości. */
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}