/**
 * ChatRepository stanowi warstwę abstrakcji między źródłem danych (ChatDao) a interfejsem użytkownika.
 * Agreguje operacje na lokalnej bazie danych Room.
 */
package com.gemchat.app.data.repository

import com.gemchat.app.data.dao.ChatDao
import com.gemchat.app.data.model.Conversation
import com.gemchat.app.data.model.Message
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {

    // --- ZARZĄDZANIE KONWERSACJAMI ---

    /** Pobiera strumień (Flow) wszystkich dostępnych rozmów. */
    fun getAllConversations(): Flow<List<Conversation>> =
        chatDao.getAllConversations()

    /** Tworzy nową rozmowę z podanym tytułem. */
    suspend fun insertConversation(title: String): Long {
        val conversation = Conversation(title = title)
        return chatDao.insertConversation(conversation)
    }

    /** Usuwa rozmowę (wyzwala kaskadowe usuwanie wiadomości). */
    suspend fun deleteConversation(conversation: Conversation) {
        chatDao.deleteConversation(conversation)
    }

    /** Aktualizuje informację o tym, co było ostatnio wysłane w danym czacie. */
    suspend fun updateLastMessage(conversationId: Long, lastMessage: String) {
        chatDao.updateConversationLastMessage(
            id = conversationId,
            lastMessage = lastMessage,
            timestamp = System.currentTimeMillis()
        )
    }

    /** Aktualizuje tytuł konwersacji (np. po wysłaniu pierwszej wiadomości). */
    suspend fun updateConversationTitle(conversationId: Long, title: String) {
        chatDao.updateConversationTitle(conversationId, title)
    }

    // --- ZARZĄDZANIE WIADOMOŚCIAMI ---

    /** Pobiera strumień wiadomości przypisanych do konkretnej rozmowy. */
    fun getMessagesForConversation(conversationId: Long): Flow<List<Message>> =
        chatDao.getMessagesForConversation(conversationId)

    /** Wstawia nową wiadomość do bazy, opcjonalnie ze ścieżką do zdjęcia. */
    suspend fun insertMessage(
        conversationId: Long,
        text: String,
        isFromUser: Boolean,
        imagePath: String? = null
    ): Long {
        val message = Message(
            conversationId = conversationId,
            text = text,
            isFromUser = isFromUser,
            localImagePath = imagePath
        )
        return chatDao.insertMessage(message)
    }

    // --- INNE ---

    /** Całkowite wyczyszczenie bazy danych (np. przy wylogowaniu/resecie). */
    suspend fun deleteAllData() {
        chatDao.deleteAllMessages()
        chatDao.deleteAllConversations()
    }
}