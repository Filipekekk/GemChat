package com.gemchat.app.data.repository

import com.gemchat.app.data.dao.ChatDao
import com.gemchat.app.data.model.Conversation
import com.gemchat.app.data.model.Message
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {

    // Conversations
    fun getAllConversations(): Flow<List<Conversation>> =
        chatDao.getAllConversations()

    suspend fun insertConversation(title: String): Long {
        val conversation = Conversation(title = title)
        return chatDao.insertConversation(conversation)
    }

    suspend fun deleteConversation(conversation: Conversation) {
        chatDao.deleteConversation(conversation)
    }

    suspend fun updateLastMessage(conversationId: Long, lastMessage: String) {
        chatDao.updateConversationLastMessage(
            id = conversationId,
            lastMessage = lastMessage,
            timestamp = System.currentTimeMillis()
        )
    }

    // Messages
    fun getMessagesForConversation(conversationId: Long): Flow<List<Message>> =
        chatDao.getMessagesForConversation(conversationId)

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
    suspend fun deleteAllData() {
        chatDao.deleteAllMessages()
        chatDao.deleteAllConversations()
    }
    suspend fun updateConversationTitle(conversationId: Long, title: String) {
        chatDao.updateConversationTitle(conversationId, title)
    }
}